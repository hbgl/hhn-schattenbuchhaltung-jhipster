package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import dev.hbgl.hhn.schattenbuchhaltung.domain.TagCustomType;
import dev.hbgl.hhn.schattenbuchhaltung.repository.TagCustomTypeRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.search.TagCustomTypeSearchRepository;
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
 * REST controller for managing {@link dev.hbgl.hhn.schattenbuchhaltung.domain.TagCustomType}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TagCustomTypeResource {

    private final Logger log = LoggerFactory.getLogger(TagCustomTypeResource.class);

    private static final String ENTITY_NAME = "tagCustomType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TagCustomTypeRepository tagCustomTypeRepository;

    private final TagCustomTypeSearchRepository tagCustomTypeSearchRepository;

    public TagCustomTypeResource(
        TagCustomTypeRepository tagCustomTypeRepository,
        TagCustomTypeSearchRepository tagCustomTypeSearchRepository
    ) {
        this.tagCustomTypeRepository = tagCustomTypeRepository;
        this.tagCustomTypeSearchRepository = tagCustomTypeSearchRepository;
    }

    /**
     * {@code POST  /tag-custom-types} : Create a new tagCustomType.
     *
     * @param tagCustomType the tagCustomType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tagCustomType, or with status {@code 400 (Bad Request)} if the tagCustomType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tag-custom-types")
    public ResponseEntity<TagCustomType> createTagCustomType(@Valid @RequestBody TagCustomType tagCustomType) throws URISyntaxException {
        log.debug("REST request to save TagCustomType : {}", tagCustomType);
        if (tagCustomType.getId() != null) {
            throw new BadRequestAlertException("A new tagCustomType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TagCustomType result = tagCustomTypeRepository.save(tagCustomType);
        tagCustomTypeSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/tag-custom-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tag-custom-types/:id} : Updates an existing tagCustomType.
     *
     * @param id the id of the tagCustomType to save.
     * @param tagCustomType the tagCustomType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tagCustomType,
     * or with status {@code 400 (Bad Request)} if the tagCustomType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tagCustomType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tag-custom-types/{id}")
    public ResponseEntity<TagCustomType> updateTagCustomType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TagCustomType tagCustomType
    ) throws URISyntaxException {
        log.debug("REST request to update TagCustomType : {}, {}", id, tagCustomType);
        if (tagCustomType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tagCustomType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tagCustomTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TagCustomType result = tagCustomTypeRepository.save(tagCustomType);
        tagCustomTypeSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tagCustomType.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tag-custom-types/:id} : Partial updates given fields of an existing tagCustomType, field will ignore if it is null
     *
     * @param id the id of the tagCustomType to save.
     * @param tagCustomType the tagCustomType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tagCustomType,
     * or with status {@code 400 (Bad Request)} if the tagCustomType is not valid,
     * or with status {@code 404 (Not Found)} if the tagCustomType is not found,
     * or with status {@code 500 (Internal Server Error)} if the tagCustomType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tag-custom-types/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<TagCustomType> partialUpdateTagCustomType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TagCustomType tagCustomType
    ) throws URISyntaxException {
        log.debug("REST request to partial update TagCustomType partially : {}, {}", id, tagCustomType);
        if (tagCustomType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tagCustomType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tagCustomTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TagCustomType> result = tagCustomTypeRepository
            .findById(tagCustomType.getId())
            .map(
                existingTagCustomType -> {
                    if (tagCustomType.getName() != null) {
                        existingTagCustomType.setName(tagCustomType.getName());
                    }
                    if (tagCustomType.getEnabled() != null) {
                        existingTagCustomType.setEnabled(tagCustomType.getEnabled());
                    }

                    return existingTagCustomType;
                }
            )
            .map(tagCustomTypeRepository::save)
            .map(
                savedTagCustomType -> {
                    tagCustomTypeSearchRepository.save(savedTagCustomType);

                    return savedTagCustomType;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tagCustomType.getId().toString())
        );
    }

    /**
     * {@code GET  /tag-custom-types} : get all the tagCustomTypes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tagCustomTypes in body.
     */
    @GetMapping("/tag-custom-types")
    public ResponseEntity<List<TagCustomType>> getAllTagCustomTypes(Pageable pageable) {
        log.debug("REST request to get a page of TagCustomTypes");
        Page<TagCustomType> page = tagCustomTypeRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tag-custom-types/:id} : get the "id" tagCustomType.
     *
     * @param id the id of the tagCustomType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tagCustomType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tag-custom-types/{id}")
    public ResponseEntity<TagCustomType> getTagCustomType(@PathVariable Long id) {
        log.debug("REST request to get TagCustomType : {}", id);
        Optional<TagCustomType> tagCustomType = tagCustomTypeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(tagCustomType);
    }

    /**
     * {@code DELETE  /tag-custom-types/:id} : delete the "id" tagCustomType.
     *
     * @param id the id of the tagCustomType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tag-custom-types/{id}")
    public ResponseEntity<Void> deleteTagCustomType(@PathVariable Long id) {
        log.debug("REST request to delete TagCustomType : {}", id);
        tagCustomTypeRepository.deleteById(id);
        tagCustomTypeSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/tag-custom-types?query=:query} : search for the tagCustomType corresponding
     * to the query.
     *
     * @param query the query of the tagCustomType search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/tag-custom-types")
    public ResponseEntity<List<TagCustomType>> searchTagCustomTypes(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of TagCustomTypes for query {}", query);
        Page<TagCustomType> page = tagCustomTypeSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
