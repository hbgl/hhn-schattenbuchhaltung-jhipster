package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import dev.hbgl.hhn.schattenbuchhaltung.domain.Comment;
import dev.hbgl.hhn.schattenbuchhaltung.domain.CostCenter;
import dev.hbgl.hhn.schattenbuchhaltung.domain.CostType;
import dev.hbgl.hhn.schattenbuchhaltung.domain.Division;
import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntry;
import dev.hbgl.hhn.schattenbuchhaltung.domain.LedgerEntry;
import dev.hbgl.hhn.schattenbuchhaltung.domain.LedgerEntryTag;
import dev.hbgl.hhn.schattenbuchhaltung.domain.Tag;
import dev.hbgl.hhn.schattenbuchhaltung.domain.User;
import dev.hbgl.hhn.schattenbuchhaltung.domain.elasticsearch.ElasticTag;
import dev.hbgl.hhn.schattenbuchhaltung.repository.CostCenterRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.CostTypeRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.DivisionRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.HistoryEntryFieldRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.HistoryEntryRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.LedgerEntryRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.LedgerEntryTagRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.TagRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.UserRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.search.TagSearchRepository;
import dev.hbgl.hhn.schattenbuchhaltung.security.SecurityUtils;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.CostCenterDTO;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.Export.ExportHistoryEntryDTO;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.Export.ExportVersion;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.Export.ImportExportDTO;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger.CommentOut;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger.LedgerEntryOut;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger.TagOut;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.LedgerImportEntryDTO;
import dev.hbgl.hhn.schattenbuchhaltung.service.dto.UpdateLedgerEntryTagsDTO;
import dev.hbgl.hhn.schattenbuchhaltung.service.parser.LedgerEntryInstantParser;
import dev.hbgl.hhn.schattenbuchhaltung.support.collections.Collect;
import dev.hbgl.hhn.schattenbuchhaltung.web.rest.errors.BadRequestAlertException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link dev.hbgl.hhn.schattenbuchhaltung.domain.LedgerEntry}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LedgerResource {

    public static final String LEDGER_ENTRY_ENTITY_NAME = "ledgerEntry";

    private final Logger log = LoggerFactory.getLogger(LedgerResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LedgerEntryRepository ledgerEntryRepository;

    private final CostCenterRepository costCenterRepository;

    private final DivisionRepository divisionRepository;

    private final CostTypeRepository costTypeRepository;

    private final TagRepository tagRepository;

    private final TagSearchRepository tagSearchRepository;

    private LedgerEntryTagRepository ledgerEntryTagRepository;

    private HistoryEntryRepository historyEntryRepository;

    private HistoryEntryFieldRepository historyEntryFieldRepository;

    private final LedgerEntryInstantParser ledgerEntryInstantParser;

    private final EntityManager entityManager;

    private final UserRepository userRepository;

    public LedgerResource(
        LedgerEntryRepository ledgerEntryRepository,
        CostCenterRepository costCenterRepository,
        DivisionRepository divisionRepository,
        CostTypeRepository costTypeRepository,
        TagRepository tagRepository,
        TagSearchRepository tagSearchRepository,
        LedgerEntryTagRepository ledgerEntryTagRepository,
        HistoryEntryRepository historyEntryRepository,
        HistoryEntryFieldRepository historyEntryFieldRepository,
        LedgerEntryInstantParser ledgerEntryInstantParser,
        EntityManager entityManager,
        UserRepository userRepository
    ) {
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.costCenterRepository = costCenterRepository;
        this.divisionRepository = divisionRepository;
        this.costTypeRepository = costTypeRepository;
        this.tagRepository = tagRepository;
        this.tagSearchRepository = tagSearchRepository;
        this.ledgerEntryTagRepository = ledgerEntryTagRepository;
        this.historyEntryRepository = historyEntryRepository;
        this.historyEntryFieldRepository = historyEntryFieldRepository;
        this.ledgerEntryInstantParser = ledgerEntryInstantParser;
        this.entityManager = entityManager;
        this.userRepository = userRepository;
    }

    @GetMapping("/ledger")
    public List<LedgerEntryOut> listLedger() throws Exception {
        log.debug("REST request to get all LedgerEntries");

        var ledgerEntryGraph = entityManager.createEntityGraph(LedgerEntry.class);
        ledgerEntryGraph.addAttributeNodes("costCenter1", "costCenter2", "costCenter3", "costType", "division");

        var result = entityManager
            .createQuery("SELECT le FROM LedgerEntry le ORDER BY bookingDate", LedgerEntry.class)
            .setHint("javax.persistence.loadgraph", ledgerEntryGraph)
            .getResultStream()
            .map(e -> LedgerEntryOut.fromEntity(e, null, this.entityManager))
            .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/ledger/entry/{no}")
    public ResponseEntity<LedgerEntryOut> getLedgerDetails(@PathVariable String no) {
        log.debug("REST request to get LedgerEntry by no : {}", no);

        var ledgerEntryGraph = entityManager.createEntityGraph(LedgerEntry.class);
        ledgerEntryGraph.addAttributeNodes("costCenter1", "costCenter2", "costCenter3", "costType", "division");

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

        var ledgerEntryTagGraph = entityManager.createEntityGraph(LedgerEntryTag.class);
        ledgerEntryTagGraph.addSubgraph("tag");
        var ledgerEntryTags = entityManager
            .createQuery(
                "SELECT let FROM LedgerEntryTag let WHERE let.ledgerEntry = :ledgerEntry ORDER BY let.priority, let.id",
                LedgerEntryTag.class
            )
            .setParameter("ledgerEntry", ledgerEntry)
            .setHint("javax.persistence.loadgraph", ledgerEntryTagGraph)
            .getResultList();

        var commentGraph = entityManager.createEntityGraph(Comment.class);
        commentGraph.addAttributeNodes("author");
        var comments = entityManager
            .createQuery("SELECT cm FROM Comment cm WHERE cm.ledgerEntry = :ledgerEntry ORDER BY cm.id DESC", Comment.class)
            .setParameter("ledgerEntry", ledgerEntry)
            .setHint("javax.persistence.loadgraph", commentGraph)
            .getResultList();

        var relations = new LedgerEntryOut.Relations();
        relations.comments = comments;
        relations.ledgerEntryTags = ledgerEntryTags;

        var vm = LedgerEntryOut.fromEntity(ledgerEntry, relations, this.entityManager);
        vm.comments = comments.stream().map(CommentOut::fromEntity).collect(Collectors.toList());

        return ResponseUtil.wrapOrNotFound(Optional.of(vm));
    }

    @PutMapping("/ledger/entry/{no}/tags")
    public ResponseEntity<List<TagOut>> updateTags(@PathVariable String no, @Valid @RequestBody UpdateLedgerEntryTagsDTO input)
        throws Exception {
        log.debug("REST request to update tag of LedgerEntry by no : {}", no);

        var maybeLedgerEntry = ledgerEntryRepository.findByNo(no);
        if (maybeLedgerEntry.isEmpty()) {
            throw new BadRequestAlertException("Related ledger entry not found.", LEDGER_ENTRY_ENTITY_NAME, "notfound");
        }
        var ledgerEntry = maybeLedgerEntry.get();

        // List with actual changes.
        var saveTags = new ArrayList<Tag>();
        var insertTags = new ArrayList<Tag>();
        var updateTags = new ArrayList<Tag>();
        var saveLedgerEntryTags = new ArrayList<LedgerEntryTag>();
        var insertLedgerEntryTags = new ArrayList<LedgerEntryTag>();
        var updateLedgerEntryTags = new ArrayList<LedgerEntryTag>();
        var historyEntries = new ArrayList<HistoryEntry>();
        var userRef = entityManager.getReference(User.class, SecurityUtils.getCurrentUserId().get());

        // Existing tag IDs to assign to this ledger entry.
        var assignTags = new ArrayList<Tag>();
        var assignTagIndices = new HashMap<String, Integer>();
        for (var text : input.assignTags) {
            var tag = new Tag().text(text).normalized();
            var duplicateIndex = assignTagIndices.get(tag.getTextNormalized());
            if (duplicateIndex != null) {
                assignTags.set(duplicateIndex, tag);
            } else {
                assignTagIndices.put(tag.getTextNormalized(), assignTags.size());
                assignTags.add(tag);
            }
        }

        // Load existing tags from DB.
        var existingTags = tagRepository
            .findAllByNormalizedText(Collect.pluckToList(assignTags, t -> t.getTextNormalized()))
            .stream()
            .collect(Collectors.toMap(t -> t.getTextNormalized(), t -> t));

        // Check if tags need to be inserted or updated.
        var assignTagsSize = assignTags.size();
        for (var i = 0; i < assignTagsSize; i++) {
            var assignTag = assignTags.get(i);
            var existingTag = existingTags.get(assignTag.getTextNormalized());
            if (existingTag == null) {
                // Add new tag.
                saveTags.add(assignTag);
                insertTags.add(assignTag);
            } else {
                // Update existing tag if dirty.
                var historyEntry = Tag.historyEntryModify(existingTag, assignTag, userRef);
                if (historyEntry != null) {
                    // Add tag history entry for modification.
                    historyEntries.add(historyEntry);
                    existingTag.setText(assignTag.getText());
                    saveTags.add(existingTag);
                    updateTags.add(assignTag);
                }
                assignTags.set(i, existingTag);
            }
        }

        // Unassign tags.
        var unassignNormalizedTexts = Arrays.stream(input.unassignTags).map(text -> Tag.normalizeText(text)).collect(Collectors.toSet());
        if (!unassignNormalizedTexts.isEmpty()) {
            var unassignTags = tagRepository.findAllByNormalizedText(unassignNormalizedTexts);
            if (!unassignTags.isEmpty()) {
                var unassignTagIds = Collect.pluckToList(unassignTags, t -> t.getId());
                var unassignLedgerEntryTags = ledgerEntryTagRepository.findAllByLedgerAndTagId(ledgerEntry.getId(), unassignTagIds);

                historyEntries.addAll(
                    unassignLedgerEntryTags.stream().map(let -> LedgerEntryTag.historyEntryDelete(let, userRef)).toList()
                );

                ledgerEntryTagRepository.deleteAll(unassignLedgerEntryTags);
            }
        }

        // Upsert tag assignments.
        tagRepository.saveAll(saveTags);

        // Add tag history entries for inserts.
        historyEntries.addAll(insertTags.stream().map(t -> Tag.historyEntryCreate(t, userRef)).toList());

        // Set up ledger entry tags.
        var assignLedgerEntryTags = new ArrayList<LedgerEntryTag>();
        for (var i = 0; i < assignTagsSize; i++) {
            var ledgerEntryTag = new LedgerEntryTag()
                .ledgerEntry(ledgerEntry)
                .ledgerEntryNo(ledgerEntry.getNo())
                .tag(assignTags.get(i))
                .priority(i + 1);
            assignLedgerEntryTags.add(ledgerEntryTag);
        }

        // Load existing ledger entry tags.
        var assignTagIds = Collect.pluckToList(assignTags, t -> t.getId());
        var existingLedgerEntryTags = ledgerEntryTagRepository
            .findAllByLedgerAndTagId(ledgerEntry.getId(), assignTagIds)
            .stream()
            .collect(Collectors.toMap(let -> let.getTag().getId(), let -> let));

        // Check if ledger entry tags need to be inserted or updated.
        var assignLedgerEntryTagsSize = assignLedgerEntryTags.size();
        for (var i = 0; i < assignLedgerEntryTagsSize; i++) {
            var assignLedgerEntryTag = assignLedgerEntryTags.get(i);
            var existingLedgerEntryTag = existingLedgerEntryTags.get(assignLedgerEntryTag.getTag().getId());
            if (existingLedgerEntryTag == null) {
                saveLedgerEntryTags.add(assignLedgerEntryTag);
                insertLedgerEntryTags.add(assignLedgerEntryTag);
            } else {
                // Update existing tag if dirty.
                var historyEntry = LedgerEntryTag.historyEntryModify(existingLedgerEntryTag, assignLedgerEntryTag, userRef);
                if (historyEntry != null) {
                    // Add ledger entry history entry for modification.
                    historyEntries.add(historyEntry);
                    existingLedgerEntryTag.setPriority(assignLedgerEntryTag.getPriority());
                    saveLedgerEntryTags.add(existingLedgerEntryTag);
                    updateLedgerEntryTags.add(assignLedgerEntryTag);
                }
                assignLedgerEntryTags.set(i, existingLedgerEntryTag);
            }
        }

        // // Upsert ledger entry tags.
        ledgerEntryTagRepository.saveAll(saveLedgerEntryTags);

        // Add ledger entry tag history entries for inserts.
        historyEntries.addAll(insertLedgerEntryTags.stream().map(let -> LedgerEntryTag.historyEntryCreate(let, userRef)).toList());

        // Save history entries.
        historyEntryRepository.saveAll(historyEntries);
        historyEntryFieldRepository.saveAll(historyEntries.stream().flatMap(e -> e.getFields().stream()).toList());

        // Update search repo. There are race conditions here but it's good enough for now.
        // TODO: Remove race conditions.
        var elasticTags = saveTags.stream().map(t -> ElasticTag.fromEntity(t)).collect(Collectors.toList());
        tagSearchRepository.saveAll(elasticTags);

        var result = assignTags.stream().map(t -> TagOut.fromEntity(t)).collect(Collectors.toList());

        return ResponseEntity
            .ok()
            .headers(
                HeaderUtil.createAlert(applicationName, applicationName + ".ledger.entry.message.tagsSaved", ledgerEntry.getId().toString())
            )
            .body(result);
    }

    @GetMapping("/ledger/tag/autocomplete")
    public ResponseEntity<List<TagOut>> getTagAutocomplete(@RequestParam String text, @RequestParam String existing) {
        log.debug("REST request to autocomplete tag text: {}", text);

        var pagable = PageRequest.of(0, 10);
        var textNormalized = Tag.normalizeText(text);
        var searchResult = tagSearchRepository.findTextAutocomplete(text, textNormalized, pagable);
        var result = searchResult.stream().map(et -> TagOut.fromElastic(et)).collect(Collectors.toList());

        return ResponseEntity.ok().body(result);
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

    @GetMapping("/ledger/meta/export")
    public ResponseEntity<ImportExportDTO> getMetaExport(@RequestParam Boolean onlyUser) {
        log.debug("REST request to export meta: only user ({})", onlyUser);

        var entryGraph = entityManager.createEntityGraph(HistoryEntry.class);
        entryGraph.addAttributeNodes("fields");

        var historyEntries = entityManager
            .createQuery("SELECT he FROM HistoryEntry he ORDER BY instant DESC", HistoryEntry.class)
            .setHint("javax.persistence.loadgraph", entryGraph)
            .getResultList();

        var user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get();

        var export = new ImportExportDTO();
        export.version = ExportVersion.VERSION_1.value;
        export.userId = user.getId();
        export.userEmail = user.getEmail();
        export.userName = user.getFirstName() + " " + user.getLastName();
        export.instant = Instant.now();
        export.historyEntries = historyEntries.stream().map(e -> ExportHistoryEntryDTO.fromEntity(e)).toList();

        return ResponseEntity.ok().body(export);
    }

    @Transactional
    @PostMapping("/ledger/meta/import")
    public ResponseEntity<Void> postMetaImport(@Valid @RequestBody ImportExportDTO dto) {
        //     log.debug("REST request to import meta");

        //     if (dto.historyEntries.size() == 0) {
        //         return ResponseEntity.noContent().build();
        //     }

        //     var entries = dto.historyEntries.stream()
        //         .map(e -> e.toEntity(entityManager))
        //         .toList();

        //     // Sort newest history entry first.
        //     entries.sort((a, b) -> b.getInstant().compareTo(a.getInstant()));

        //     var groups = entries.stream()
        //         .collect(Collectors.groupingBy(e -> Triplet.create(e.getRecType(), e.getRecId1(), e.getRecId2())));

        //     for (var key : groups.keySet()) {
        //         var recEntries = groups.get(key);
        //         var startIndexLocal = HistoryUtils.findSyncStartIndex(recEntries);
        //         var startEntryLocal = recEntries.get(startIndexLocal);

        //         var startEntryDbQuery = entityManager.createQuery("""
        //         SELECT he FROM HistoryEntry he
        //         WHERE
        //             recType = :rec_type
        //             AND recId1 = :rec_id_1
        //             AND recId2 = :rec_id_1
        //             AND \"action\" IN ('CREATE', 'DELETE')
        //             AND instant >= :instant
        //         ORDER BY instant DESC
        //         """, HistoryEntry.class);
        //         startEntryDbQuery.setParameter("rec_type", startEntryLocal.getRecType());
        //         startEntryDbQuery.setParameter("rec_id_1", startEntryLocal.getRecId1());
        //         startEntryDbQuery.setParameter("rec_id_2", startEntryLocal.getRecId2());
        //         startEntryDbQuery.setParameter("instant", startEntryLocal.getInstant());
        //         startEntryDbQuery.setMaxResults(1);
        //         var startEntryDb = startEntryDbQuery.getResultList().stream().findFirst().orElse(null);

        //         var startEntry = HistoryUtils.newest(startEntryLocal, startEntryDb);

        //         var syncEntriesQuery = entityManager.createQuery("""
        //             SELECT he FROM HistoryEntry he
        //             WHERE
        //                 recType = :rec_type
        //                 AND recId1 = :rec_id_1
        //                 AND recId2 = :rec_id_1
        //                 AND instant >= :instant
        //             ORDER BY instant DESC
        //         """, HistoryEntry.class);
        //         syncEntriesQuery.setParameter("rec_type", startEntry.getRecType());
        //         syncEntriesQuery.setParameter("rec_id_1", startEntry.getRecId1());
        //         syncEntriesQuery.setParameter("rec_id_2", startEntry.getRecId2());
        //         syncEntriesQuery.setParameter("instant", startEntry.getInstant());
        //     }
        //
        return ResponseEntity.noContent().build();
    }
}
