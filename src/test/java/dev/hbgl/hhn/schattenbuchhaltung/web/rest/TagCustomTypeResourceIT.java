package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.hbgl.hhn.schattenbuchhaltung.IntegrationTest;
import dev.hbgl.hhn.schattenbuchhaltung.domain.TagCustomType;
import dev.hbgl.hhn.schattenbuchhaltung.repository.TagCustomTypeRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.search.TagCustomTypeSearchRepository;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TagCustomTypeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TagCustomTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final String ENTITY_API_URL = "/api/tag-custom-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/tag-custom-types";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TagCustomTypeRepository tagCustomTypeRepository;

    /**
     * This repository is mocked in the dev.hbgl.hhn.schattenbuchhaltung.repository.search test package.
     *
     * @see dev.hbgl.hhn.schattenbuchhaltung.repository.search.TagCustomTypeSearchRepositoryMockConfiguration
     */
    @Autowired
    private TagCustomTypeSearchRepository mockTagCustomTypeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTagCustomTypeMockMvc;

    private TagCustomType tagCustomType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TagCustomType createEntity(EntityManager em) {
        TagCustomType tagCustomType = new TagCustomType().name(DEFAULT_NAME).enabled(DEFAULT_ENABLED);
        return tagCustomType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TagCustomType createUpdatedEntity(EntityManager em) {
        TagCustomType tagCustomType = new TagCustomType().name(UPDATED_NAME).enabled(UPDATED_ENABLED);
        return tagCustomType;
    }

    @BeforeEach
    public void initTest() {
        tagCustomType = createEntity(em);
    }

    @Test
    @Transactional
    void createTagCustomType() throws Exception {
        int databaseSizeBeforeCreate = tagCustomTypeRepository.findAll().size();
        // Create the TagCustomType
        restTagCustomTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomType))
            )
            .andExpect(status().isCreated());

        // Validate the TagCustomType in the database
        List<TagCustomType> tagCustomTypeList = tagCustomTypeRepository.findAll();
        assertThat(tagCustomTypeList).hasSize(databaseSizeBeforeCreate + 1);
        TagCustomType testTagCustomType = tagCustomTypeList.get(tagCustomTypeList.size() - 1);
        assertThat(testTagCustomType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTagCustomType.getEnabled()).isEqualTo(DEFAULT_ENABLED);

        // Validate the TagCustomType in Elasticsearch
        verify(mockTagCustomTypeSearchRepository, times(1)).save(testTagCustomType);
    }

    @Test
    @Transactional
    void createTagCustomTypeWithExistingId() throws Exception {
        // Create the TagCustomType with an existing ID
        tagCustomType.setId(1L);

        int databaseSizeBeforeCreate = tagCustomTypeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTagCustomTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomType))
            )
            .andExpect(status().isBadRequest());

        // Validate the TagCustomType in the database
        List<TagCustomType> tagCustomTypeList = tagCustomTypeRepository.findAll();
        assertThat(tagCustomTypeList).hasSize(databaseSizeBeforeCreate);

        // Validate the TagCustomType in Elasticsearch
        verify(mockTagCustomTypeSearchRepository, times(0)).save(tagCustomType);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagCustomTypeRepository.findAll().size();
        // set the field null
        tagCustomType.setName(null);

        // Create the TagCustomType, which fails.

        restTagCustomTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomType))
            )
            .andExpect(status().isBadRequest());

        List<TagCustomType> tagCustomTypeList = tagCustomTypeRepository.findAll();
        assertThat(tagCustomTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEnabledIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagCustomTypeRepository.findAll().size();
        // set the field null
        tagCustomType.setEnabled(null);

        // Create the TagCustomType, which fails.

        restTagCustomTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomType))
            )
            .andExpect(status().isBadRequest());

        List<TagCustomType> tagCustomTypeList = tagCustomTypeRepository.findAll();
        assertThat(tagCustomTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTagCustomTypes() throws Exception {
        // Initialize the database
        tagCustomTypeRepository.saveAndFlush(tagCustomType);

        // Get all the tagCustomTypeList
        restTagCustomTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tagCustomType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED.booleanValue())));
    }

    @Test
    @Transactional
    void getTagCustomType() throws Exception {
        // Initialize the database
        tagCustomTypeRepository.saveAndFlush(tagCustomType);

        // Get the tagCustomType
        restTagCustomTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, tagCustomType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tagCustomType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingTagCustomType() throws Exception {
        // Get the tagCustomType
        restTagCustomTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTagCustomType() throws Exception {
        // Initialize the database
        tagCustomTypeRepository.saveAndFlush(tagCustomType);

        int databaseSizeBeforeUpdate = tagCustomTypeRepository.findAll().size();

        // Update the tagCustomType
        TagCustomType updatedTagCustomType = tagCustomTypeRepository.findById(tagCustomType.getId()).get();
        // Disconnect from session so that the updates on updatedTagCustomType are not directly saved in db
        em.detach(updatedTagCustomType);
        updatedTagCustomType.name(UPDATED_NAME).enabled(UPDATED_ENABLED);

        restTagCustomTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTagCustomType.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTagCustomType))
            )
            .andExpect(status().isOk());

        // Validate the TagCustomType in the database
        List<TagCustomType> tagCustomTypeList = tagCustomTypeRepository.findAll();
        assertThat(tagCustomTypeList).hasSize(databaseSizeBeforeUpdate);
        TagCustomType testTagCustomType = tagCustomTypeList.get(tagCustomTypeList.size() - 1);
        assertThat(testTagCustomType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTagCustomType.getEnabled()).isEqualTo(UPDATED_ENABLED);

        // Validate the TagCustomType in Elasticsearch
        verify(mockTagCustomTypeSearchRepository).save(testTagCustomType);
    }

    @Test
    @Transactional
    void putNonExistingTagCustomType() throws Exception {
        int databaseSizeBeforeUpdate = tagCustomTypeRepository.findAll().size();
        tagCustomType.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTagCustomTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tagCustomType.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomType))
            )
            .andExpect(status().isBadRequest());

        // Validate the TagCustomType in the database
        List<TagCustomType> tagCustomTypeList = tagCustomTypeRepository.findAll();
        assertThat(tagCustomTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TagCustomType in Elasticsearch
        verify(mockTagCustomTypeSearchRepository, times(0)).save(tagCustomType);
    }

    @Test
    @Transactional
    void putWithIdMismatchTagCustomType() throws Exception {
        int databaseSizeBeforeUpdate = tagCustomTypeRepository.findAll().size();
        tagCustomType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagCustomTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomType))
            )
            .andExpect(status().isBadRequest());

        // Validate the TagCustomType in the database
        List<TagCustomType> tagCustomTypeList = tagCustomTypeRepository.findAll();
        assertThat(tagCustomTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TagCustomType in Elasticsearch
        verify(mockTagCustomTypeSearchRepository, times(0)).save(tagCustomType);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTagCustomType() throws Exception {
        int databaseSizeBeforeUpdate = tagCustomTypeRepository.findAll().size();
        tagCustomType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagCustomTypeMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomType))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TagCustomType in the database
        List<TagCustomType> tagCustomTypeList = tagCustomTypeRepository.findAll();
        assertThat(tagCustomTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TagCustomType in Elasticsearch
        verify(mockTagCustomTypeSearchRepository, times(0)).save(tagCustomType);
    }

    @Test
    @Transactional
    void partialUpdateTagCustomTypeWithPatch() throws Exception {
        // Initialize the database
        tagCustomTypeRepository.saveAndFlush(tagCustomType);

        int databaseSizeBeforeUpdate = tagCustomTypeRepository.findAll().size();

        // Update the tagCustomType using partial update
        TagCustomType partialUpdatedTagCustomType = new TagCustomType();
        partialUpdatedTagCustomType.setId(tagCustomType.getId());

        partialUpdatedTagCustomType.name(UPDATED_NAME);

        restTagCustomTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTagCustomType.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTagCustomType))
            )
            .andExpect(status().isOk());

        // Validate the TagCustomType in the database
        List<TagCustomType> tagCustomTypeList = tagCustomTypeRepository.findAll();
        assertThat(tagCustomTypeList).hasSize(databaseSizeBeforeUpdate);
        TagCustomType testTagCustomType = tagCustomTypeList.get(tagCustomTypeList.size() - 1);
        assertThat(testTagCustomType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTagCustomType.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    @Transactional
    void fullUpdateTagCustomTypeWithPatch() throws Exception {
        // Initialize the database
        tagCustomTypeRepository.saveAndFlush(tagCustomType);

        int databaseSizeBeforeUpdate = tagCustomTypeRepository.findAll().size();

        // Update the tagCustomType using partial update
        TagCustomType partialUpdatedTagCustomType = new TagCustomType();
        partialUpdatedTagCustomType.setId(tagCustomType.getId());

        partialUpdatedTagCustomType.name(UPDATED_NAME).enabled(UPDATED_ENABLED);

        restTagCustomTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTagCustomType.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTagCustomType))
            )
            .andExpect(status().isOk());

        // Validate the TagCustomType in the database
        List<TagCustomType> tagCustomTypeList = tagCustomTypeRepository.findAll();
        assertThat(tagCustomTypeList).hasSize(databaseSizeBeforeUpdate);
        TagCustomType testTagCustomType = tagCustomTypeList.get(tagCustomTypeList.size() - 1);
        assertThat(testTagCustomType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTagCustomType.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    @Transactional
    void patchNonExistingTagCustomType() throws Exception {
        int databaseSizeBeforeUpdate = tagCustomTypeRepository.findAll().size();
        tagCustomType.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTagCustomTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tagCustomType.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomType))
            )
            .andExpect(status().isBadRequest());

        // Validate the TagCustomType in the database
        List<TagCustomType> tagCustomTypeList = tagCustomTypeRepository.findAll();
        assertThat(tagCustomTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TagCustomType in Elasticsearch
        verify(mockTagCustomTypeSearchRepository, times(0)).save(tagCustomType);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTagCustomType() throws Exception {
        int databaseSizeBeforeUpdate = tagCustomTypeRepository.findAll().size();
        tagCustomType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagCustomTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomType))
            )
            .andExpect(status().isBadRequest());

        // Validate the TagCustomType in the database
        List<TagCustomType> tagCustomTypeList = tagCustomTypeRepository.findAll();
        assertThat(tagCustomTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TagCustomType in Elasticsearch
        verify(mockTagCustomTypeSearchRepository, times(0)).save(tagCustomType);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTagCustomType() throws Exception {
        int databaseSizeBeforeUpdate = tagCustomTypeRepository.findAll().size();
        tagCustomType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagCustomTypeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomType))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TagCustomType in the database
        List<TagCustomType> tagCustomTypeList = tagCustomTypeRepository.findAll();
        assertThat(tagCustomTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TagCustomType in Elasticsearch
        verify(mockTagCustomTypeSearchRepository, times(0)).save(tagCustomType);
    }

    @Test
    @Transactional
    void deleteTagCustomType() throws Exception {
        // Initialize the database
        tagCustomTypeRepository.saveAndFlush(tagCustomType);

        int databaseSizeBeforeDelete = tagCustomTypeRepository.findAll().size();

        // Delete the tagCustomType
        restTagCustomTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, tagCustomType.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TagCustomType> tagCustomTypeList = tagCustomTypeRepository.findAll();
        assertThat(tagCustomTypeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the TagCustomType in Elasticsearch
        verify(mockTagCustomTypeSearchRepository, times(1)).deleteById(tagCustomType.getId());
    }

    @Test
    @Transactional
    void searchTagCustomType() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        tagCustomTypeRepository.saveAndFlush(tagCustomType);
        when(mockTagCustomTypeSearchRepository.search(queryStringQuery("id:" + tagCustomType.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(tagCustomType), PageRequest.of(0, 1), 1));

        // Search the tagCustomType
        restTagCustomTypeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + tagCustomType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tagCustomType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED.booleanValue())));
    }
}
