package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.hbgl.hhn.schattenbuchhaltung.IntegrationTest;
import dev.hbgl.hhn.schattenbuchhaltung.domain.CostType;
import dev.hbgl.hhn.schattenbuchhaltung.repository.CostTypeRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CostTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CostTypeResourceIT {

    private static final String DEFAULT_NO = "AAAAAAAAAA";
    private static final String UPDATED_NO = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cost-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CostTypeRepository costTypeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCostTypeMockMvc;

    private CostType costType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CostType createEntity(EntityManager em) {
        CostType costType = new CostType().no(DEFAULT_NO).name(DEFAULT_NAME);
        return costType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CostType createUpdatedEntity(EntityManager em) {
        CostType costType = new CostType().no(UPDATED_NO).name(UPDATED_NAME);
        return costType;
    }

    @BeforeEach
    public void initTest() {
        costType = createEntity(em);
    }

    @Test
    @Transactional
    void createCostType() throws Exception {
        int databaseSizeBeforeCreate = costTypeRepository.findAll().size();
        // Create the CostType
        restCostTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costType))
            )
            .andExpect(status().isCreated());

        // Validate the CostType in the database
        List<CostType> costTypeList = costTypeRepository.findAll();
        assertThat(costTypeList).hasSize(databaseSizeBeforeCreate + 1);
        CostType testCostType = costTypeList.get(costTypeList.size() - 1);
        assertThat(testCostType.getNo()).isEqualTo(DEFAULT_NO);
        assertThat(testCostType.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createCostTypeWithExistingId() throws Exception {
        // Create the CostType with an existing ID
        costType.setId(1L);

        int databaseSizeBeforeCreate = costTypeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCostTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costType))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostType in the database
        List<CostType> costTypeList = costTypeRepository.findAll();
        assertThat(costTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNoIsRequired() throws Exception {
        int databaseSizeBeforeTest = costTypeRepository.findAll().size();
        // set the field null
        costType.setNo(null);

        // Create the CostType, which fails.

        restCostTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costType))
            )
            .andExpect(status().isBadRequest());

        List<CostType> costTypeList = costTypeRepository.findAll();
        assertThat(costTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = costTypeRepository.findAll().size();
        // set the field null
        costType.setName(null);

        // Create the CostType, which fails.

        restCostTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costType))
            )
            .andExpect(status().isBadRequest());

        List<CostType> costTypeList = costTypeRepository.findAll();
        assertThat(costTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCostTypes() throws Exception {
        // Initialize the database
        costTypeRepository.saveAndFlush(costType);

        // Get all the costTypeList
        restCostTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(costType.getId().intValue())))
            .andExpect(jsonPath("$.[*].no").value(hasItem(DEFAULT_NO)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getCostType() throws Exception {
        // Initialize the database
        costTypeRepository.saveAndFlush(costType);

        // Get the costType
        restCostTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, costType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(costType.getId().intValue()))
            .andExpect(jsonPath("$.no").value(DEFAULT_NO))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingCostType() throws Exception {
        // Get the costType
        restCostTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCostType() throws Exception {
        // Initialize the database
        costTypeRepository.saveAndFlush(costType);

        int databaseSizeBeforeUpdate = costTypeRepository.findAll().size();

        // Update the costType
        CostType updatedCostType = costTypeRepository.findById(costType.getId()).get();
        // Disconnect from session so that the updates on updatedCostType are not directly saved in db
        em.detach(updatedCostType);
        updatedCostType.no(UPDATED_NO).name(UPDATED_NAME);

        restCostTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCostType.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCostType))
            )
            .andExpect(status().isOk());

        // Validate the CostType in the database
        List<CostType> costTypeList = costTypeRepository.findAll();
        assertThat(costTypeList).hasSize(databaseSizeBeforeUpdate);
        CostType testCostType = costTypeList.get(costTypeList.size() - 1);
        assertThat(testCostType.getNo()).isEqualTo(UPDATED_NO);
        assertThat(testCostType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingCostType() throws Exception {
        int databaseSizeBeforeUpdate = costTypeRepository.findAll().size();
        costType.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCostTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, costType.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costType))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostType in the database
        List<CostType> costTypeList = costTypeRepository.findAll();
        assertThat(costTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCostType() throws Exception {
        int databaseSizeBeforeUpdate = costTypeRepository.findAll().size();
        costType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costType))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostType in the database
        List<CostType> costTypeList = costTypeRepository.findAll();
        assertThat(costTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCostType() throws Exception {
        int databaseSizeBeforeUpdate = costTypeRepository.findAll().size();
        costType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostTypeMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costType))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CostType in the database
        List<CostType> costTypeList = costTypeRepository.findAll();
        assertThat(costTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCostTypeWithPatch() throws Exception {
        // Initialize the database
        costTypeRepository.saveAndFlush(costType);

        int databaseSizeBeforeUpdate = costTypeRepository.findAll().size();

        // Update the costType using partial update
        CostType partialUpdatedCostType = new CostType();
        partialUpdatedCostType.setId(costType.getId());

        partialUpdatedCostType.no(UPDATED_NO);

        restCostTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCostType.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCostType))
            )
            .andExpect(status().isOk());

        // Validate the CostType in the database
        List<CostType> costTypeList = costTypeRepository.findAll();
        assertThat(costTypeList).hasSize(databaseSizeBeforeUpdate);
        CostType testCostType = costTypeList.get(costTypeList.size() - 1);
        assertThat(testCostType.getNo()).isEqualTo(UPDATED_NO);
        assertThat(testCostType.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateCostTypeWithPatch() throws Exception {
        // Initialize the database
        costTypeRepository.saveAndFlush(costType);

        int databaseSizeBeforeUpdate = costTypeRepository.findAll().size();

        // Update the costType using partial update
        CostType partialUpdatedCostType = new CostType();
        partialUpdatedCostType.setId(costType.getId());

        partialUpdatedCostType.no(UPDATED_NO).name(UPDATED_NAME);

        restCostTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCostType.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCostType))
            )
            .andExpect(status().isOk());

        // Validate the CostType in the database
        List<CostType> costTypeList = costTypeRepository.findAll();
        assertThat(costTypeList).hasSize(databaseSizeBeforeUpdate);
        CostType testCostType = costTypeList.get(costTypeList.size() - 1);
        assertThat(testCostType.getNo()).isEqualTo(UPDATED_NO);
        assertThat(testCostType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingCostType() throws Exception {
        int databaseSizeBeforeUpdate = costTypeRepository.findAll().size();
        costType.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCostTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, costType.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(costType))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostType in the database
        List<CostType> costTypeList = costTypeRepository.findAll();
        assertThat(costTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCostType() throws Exception {
        int databaseSizeBeforeUpdate = costTypeRepository.findAll().size();
        costType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(costType))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostType in the database
        List<CostType> costTypeList = costTypeRepository.findAll();
        assertThat(costTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCostType() throws Exception {
        int databaseSizeBeforeUpdate = costTypeRepository.findAll().size();
        costType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostTypeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(costType))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CostType in the database
        List<CostType> costTypeList = costTypeRepository.findAll();
        assertThat(costTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCostType() throws Exception {
        // Initialize the database
        costTypeRepository.saveAndFlush(costType);

        int databaseSizeBeforeDelete = costTypeRepository.findAll().size();

        // Delete the costType
        restCostTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, costType.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CostType> costTypeList = costTypeRepository.findAll();
        assertThat(costTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
