package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntry;
import dev.hbgl.hhn.schattenbuchhaltung.repository.HistoryEntryRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.search.HistoryEntrySearchRepository;
import dev.hbgl.hhn.schattenbuchhaltung.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntry}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class HistoryEntryResource {

    private final Logger log = LoggerFactory.getLogger(HistoryEntryResource.class);

    private final HistoryEntryRepository historyEntryRepository;

    private final HistoryEntrySearchRepository historyEntrySearchRepository;

    public HistoryEntryResource(HistoryEntryRepository historyEntryRepository, HistoryEntrySearchRepository historyEntrySearchRepository) {
        this.historyEntryRepository = historyEntryRepository;
        this.historyEntrySearchRepository = historyEntrySearchRepository;
    }

    /**
     * {@code GET  /history-entries} : get all the historyEntries.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of historyEntries in body.
     */
    @GetMapping("/history-entries")
    public ResponseEntity<List<HistoryEntry>> getAllHistoryEntries(Pageable pageable) {
        log.debug("REST request to get a page of HistoryEntries");
        Page<HistoryEntry> page = historyEntryRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /history-entries/:id} : get the "id" historyEntry.
     *
     * @param id the id of the historyEntry to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the historyEntry, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/history-entries/{id}")
    public ResponseEntity<HistoryEntry> getHistoryEntry(@PathVariable Long id) {
        log.debug("REST request to get HistoryEntry : {}", id);
        Optional<HistoryEntry> historyEntry = historyEntryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(historyEntry);
    }

    /**
     * {@code SEARCH  /_search/history-entries?query=:query} : search for the historyEntry corresponding
     * to the query.
     *
     * @param query the query of the historyEntry search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/history-entries")
    public ResponseEntity<List<HistoryEntry>> searchHistoryEntries(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of HistoryEntries for query {}", query);
        Page<HistoryEntry> page = historyEntrySearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
