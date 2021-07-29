package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import dev.hbgl.hhn.schattenbuchhaltung.domain.LedgerEntry;
import dev.hbgl.hhn.schattenbuchhaltung.repository.LedgerEntryRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.search.LedgerEntrySearchRepository;
import dev.hbgl.hhn.schattenbuchhaltung.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
public class LedgerEntryResource {

    private final Logger log = LoggerFactory.getLogger(LedgerEntryResource.class);

    private static final String ENTITY_NAME = "ledgerEntry";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LedgerEntryRepository ledgerEntryRepository;

    private final LedgerEntrySearchRepository ledgerEntrySearchRepository;

    public LedgerEntryResource(LedgerEntryRepository ledgerEntryRepository, LedgerEntrySearchRepository ledgerEntrySearchRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.ledgerEntrySearchRepository = ledgerEntrySearchRepository;
    }

    /**
     * {@code POST  /ledger-entries} : Create a new ledgerEntry.
     *
     * @param ledgerEntry the ledgerEntry to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ledgerEntry, or with status {@code 400 (Bad Request)} if the ledgerEntry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ledger-entries")
    public ResponseEntity<LedgerEntry> createLedgerEntry(@Valid @RequestBody LedgerEntry ledgerEntry) throws URISyntaxException {
        log.debug("REST request to save LedgerEntry : {}", ledgerEntry);
        if (ledgerEntry.getId() != null) {
            throw new BadRequestAlertException("A new ledgerEntry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LedgerEntry result = ledgerEntryRepository.save(ledgerEntry);
        ledgerEntrySearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/ledger-entries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ledger-entries/:id} : Updates an existing ledgerEntry.
     *
     * @param id the id of the ledgerEntry to save.
     * @param ledgerEntry the ledgerEntry to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ledgerEntry,
     * or with status {@code 400 (Bad Request)} if the ledgerEntry is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ledgerEntry couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ledger-entries/{id}")
    public ResponseEntity<LedgerEntry> updateLedgerEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LedgerEntry ledgerEntry
    ) throws URISyntaxException {
        log.debug("REST request to update LedgerEntry : {}, {}", id, ledgerEntry);
        if (ledgerEntry.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ledgerEntry.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ledgerEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LedgerEntry result = ledgerEntryRepository.save(ledgerEntry);
        ledgerEntrySearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ledgerEntry.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ledger-entries/:id} : Partial updates given fields of an existing ledgerEntry, field will ignore if it is null
     *
     * @param id the id of the ledgerEntry to save.
     * @param ledgerEntry the ledgerEntry to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ledgerEntry,
     * or with status {@code 400 (Bad Request)} if the ledgerEntry is not valid,
     * or with status {@code 404 (Not Found)} if the ledgerEntry is not found,
     * or with status {@code 500 (Internal Server Error)} if the ledgerEntry couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ledger-entries/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<LedgerEntry> partialUpdateLedgerEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LedgerEntry ledgerEntry
    ) throws URISyntaxException {
        log.debug("REST request to partial update LedgerEntry partially : {}, {}", id, ledgerEntry);
        if (ledgerEntry.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ledgerEntry.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ledgerEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LedgerEntry> result = ledgerEntryRepository
            .findById(ledgerEntry.getId())
            .map(
                existingLedgerEntry -> {
                    if (ledgerEntry.getNo() != null) {
                        existingLedgerEntry.setNo(ledgerEntry.getNo());
                    }
                    if (ledgerEntry.getDescription() != null) {
                        existingLedgerEntry.setDescription(ledgerEntry.getDescription());
                    }
                    if (ledgerEntry.getaNo() != null) {
                        existingLedgerEntry.setaNo(ledgerEntry.getaNo());
                    }
                    if (ledgerEntry.getBookingDate() != null) {
                        existingLedgerEntry.setBookingDate(ledgerEntry.getBookingDate());
                    }
                    if (ledgerEntry.getIncome() != null) {
                        existingLedgerEntry.setIncome(ledgerEntry.getIncome());
                    }
                    if (ledgerEntry.getExpenditure() != null) {
                        existingLedgerEntry.setExpenditure(ledgerEntry.getExpenditure());
                    }
                    if (ledgerEntry.getLiability() != null) {
                        existingLedgerEntry.setLiability(ledgerEntry.getLiability());
                    }

                    return existingLedgerEntry;
                }
            )
            .map(ledgerEntryRepository::save)
            .map(
                savedLedgerEntry -> {
                    ledgerEntrySearchRepository.save(savedLedgerEntry);

                    return savedLedgerEntry;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ledgerEntry.getId().toString())
        );
    }

    /**
     * {@code GET  /ledger-entries} : get all the ledgerEntries.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ledgerEntries in body.
     */
    @GetMapping("/ledger-entries")
    public List<LedgerEntry> getAllLedgerEntries(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all LedgerEntries");
        return ledgerEntryRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /ledger-entries/:id} : get the "id" ledgerEntry.
     *
     * @param id the id of the ledgerEntry to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ledgerEntry, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ledger-entries/{id}")
    public ResponseEntity<LedgerEntry> getLedgerEntry(@PathVariable Long id) {
        log.debug("REST request to get LedgerEntry : {}", id);
        Optional<LedgerEntry> ledgerEntry = ledgerEntryRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(ledgerEntry);
    }

    /**
     * {@code DELETE  /ledger-entries/:id} : delete the "id" ledgerEntry.
     *
     * @param id the id of the ledgerEntry to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ledger-entries/{id}")
    public ResponseEntity<Void> deleteLedgerEntry(@PathVariable Long id) {
        log.debug("REST request to delete LedgerEntry : {}", id);
        ledgerEntryRepository.deleteById(id);
        ledgerEntrySearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/ledger-entries?query=:query} : search for the ledgerEntry corresponding
     * to the query.
     *
     * @param query the query of the ledgerEntry search.
     * @return the result of the search.
     */
    @GetMapping("/_search/ledger-entries")
    public List<LedgerEntry> searchLedgerEntries(@RequestParam String query) {
        log.debug("REST request to search LedgerEntries for query {}", query);
        return StreamSupport
            .stream(ledgerEntrySearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
