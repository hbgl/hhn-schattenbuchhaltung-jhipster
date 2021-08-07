package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import dev.hbgl.hhn.schattenbuchhaltung.domain.Comment;
import dev.hbgl.hhn.schattenbuchhaltung.domain.CostCenter;
import dev.hbgl.hhn.schattenbuchhaltung.domain.CostType;
import dev.hbgl.hhn.schattenbuchhaltung.domain.Division;
import dev.hbgl.hhn.schattenbuchhaltung.domain.LedgerEntry;
import dev.hbgl.hhn.schattenbuchhaltung.domain.Tag;
import dev.hbgl.hhn.schattenbuchhaltung.repository.CostCenterRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.CostTypeRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.DivisionRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.LedgerEntryRepository;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.CostCenterDTO;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger.CommentOut;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger.LedgerEntryOut;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.LedgerImportEntryDTO;
import dev.hbgl.hhn.schattenbuchhaltung.service.parser.LedgerEntryInstantParser;
import dev.hbgl.hhn.schattenbuchhaltung.web.rest.errors.BadRequestAlertException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link dev.hbgl.hhn.schattenbuchhaltung.domain.LedgerEntry}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LedgerResource {

    private final Logger log = LoggerFactory.getLogger(LedgerEntryResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LedgerEntryRepository ledgerEntryRepository;

    private final CostCenterRepository costCenterRepository;

    private final DivisionRepository divisionRepository;

    private final CostTypeRepository costTypeRepository;

    private final LedgerEntryInstantParser ledgerEntryInstantParser;

    private final EntityManager entityManager;

    public LedgerResource(
        LedgerEntryRepository ledgerEntryRepository,
        CostCenterRepository costCenterRepository,
        DivisionRepository divisionRepository,
        CostTypeRepository costTypeRepository,
        LedgerEntryInstantParser ledgerEntryInstantParser,
        EntityManager entityManager
    ) {
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.costCenterRepository = costCenterRepository;
        this.divisionRepository = divisionRepository;
        this.costTypeRepository = costTypeRepository;
        this.ledgerEntryInstantParser = ledgerEntryInstantParser;
        this.entityManager = entityManager;
    }

    @GetMapping("/ledger")
    public List<LedgerEntryOut> listLedger() {
        log.debug("REST request to get all LedgerEntries");

        var ledgerEntryGraph = entityManager.createEntityGraph(LedgerEntry.class);
        ledgerEntryGraph.addAttributeNodes("costCenter1", "costCenter2", "costCenter3", "costType", "division");

        var result = entityManager
            .createQuery("SELECT le FROM LedgerEntry le ORDER BY bookingDate", LedgerEntry.class)
            .setHint("javax.persistence.loadgraph", ledgerEntryGraph)
            .getResultStream()
            .map(e -> LedgerEntryOut.fromEntity(e, this.entityManager))
            .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/ledger/entry/{no}")
    public ResponseEntity<LedgerEntryOut> getLedgerDetails(@PathVariable String no) {
        log.debug("REST request to get LedgerEntry by no : {}", no);

        var ledgerEntryGraph = entityManager.createEntityGraph(LedgerEntry.class);
        ledgerEntryGraph.addAttributeNodes("costCenter1", "costCenter2", "costCenter3", "costType", "division");
        ledgerEntryGraph.addSubgraph("tags").addAttributeNodes("customType", "customValue");

        var ledgerEntryMaybe = entityManager
            .createQuery("SELECT le FROM LedgerEntry le WHERE le.no = :no", LedgerEntry.class)
            .setParameter("no", no)
            .setHint("javax.persistence.loadgraph", ledgerEntryGraph)
            .getResultStream()
            .findFirst();

        if (ledgerEntryMaybe.isEmpty()) {
            return ResponseUtil.wrapOrNotFound(Optional.empty());
        }

        var ledgerEntry = ledgerEntryMaybe.get();

        var commentGraph = entityManager.createEntityGraph(Comment.class);
        commentGraph.addAttributeNodes("author");
        var comments = entityManager
            .createQuery("SELECT cm FROM Comment cm WHERE cm.ledgerEntry = :ledgerEntry ORDER BY cm.id DESC", Comment.class)
            .setParameter("ledgerEntry", ledgerEntry)
            .setHint("javax.persistence.loadgraph", commentGraph)
            .getResultList();

        var vm = LedgerEntryOut.fromEntity(ledgerEntry, this.entityManager);
        vm.comments = comments.stream().map(CommentOut::fromEntity).collect(Collectors.toList());

        return ResponseUtil.wrapOrNotFound(Optional.of(vm));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PostMapping("/ledger/import")
    public ResponseEntity<Void> importLedger(@Valid @RequestBody LedgerImportEntryDTO[] inputEntries)
        throws URISyntaxException, IOException {
        // TODO Add history entries
        // TODO Tests (also frontend)

        log.debug("REST request to import ledger entries");

        /**
         * Insert/ update cost centers.
         */
        var inputCostCenterMap = new HashMap<String, CostCenterDTO>();
        var inputCostCenters = new ArrayList<CostCenterDTO>();
        for (var dto : inputEntries) {
            var rank = 1;
            CostCenterDTO parent = null;
            for (var ccDto : (Iterable<CostCenterDTO>) Stream.of(dto.costCenter1, dto.costCenter2, dto.costCenter3)::iterator) {
                ccDto.index = -1;
                ccDto.rank = rank;
                if (parent != null) {
                    ccDto.parentNo = parent.no;
                }
                // If a cost center has multiple ranks, keep the highest one.
                var existingDto = inputCostCenterMap.get(ccDto.no);
                if (existingDto == null) {
                    ccDto.index = inputCostCenters.size();
                    inputCostCenters.add(ccDto);
                    inputCostCenterMap.put(ccDto.no, ccDto);
                } else if (ccDto.rank < existingDto.rank) {
                    // Update rank to the higher one.
                    ccDto.index = existingDto.index;
                    existingDto.index = -1;
                    inputCostCenters.set(ccDto.index, ccDto);
                    inputCostCenterMap.put(ccDto.no, ccDto);
                }
                rank++;
                parent = ccDto;
            }
        }
        // Sort cost centers such that the highest rank comes first,
        // i.e. parents come before their children.
        inputCostCenters.sort((a, b) -> a.rank - b.rank);

        var costCenters =
            this.costCenterRepository.findByNosWithParent(inputCostCenterMap.keySet())
                .stream()
                .collect(Collectors.toMap(CostCenter::getNo, Function.identity()));

        for (var dto : inputCostCenters) {
            var costCenter = costCenters.get(dto.no);
            if (costCenter == null) {
                costCenter = new CostCenter();
                costCenter.setNo(dto.no);
                costCenter.setRank(dto.rank);
                costCenter.setParent(costCenters.get(dto.parentNo));
                costCenters.put(dto.no, costCenter);
            } else {
                // Cannot update the rank and therefore the parent of a cost center.
                if (dto.rank != costCenter.getRank()) {
                    throw new BadRequestAlertException("Cannot change rank of cost center " + dto.no + ".", "costCenter", "rankinvalid");
                }
                if (!Objects.equals(dto.parentNo, costCenter.getParent() == null ? null : costCenter.getParent().getNo())) {
                    throw new BadRequestAlertException("Cannot re-parent cost center " + dto.no + ".", "costCenter", "invop");
                }
            }
            costCenter.setName(dto.name);
        }

        /**
         * Insert / update divisions.
         */
        var inputDivisions = Arrays
            .stream(inputEntries)
            .filter(e -> e.divisionNo != null)
            .collect(Collectors.toMap(e -> e.divisionNo, e -> e.divisionName, (a, b) -> a));

        var divisions =
            this.divisionRepository.findByNos(inputDivisions.keySet())
                .stream()
                .collect(Collectors.toMap(Division::getNo, Function.identity()));

        for (var entry : inputDivisions.entrySet()) {
            var division = divisions.get(entry.getKey());
            if (division == null) {
                division = new Division();
                division.setNo(entry.getKey());
                divisions.put(division.getNo(), division);
            }
            division.setName(entry.getValue());
        }

        /**
         * Insert / update cost types.
         */
        var inputCostTypes = Arrays
            .stream(inputEntries)
            .filter(e -> e.costTypeNo != null)
            .collect(Collectors.toMap(e -> e.costTypeNo, e -> e.costTypeName, (a, b) -> a));

        var costTypes =
            this.costTypeRepository.findByNos(inputCostTypes.keySet())
                .stream()
                .collect(Collectors.toMap(CostType::getNo, Function.identity()));

        for (var entry : inputCostTypes.entrySet()) {
            var costType = costTypes.get(entry.getKey());
            if (costType == null) {
                costType = new CostType();
                costType.setNo(entry.getKey());
                costTypes.put(costType.getNo(), costType);
            }
            costType.setName(entry.getValue());
        }

        /**
         * Insert / update ledger entries.
         */
        var inputNos = Arrays.stream(inputEntries).map(e -> e.no).collect(Collectors.toList());

        var ledgerEntries =
            this.ledgerEntryRepository.findByNos(inputNos).stream().collect(Collectors.toMap(LedgerEntry::getNo, Function.identity()));

        for (var inputEntry : inputEntries) {
            var ledgerEntry = ledgerEntries.get(inputEntry.no);
            if (ledgerEntry == null) {
                ledgerEntry = new LedgerEntry();
                ledgerEntry.setNo(inputEntry.no);
                ledgerEntries.put(ledgerEntry.getNo(), ledgerEntry);
            }
            ledgerEntry.setDescription(inputEntry.description);
            ledgerEntry.setaNo(inputEntry.aNo);
            ledgerEntry.setBookingDate(this.ledgerEntryInstantParser.parse(inputEntry.bookingDate));
            ledgerEntry.setIncome(inputEntry.income);
            ledgerEntry.setExpenditure(inputEntry.expenditure);
            ledgerEntry.setLiability(inputEntry.income);
            ledgerEntry.setCostCenter1(costCenters.get(inputEntry.costCenter1.no));
            ledgerEntry.setCostCenter2(costCenters.get(inputEntry.costCenter2.no));
            ledgerEntry.setCostCenter3(costCenters.get(inputEntry.costCenter3.no));
            ledgerEntry.setDivision(divisions.get(inputEntry.divisionNo));
            ledgerEntry.setCostType(costTypes.get(inputEntry.costTypeNo));
        }

        this.costCenterRepository.saveAll(costCenters.values());
        this.divisionRepository.saveAll(divisions.values());
        this.costTypeRepository.saveAll(costTypes.values());
        this.ledgerEntryRepository.saveAll(ledgerEntries.values());

        return ResponseEntity.noContent().build();
    }
}
