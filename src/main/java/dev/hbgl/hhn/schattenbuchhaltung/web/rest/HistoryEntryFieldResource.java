package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntryField;
import dev.hbgl.hhn.schattenbuchhaltung.repository.HistoryEntryFieldRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.search.HistoryEntryFieldSearchRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntryField}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class HistoryEntryFieldResource {

    private final Logger log = LoggerFactory.getLogger(HistoryEntryFieldResource.class);

    private final HistoryEntryFieldRepository historyEntryFieldRepository;

    private final HistoryEntryFieldSearchRepository historyEntryFieldSearchRepository;

    public HistoryEntryFieldResource(
        HistoryEntryFieldRepository historyEntryFieldRepository,
        HistoryEntryFieldSearchRepository historyEntryFieldSearchRepository
    ) {
        this.historyEntryFieldRepository = historyEntryFieldRepository;
        this.historyEntryFieldSearchRepository = historyEntryFieldSearchRepository;
    }

    /**
     * {@code GET  /history-entry-fields} : get all the historyEntryFields.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of historyEntryFields in body.
     */
    @GetMapping("/history-entry-fields")
    public List<HistoryEntryField> getAllHistoryEntryFields() {
        log.debug("REST request to get all HistoryEntryFields");
        return historyEntryFieldRepository.findAll();
    }

    /**
     * {@code GET  /history-entry-fields/:id} : get the "id" historyEntryField.
     *
     * @param id the id of the historyEntryField to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the historyEntryField, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/history-entry-fields/{id}")
    public ResponseEntity<HistoryEntryField> getHistoryEntryField(@PathVariable Long id) {
        log.debug("REST request to get HistoryEntryField : {}", id);
        Optional<HistoryEntryField> historyEntryField = historyEntryFieldRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(historyEntryField);
    }

    /**
     * {@code SEARCH  /_search/history-entry-fields?query=:query} : search for the historyEntryField corresponding
     * to the query.
     *
     * @param query the query of the historyEntryField search.
     * @return the result of the search.
     */
    @GetMapping("/_search/history-entry-fields")
    public List<HistoryEntryField> searchHistoryEntryFields(@RequestParam String query) {
        log.debug("REST request to search HistoryEntryFields for query {}", query);
        return StreamSupport
            .stream(historyEntryFieldSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
