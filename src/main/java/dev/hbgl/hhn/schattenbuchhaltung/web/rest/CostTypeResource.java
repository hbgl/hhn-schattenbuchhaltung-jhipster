package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import dev.hbgl.hhn.schattenbuchhaltung.domain.CostType;
import dev.hbgl.hhn.schattenbuchhaltung.repository.CostTypeRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link dev.hbgl.hhn.schattenbuchhaltung.domain.CostType}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CostTypeResource {

    private final Logger log = LoggerFactory.getLogger(CostTypeResource.class);

    private static final String ENTITY_NAME = "costType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CostTypeRepository costTypeRepository;

    public CostTypeResource(CostTypeRepository costTypeRepository) {
        this.costTypeRepository = costTypeRepository;
    }

    /**
     * {@code POST  /cost-types} : Create a new costType.
     *
     * @param costType the costType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new costType, or with status {@code 400 (Bad Request)} if the costType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cost-types")
    public ResponseEntity<CostType> createCostType(@Valid @RequestBody CostType costType) throws URISyntaxException {
        log.debug("REST request to save CostType : {}", costType);
        if (costType.getId() != null) {
            throw new BadRequestAlertException("A new costType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CostType result = costTypeRepository.save(costType);
        return ResponseEntity
            .created(new URI("/api/cost-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cost-types/:id} : Updates an existing costType.
     *
     * @param id the id of the costType to save.
     * @param costType the costType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated costType,
     * or with status {@code 400 (Bad Request)} if the costType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the costType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cost-types/{id}")
    public ResponseEntity<CostType> updateCostType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CostType costType
    ) throws URISyntaxException {
        log.debug("REST request to update CostType : {}, {}", id, costType);
        if (costType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, costType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!costTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CostType result = costTypeRepository.save(costType);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, costType.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /cost-types/:id} : Partial updates given fields of an existing costType, field will ignore if it is null
     *
     * @param id the id of the costType to save.
     * @param costType the costType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated costType,
     * or with status {@code 400 (Bad Request)} if the costType is not valid,
     * or with status {@code 404 (Not Found)} if the costType is not found,
     * or with status {@code 500 (Internal Server Error)} if the costType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/cost-types/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<CostType> partialUpdateCostType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CostType costType
    ) throws URISyntaxException {
        log.debug("REST request to partial update CostType partially : {}, {}", id, costType);
        if (costType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, costType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!costTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CostType> result = costTypeRepository
            .findById(costType.getId())
            .map(
                existingCostType -> {
                    if (costType.getNo() != null) {
                        existingCostType.setNo(costType.getNo());
                    }
                    if (costType.getName() != null) {
                        existingCostType.setName(costType.getName());
                    }

                    return existingCostType;
                }
            )
            .map(costTypeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, costType.getId().toString())
        );
    }

    /**
     * {@code GET  /cost-types} : get all the costTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of costTypes in body.
     */
    @GetMapping("/cost-types")
    public List<CostType> getAllCostTypes() {
        log.debug("REST request to get all CostTypes");
        return costTypeRepository.findAll();
    }

    /**
     * {@code GET  /cost-types/:id} : get the "id" costType.
     *
     * @param id the id of the costType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the costType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cost-types/{id}")
    public ResponseEntity<CostType> getCostType(@PathVariable Long id) {
        log.debug("REST request to get CostType : {}", id);
        Optional<CostType> costType = costTypeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(costType);
    }

    /**
     * {@code DELETE  /cost-types/:id} : delete the "id" costType.
     *
     * @param id the id of the costType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cost-types/{id}")
    public ResponseEntity<Void> deleteCostType(@PathVariable Long id) {
        log.debug("REST request to delete CostType : {}", id);
        costTypeRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
