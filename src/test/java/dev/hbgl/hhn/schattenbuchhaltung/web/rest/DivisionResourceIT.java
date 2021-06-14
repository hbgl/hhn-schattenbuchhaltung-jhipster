package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.hbgl.hhn.schattenbuchhaltung.IntegrationTest;
import dev.hbgl.hhn.schattenbuchhaltung.domain.Division;
import dev.hbgl.hhn.schattenbuchhaltung.repository.DivisionRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.search.DivisionSearchRepository;
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
 * Integration tests for the {@link DivisionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DivisionResourceIT {

    private static final String DEFAULT_NO = "AAAAAAAAAA";
    private static final String UPDATED_NO = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/divisions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/divisions";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DivisionRepository divisionRepository;

    /**
     * This repository is mocked in the dev.hbgl.hhn.schattenbuchhaltung.repository.search test package.
     *
     * @see dev.hbgl.hhn.schattenbuchhaltung.repository.search.DivisionSearchRepositoryMockConfiguration
     */
    @Autowired
    private DivisionSearchRepository mockDivisionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDivisionMockMvc;

    private Division division;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Division createEntity(EntityManager em) {
        Division division = new Division().no(DEFAULT_NO).name(DEFAULT_NAME);
        return division;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Division createUpdatedEntity(EntityManager em) {
        Division division = new Division().no(UPDATED_NO).name(UPDATED_NAME);
        return division;
    }

    @BeforeEach
    public void initTest() {
        division = createEntity(em);
    }

    @Test
    @Transactional
    void createDivision() throws Exception {
        int databaseSizeBeforeCreate = divisionRepository.findAll().size();
        // Create the Division
        restDivisionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(division))
            )
            .andExpect(status().isCreated());

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll();
        assertThat(divisionList).hasSize(databaseSizeBeforeCreate + 1);
        Division testDivision = divisionList.get(divisionList.size() - 1);
        assertThat(testDivision.getNo()).isEqualTo(DEFAULT_NO);
        assertThat(testDivision.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Division in Elasticsearch
        verify(mockDivisionSearchRepository, times(1)).save(testDivision);
    }

    @Test
    @Transactional
    void createDivisionWithExistingId() throws Exception {
        // Create the Division with an existing ID
        division.setId(1L);

        int databaseSizeBeforeCreate = divisionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDivisionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(division))
            )
            .andExpect(status().isBadRequest());

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll();
        assertThat(divisionList).hasSize(databaseSizeBeforeCreate);

        // Validate the Division in Elasticsearch
        verify(mockDivisionSearchRepository, times(0)).save(division);
    }

    @Test
    @Transactional
    void checkNoIsRequired() throws Exception {
        int databaseSizeBeforeTest = divisionRepository.findAll().size();
        // set the field null
        division.setNo(null);

        // Create the Division, which fails.

        restDivisionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(division))
            )
            .andExpect(status().isBadRequest());

        List<Division> divisionList = divisionRepository.findAll();
        assertThat(divisionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = divisionRepository.findAll().size();
        // set the field null
        division.setName(null);

        // Create the Division, which fails.

        restDivisionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(division))
            )
            .andExpect(status().isBadRequest());

        List<Division> divisionList = divisionRepository.findAll();
        assertThat(divisionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDivisions() throws Exception {
        // Initialize the database
        divisionRepository.saveAndFlush(division);

        // Get all the divisionList
        restDivisionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(division.getId().intValue())))
            .andExpect(jsonPath("$.[*].no").value(hasItem(DEFAULT_NO)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getDivision() throws Exception {
        // Initialize the database
        divisionRepository.saveAndFlush(division);

        // Get the division
        restDivisionMockMvc
            .perform(get(ENTITY_API_URL_ID, division.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(division.getId().intValue()))
            .andExpect(jsonPath("$.no").value(DEFAULT_NO))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingDivision() throws Exception {
        // Get the division
        restDivisionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDivision() throws Exception {
        // Initialize the database
        divisionRepository.saveAndFlush(division);

        int databaseSizeBeforeUpdate = divisionRepository.findAll().size();

        // Update the division
        Division updatedDivision = divisionRepository.findById(division.getId()).get();
        // Disconnect from session so that the updates on updatedDivision are not directly saved in db
        em.detach(updatedDivision);
        updatedDivision.no(UPDATED_NO).name(UPDATED_NAME);

        restDivisionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDivision.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDivision))
            )
            .andExpect(status().isOk());

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);
        Division testDivision = divisionList.get(divisionList.size() - 1);
        assertThat(testDivision.getNo()).isEqualTo(UPDATED_NO);
        assertThat(testDivision.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Division in Elasticsearch
        verify(mockDivisionSearchRepository).save(testDivision);
    }

    @Test
    @Transactional
    void putNonExistingDivision() throws Exception {
        int databaseSizeBeforeUpdate = divisionRepository.findAll().size();
        division.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDivisionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, division.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(division))
            )
            .andExpect(status().isBadRequest());

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Division in Elasticsearch
        verify(mockDivisionSearchRepository, times(0)).save(division);
    }

    @Test
    @Transactional
    void putWithIdMismatchDivision() throws Exception {
        int databaseSizeBeforeUpdate = divisionRepository.findAll().size();
        division.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDivisionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(division))
            )
            .andExpect(status().isBadRequest());

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Division in Elasticsearch
        verify(mockDivisionSearchRepository, times(0)).save(division);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDivision() throws Exception {
        int databaseSizeBeforeUpdate = divisionRepository.findAll().size();
        division.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDivisionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(division))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Division in Elasticsearch
        verify(mockDivisionSearchRepository, times(0)).save(division);
    }

    @Test
    @Transactional
    void partialUpdateDivisionWithPatch() throws Exception {
        // Initialize the database
        divisionRepository.saveAndFlush(division);

        int databaseSizeBeforeUpdate = divisionRepository.findAll().size();

        // Update the division using partial update
        Division partialUpdatedDivision = new Division();
        partialUpdatedDivision.setId(division.getId());

        restDivisionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDivision.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDivision))
            )
            .andExpect(status().isOk());

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);
        Division testDivision = divisionList.get(divisionList.size() - 1);
        assertThat(testDivision.getNo()).isEqualTo(DEFAULT_NO);
        assertThat(testDivision.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateDivisionWithPatch() throws Exception {
        // Initialize the database
        divisionRepository.saveAndFlush(division);

        int databaseSizeBeforeUpdate = divisionRepository.findAll().size();

        // Update the division using partial update
        Division partialUpdatedDivision = new Division();
        partialUpdatedDivision.setId(division.getId());

        partialUpdatedDivision.no(UPDATED_NO).name(UPDATED_NAME);

        restDivisionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDivision.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDivision))
            )
            .andExpect(status().isOk());

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);
        Division testDivision = divisionList.get(divisionList.size() - 1);
        assertThat(testDivision.getNo()).isEqualTo(UPDATED_NO);
        assertThat(testDivision.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingDivision() throws Exception {
        int databaseSizeBeforeUpdate = divisionRepository.findAll().size();
        division.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDivisionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, division.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(division))
            )
            .andExpect(status().isBadRequest());

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Division in Elasticsearch
        verify(mockDivisionSearchRepository, times(0)).save(division);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDivision() throws Exception {
        int databaseSizeBeforeUpdate = divisionRepository.findAll().size();
        division.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDivisionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(division))
            )
            .andExpect(status().isBadRequest());

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Division in Elasticsearch
        verify(mockDivisionSearchRepository, times(0)).save(division);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDivision() throws Exception {
        int databaseSizeBeforeUpdate = divisionRepository.findAll().size();
        division.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDivisionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(division))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Division in Elasticsearch
        verify(mockDivisionSearchRepository, times(0)).save(division);
    }

    @Test
    @Transactional
    void deleteDivision() throws Exception {
        // Initialize the database
        divisionRepository.saveAndFlush(division);

        int databaseSizeBeforeDelete = divisionRepository.findAll().size();

        // Delete the division
        restDivisionMockMvc
            .perform(delete(ENTITY_API_URL_ID, division.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Division> divisionList = divisionRepository.findAll();
        assertThat(divisionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Division in Elasticsearch
        verify(mockDivisionSearchRepository, times(1)).deleteById(division.getId());
    }

    @Test
    @Transactional
    void searchDivision() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        divisionRepository.saveAndFlush(division);
        when(mockDivisionSearchRepository.search(queryStringQuery("id:" + division.getId())))
            .thenReturn(Collections.singletonList(division));

        // Search the division
        restDivisionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + division.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(division.getId().intValue())))
            .andExpect(jsonPath("$.[*].no").value(hasItem(DEFAULT_NO)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
