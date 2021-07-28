package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import dev.hbgl.hhn.schattenbuchhaltung.domain.TagCustomValue;
import dev.hbgl.hhn.schattenbuchhaltung.repository.TagCustomValueRepository;
import dev.hbgl.hhn.schattenbuchhaltung.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
 * REST controller for managing {@link dev.hbgl.hhn.schattenbuchhaltung.domain.TagCustomValue}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TagCustomValueResource {

    private final Logger log = LoggerFactory.getLogger(TagCustomValueResource.class);

    private static final String ENTITY_NAME = "tagCustomValue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TagCustomValueRepository tagCustomValueRepository;

    public TagCustomValueResource(TagCustomValueRepository tagCustomValueRepository) {
        this.tagCustomValueRepository = tagCustomValueRepository;
    }

    /**
     * {@code POST  /tag-custom-values} : Create a new tagCustomValue.
     *
     * @param tagCustomValue the tagCustomValue to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tagCustomValue, or with status {@code 400 (Bad Request)} if the tagCustomValue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tag-custom-values")
    public ResponseEntity<TagCustomValue> createTagCustomValue(@Valid @RequestBody TagCustomValue tagCustomValue)
        throws URISyntaxException {
        log.debug("REST request to save TagCustomValue : {}", tagCustomValue);
        if (tagCustomValue.getId() != null) {
            throw new BadRequestAlertException("A new tagCustomValue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TagCustomValue result = tagCustomValueRepository.save(tagCustomValue);
        return ResponseEntity
            .created(new URI("/api/tag-custom-values/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tag-custom-values/:id} : Updates an existing tagCustomValue.
     *
     * @param id the id of the tagCustomValue to save.
     * @param tagCustomValue the tagCustomValue to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tagCustomValue,
     * or with status {@code 400 (Bad Request)} if the tagCustomValue is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tagCustomValue couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tag-custom-values/{id}")
    public ResponseEntity<TagCustomValue> updateTagCustomValue(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TagCustomValue tagCustomValue
    ) throws URISyntaxException {
        log.debug("REST request to update TagCustomValue : {}, {}", id, tagCustomValue);
        if (tagCustomValue.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tagCustomValue.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tagCustomValueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TagCustomValue result = tagCustomValueRepository.save(tagCustomValue);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tagCustomValue.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tag-custom-values/:id} : Partial updates given fields of an existing tagCustomValue, field will ignore if it is null
     *
     * @param id the id of the tagCustomValue to save.
     * @param tagCustomValue the tagCustomValue to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tagCustomValue,
     * or with status {@code 400 (Bad Request)} if the tagCustomValue is not valid,
     * or with status {@code 404 (Not Found)} if the tagCustomValue is not found,
     * or with status {@code 500 (Internal Server Error)} if the tagCustomValue couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tag-custom-values/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<TagCustomValue> partialUpdateTagCustomValue(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TagCustomValue tagCustomValue
    ) throws URISyntaxException {
        log.debug("REST request to partial update TagCustomValue partially : {}, {}", id, tagCustomValue);
        if (tagCustomValue.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tagCustomValue.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tagCustomValueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TagCustomValue> result = tagCustomValueRepository
            .findById(tagCustomValue.getId())
            .map(
                existingTagCustomValue -> {
                    if (tagCustomValue.getValue() != null) {
                        existingTagCustomValue.setValue(tagCustomValue.getValue());
                    }

                    return existingTagCustomValue;
                }
            )
            .map(tagCustomValueRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tagCustomValue.getId().toString())
        );
    }

    /**
     * {@code GET  /tag-custom-values} : get all the tagCustomValues.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tagCustomValues in body.
     */
    @GetMapping("/tag-custom-values")
    public ResponseEntity<List<TagCustomValue>> getAllTagCustomValues(Pageable pageable) {
        log.debug("REST request to get a page of TagCustomValues");
        Page<TagCustomValue> page = tagCustomValueRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tag-custom-values/:id} : get the "id" tagCustomValue.
     *
     * @param id the id of the tagCustomValue to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tagCustomValue, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tag-custom-values/{id}")
    public ResponseEntity<TagCustomValue> getTagCustomValue(@PathVariable Long id) {
        log.debug("REST request to get TagCustomValue : {}", id);
        Optional<TagCustomValue> tagCustomValue = tagCustomValueRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(tagCustomValue);
    }

    /**
     * {@code DELETE  /tag-custom-values/:id} : delete the "id" tagCustomValue.
     *
     * @param id the id of the tagCustomValue to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tag-custom-values/{id}")
    public ResponseEntity<Void> deleteTagCustomValue(@PathVariable Long id) {
        log.debug("REST request to delete TagCustomValue : {}", id);
        tagCustomValueRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
