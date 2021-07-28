package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntry;
import dev.hbgl.hhn.schattenbuchhaltung.repository.HistoryEntryRepository;
import dev.hbgl.hhn.schattenbuchhaltung.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    public HistoryEntryResource(HistoryEntryRepository historyEntryRepository) {
        this.historyEntryRepository = historyEntryRepository;
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
}
