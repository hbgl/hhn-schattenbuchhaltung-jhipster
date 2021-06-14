package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.hbgl.hhn.schattenbuchhaltung.IntegrationTest;
import dev.hbgl.hhn.schattenbuchhaltung.domain.CostCenter;
import dev.hbgl.hhn.schattenbuchhaltung.repository.CostCenterRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.search.CostCenterSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CostCenterResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CostCenterResourceIT {

    private static final String DEFAULT_NO = "AAAAAAAAAA";
    private static final String UPDATED_NO = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_RANK = 1;
    private static final Integer UPDATED_RANK = 2;

    private static final String ENTITY_API_URL = "/api/cost-centers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/cost-centers";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CostCenterRepository costCenterRepository;

    /**
     * This repository is mocked in the dev.hbgl.hhn.schattenbuchhaltung.repository.search test package.
     *
     * @see dev.hbgl.hhn.schattenbuchhaltung.repository.search.CostCenterSearchRepositoryMockConfiguration
     */
    @Autowired
    private CostCenterSearchRepository mockCostCenterSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCostCenterMockMvc;

    private CostCenter costCenter;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CostCenter createEntity(EntityManager em) {
        CostCenter costCenter = new CostCenter().no(DEFAULT_NO).name(DEFAULT_NAME).rank(DEFAULT_RANK);
        return costCenter;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CostCenter createUpdatedEntity(EntityManager em) {
        CostCenter costCenter = new CostCenter().no(UPDATED_NO).name(UPDATED_NAME).rank(UPDATED_RANK);
        return costCenter;
    }

    @BeforeEach
    public void initTest() {
        costCenter = createEntity(em);
    }

    @Test
    @Transactional
    void createCostCenter() throws Exception {
        int databaseSizeBeforeCreate = costCenterRepository.findAll().size();
        // Create the CostCenter
        restCostCenterMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costCenter))
            )
            .andExpect(status().isCreated());

        // Validate the CostCenter in the database
        List<CostCenter> costCenterList = costCenterRepository.findAll();
        assertThat(costCenterList).hasSize(databaseSizeBeforeCreate + 1);
        CostCenter testCostCenter = costCenterList.get(costCenterList.size() - 1);
        assertThat(testCostCenter.getNo()).isEqualTo(DEFAULT_NO);
        assertThat(testCostCenter.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCostCenter.getRank()).isEqualTo(DEFAULT_RANK);

        // Validate the CostCenter in Elasticsearch
        verify(mockCostCenterSearchRepository, times(1)).save(testCostCenter);
    }

    @Test
    @Transactional
    void createCostCenterWithExistingId() throws Exception {
        // Create the CostCenter with an existing ID
        costCenter.setId(1L);

        int databaseSizeBeforeCreate = costCenterRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCostCenterMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costCenter))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostCenter in the database
        List<CostCenter> costCenterList = costCenterRepository.findAll();
        assertThat(costCenterList).hasSize(databaseSizeBeforeCreate);

        // Validate the CostCenter in Elasticsearch
        verify(mockCostCenterSearchRepository, times(0)).save(costCenter);
    }

    @Test
    @Transactional
    void checkNoIsRequired() throws Exception {
        int databaseSizeBeforeTest = costCenterRepository.findAll().size();
        // set the field null
        costCenter.setNo(null);

        // Create the CostCenter, which fails.

        restCostCenterMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costCenter))
            )
            .andExpect(status().isBadRequest());

        List<CostCenter> costCenterList = costCenterRepository.findAll();
        assertThat(costCenterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = costCenterRepository.findAll().size();
        // set the field null
        costCenter.setName(null);

        // Create the CostCenter, which fails.

        restCostCenterMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costCenter))
            )
            .andExpect(status().isBadRequest());

        List<CostCenter> costCenterList = costCenterRepository.findAll();
        assertThat(costCenterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRankIsRequired() throws Exception {
        int databaseSizeBeforeTest = costCenterRepository.findAll().size();
        // set the field null
        costCenter.setRank(null);

        // Create the CostCenter, which fails.

        restCostCenterMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costCenter))
            )
            .andExpect(status().isBadRequest());

        List<CostCenter> costCenterList = costCenterRepository.findAll();
        assertThat(costCenterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCostCenters() throws Exception {
        // Initialize the database
        costCenterRepository.saveAndFlush(costCenter);

        // Get all the costCenterList
        restCostCenterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(costCenter.getId().intValue())))
            .andExpect(jsonPath("$.[*].no").value(hasItem(DEFAULT_NO)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)));
    }

    @Test
    @Transactional
    void getCostCenter() throws Exception {
        // Initialize the database
        costCenterRepository.saveAndFlush(costCenter);

        // Get the costCenter
        restCostCenterMockMvc
            .perform(get(ENTITY_API_URL_ID, costCenter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(costCenter.getId().intValue()))
            .andExpect(jsonPath("$.no").value(DEFAULT_NO))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.rank").value(DEFAULT_RANK));
    }

    @Test
    @Transactional
    void getNonExistingCostCenter() throws Exception {
        // Get the costCenter
        restCostCenterMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCostCenter() throws Exception {
        // Initialize the database
        costCenterRepository.saveAndFlush(costCenter);

        int databaseSizeBeforeUpdate = costCenterRepository.findAll().size();

        // Update the costCenter
        CostCenter updatedCostCenter = costCenterRepository.findById(costCenter.getId()).get();
        // Disconnect from session so that the updates on updatedCostCenter are not directly saved in db
        em.detach(updatedCostCenter);
        updatedCostCenter.no(UPDATED_NO).name(UPDATED_NAME).rank(UPDATED_RANK);

        restCostCenterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCostCenter.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCostCenter))
            )
            .andExpect(status().isOk());

        // Validate the CostCenter in the database
        List<CostCenter> costCenterList = costCenterRepository.findAll();
        assertThat(costCenterList).hasSize(databaseSizeBeforeUpdate);
        CostCenter testCostCenter = costCenterList.get(costCenterList.size() - 1);
        assertThat(testCostCenter.getNo()).isEqualTo(UPDATED_NO);
        assertThat(testCostCenter.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCostCenter.getRank()).isEqualTo(UPDATED_RANK);

        // Validate the CostCenter in Elasticsearch
        verify(mockCostCenterSearchRepository).save(testCostCenter);
    }

    @Test
    @Transactional
    void putNonExistingCostCenter() throws Exception {
        int databaseSizeBeforeUpdate = costCenterRepository.findAll().size();
        costCenter.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCostCenterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, costCenter.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costCenter))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostCenter in the database
        List<CostCenter> costCenterList = costCenterRepository.findAll();
        assertThat(costCenterList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CostCenter in Elasticsearch
        verify(mockCostCenterSearchRepository, times(0)).save(costCenter);
    }

    @Test
    @Transactional
    void putWithIdMismatchCostCenter() throws Exception {
        int databaseSizeBeforeUpdate = costCenterRepository.findAll().size();
        costCenter.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostCenterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costCenter))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostCenter in the database
        List<CostCenter> costCenterList = costCenterRepository.findAll();
        assertThat(costCenterList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CostCenter in Elasticsearch
        verify(mockCostCenterSearchRepository, times(0)).save(costCenter);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCostCenter() throws Exception {
        int databaseSizeBeforeUpdate = costCenterRepository.findAll().size();
        costCenter.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostCenterMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costCenter))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CostCenter in the database
        List<CostCenter> costCenterList = costCenterRepository.findAll();
        assertThat(costCenterList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CostCenter in Elasticsearch
        verify(mockCostCenterSearchRepository, times(0)).save(costCenter);
    }

    @Test
    @Transactional
    void partialUpdateCostCenterWithPatch() throws Exception {
        // Initialize the database
        costCenterRepository.saveAndFlush(costCenter);

        int databaseSizeBeforeUpdate = costCenterRepository.findAll().size();

        // Update the costCenter using partial update
        CostCenter partialUpdatedCostCenter = new CostCenter();
        partialUpdatedCostCenter.setId(costCenter.getId());

        partialUpdatedCostCenter.no(UPDATED_NO).name(UPDATED_NAME).rank(UPDATED_RANK);

        restCostCenterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCostCenter.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCostCenter))
            )
            .andExpect(status().isOk());

        // Validate the CostCenter in the database
        List<CostCenter> costCenterList = costCenterRepository.findAll();
        assertThat(costCenterList).hasSize(databaseSizeBeforeUpdate);
        CostCenter testCostCenter = costCenterList.get(costCenterList.size() - 1);
        assertThat(testCostCenter.getNo()).isEqualTo(UPDATED_NO);
        assertThat(testCostCenter.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCostCenter.getRank()).isEqualTo(UPDATED_RANK);
    }

    @Test
    @Transactional
    void fullUpdateCostCenterWithPatch() throws Exception {
        // Initialize the database
        costCenterRepository.saveAndFlush(costCenter);

        int databaseSizeBeforeUpdate = costCenterRepository.findAll().size();

        // Update the costCenter using partial update
        CostCenter partialUpdatedCostCenter = new CostCenter();
        partialUpdatedCostCenter.setId(costCenter.getId());

        partialUpdatedCostCenter.no(UPDATED_NO).name(UPDATED_NAME).rank(UPDATED_RANK);

        restCostCenterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCostCenter.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCostCenter))
            )
            .andExpect(status().isOk());

        // Validate the CostCenter in the database
        List<CostCenter> costCenterList = costCenterRepository.findAll();
        assertThat(costCenterList).hasSize(databaseSizeBeforeUpdate);
        CostCenter testCostCenter = costCenterList.get(costCenterList.size() - 1);
        assertThat(testCostCenter.getNo()).isEqualTo(UPDATED_NO);
        assertThat(testCostCenter.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCostCenter.getRank()).isEqualTo(UPDATED_RANK);
    }

    @Test
    @Transactional
    void patchNonExistingCostCenter() throws Exception {
        int databaseSizeBeforeUpdate = costCenterRepository.findAll().size();
        costCenter.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCostCenterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, costCenter.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(costCenter))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostCenter in the database
        List<CostCenter> costCenterList = costCenterRepository.findAll();
        assertThat(costCenterList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CostCenter in Elasticsearch
        verify(mockCostCenterSearchRepository, times(0)).save(costCenter);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCostCenter() throws Exception {
        int databaseSizeBeforeUpdate = costCenterRepository.findAll().size();
        costCenter.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostCenterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(costCenter))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostCenter in the database
        List<CostCenter> costCenterList = costCenterRepository.findAll();
        assertThat(costCenterList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CostCenter in Elasticsearch
        verify(mockCostCenterSearchRepository, times(0)).save(costCenter);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCostCenter() throws Exception {
        int databaseSizeBeforeUpdate = costCenterRepository.findAll().size();
        costCenter.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostCenterMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(costCenter))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CostCenter in the database
        List<CostCenter> costCenterList = costCenterRepository.findAll();
        assertThat(costCenterList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CostCenter in Elasticsearch
        verify(mockCostCenterSearchRepository, times(0)).save(costCenter);
    }

    @Test
    @Transactional
    void deleteCostCenter() throws Exception {
        // Initialize the database
        costCenterRepository.saveAndFlush(costCenter);

        int databaseSizeBeforeDelete = costCenterRepository.findAll().size();

        // Delete the costCenter
        restCostCenterMockMvc
            .perform(delete(ENTITY_API_URL_ID, costCenter.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CostCenter> costCenterList = costCenterRepository.findAll();
        assertThat(costCenterList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the CostCenter in Elasticsearch
        verify(mockCostCenterSearchRepository, times(1)).deleteById(costCenter.getId());
    }

    @Test
    @Transactional
    void searchCostCenter() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        costCenterRepository.saveAndFlush(costCenter);
        when(mockCostCenterSearchRepository.search(queryStringQuery("id:" + costCenter.getId())))
            .thenReturn(Collections.singletonList(costCenter));

        // Search the costCenter
        restCostCenterMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + costCenter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(costCenter.getId().intValue())))
            .andExpect(jsonPath("$.[*].no").value(hasItem(DEFAULT_NO)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)));
    }
}
