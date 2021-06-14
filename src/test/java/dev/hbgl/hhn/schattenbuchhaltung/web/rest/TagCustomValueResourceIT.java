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
import dev.hbgl.hhn.schattenbuchhaltung.domain.TagCustomValue;
import dev.hbgl.hhn.schattenbuchhaltung.repository.TagCustomValueRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.search.TagCustomValueSearchRepository;
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
 * Integration tests for the {@link TagCustomValueResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TagCustomValueResourceIT {

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tag-custom-values";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/tag-custom-values";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TagCustomValueRepository tagCustomValueRepository;

    /**
     * This repository is mocked in the dev.hbgl.hhn.schattenbuchhaltung.repository.search test package.
     *
     * @see dev.hbgl.hhn.schattenbuchhaltung.repository.search.TagCustomValueSearchRepositoryMockConfiguration
     */
    @Autowired
    private TagCustomValueSearchRepository mockTagCustomValueSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTagCustomValueMockMvc;

    private TagCustomValue tagCustomValue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TagCustomValue createEntity(EntityManager em) {
        TagCustomValue tagCustomValue = new TagCustomValue().value(DEFAULT_VALUE);
        // Add required entity
        TagCustomType tagCustomType;
        if (TestUtil.findAll(em, TagCustomType.class).isEmpty()) {
            tagCustomType = TagCustomTypeResourceIT.createEntity(em);
            em.persist(tagCustomType);
            em.flush();
        } else {
            tagCustomType = TestUtil.findAll(em, TagCustomType.class).get(0);
        }
        tagCustomValue.setType(tagCustomType);
        return tagCustomValue;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TagCustomValue createUpdatedEntity(EntityManager em) {
        TagCustomValue tagCustomValue = new TagCustomValue().value(UPDATED_VALUE);
        // Add required entity
        TagCustomType tagCustomType;
        if (TestUtil.findAll(em, TagCustomType.class).isEmpty()) {
            tagCustomType = TagCustomTypeResourceIT.createUpdatedEntity(em);
            em.persist(tagCustomType);
            em.flush();
        } else {
            tagCustomType = TestUtil.findAll(em, TagCustomType.class).get(0);
        }
        tagCustomValue.setType(tagCustomType);
        return tagCustomValue;
    }

    @BeforeEach
    public void initTest() {
        tagCustomValue = createEntity(em);
    }

    @Test
    @Transactional
    void createTagCustomValue() throws Exception {
        int databaseSizeBeforeCreate = tagCustomValueRepository.findAll().size();
        // Create the TagCustomValue
        restTagCustomValueMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomValue))
            )
            .andExpect(status().isCreated());

        // Validate the TagCustomValue in the database
        List<TagCustomValue> tagCustomValueList = tagCustomValueRepository.findAll();
        assertThat(tagCustomValueList).hasSize(databaseSizeBeforeCreate + 1);
        TagCustomValue testTagCustomValue = tagCustomValueList.get(tagCustomValueList.size() - 1);
        assertThat(testTagCustomValue.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the TagCustomValue in Elasticsearch
        verify(mockTagCustomValueSearchRepository, times(1)).save(testTagCustomValue);
    }

    @Test
    @Transactional
    void createTagCustomValueWithExistingId() throws Exception {
        // Create the TagCustomValue with an existing ID
        tagCustomValue.setId(1L);

        int databaseSizeBeforeCreate = tagCustomValueRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTagCustomValueMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomValue))
            )
            .andExpect(status().isBadRequest());

        // Validate the TagCustomValue in the database
        List<TagCustomValue> tagCustomValueList = tagCustomValueRepository.findAll();
        assertThat(tagCustomValueList).hasSize(databaseSizeBeforeCreate);

        // Validate the TagCustomValue in Elasticsearch
        verify(mockTagCustomValueSearchRepository, times(0)).save(tagCustomValue);
    }

    @Test
    @Transactional
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagCustomValueRepository.findAll().size();
        // set the field null
        tagCustomValue.setValue(null);

        // Create the TagCustomValue, which fails.

        restTagCustomValueMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomValue))
            )
            .andExpect(status().isBadRequest());

        List<TagCustomValue> tagCustomValueList = tagCustomValueRepository.findAll();
        assertThat(tagCustomValueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTagCustomValues() throws Exception {
        // Initialize the database
        tagCustomValueRepository.saveAndFlush(tagCustomValue);

        // Get all the tagCustomValueList
        restTagCustomValueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tagCustomValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    void getTagCustomValue() throws Exception {
        // Initialize the database
        tagCustomValueRepository.saveAndFlush(tagCustomValue);

        // Get the tagCustomValue
        restTagCustomValueMockMvc
            .perform(get(ENTITY_API_URL_ID, tagCustomValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tagCustomValue.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingTagCustomValue() throws Exception {
        // Get the tagCustomValue
        restTagCustomValueMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTagCustomValue() throws Exception {
        // Initialize the database
        tagCustomValueRepository.saveAndFlush(tagCustomValue);

        int databaseSizeBeforeUpdate = tagCustomValueRepository.findAll().size();

        // Update the tagCustomValue
        TagCustomValue updatedTagCustomValue = tagCustomValueRepository.findById(tagCustomValue.getId()).get();
        // Disconnect from session so that the updates on updatedTagCustomValue are not directly saved in db
        em.detach(updatedTagCustomValue);
        updatedTagCustomValue.value(UPDATED_VALUE);

        restTagCustomValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTagCustomValue.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTagCustomValue))
            )
            .andExpect(status().isOk());

        // Validate the TagCustomValue in the database
        List<TagCustomValue> tagCustomValueList = tagCustomValueRepository.findAll();
        assertThat(tagCustomValueList).hasSize(databaseSizeBeforeUpdate);
        TagCustomValue testTagCustomValue = tagCustomValueList.get(tagCustomValueList.size() - 1);
        assertThat(testTagCustomValue.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the TagCustomValue in Elasticsearch
        verify(mockTagCustomValueSearchRepository).save(testTagCustomValue);
    }

    @Test
    @Transactional
    void putNonExistingTagCustomValue() throws Exception {
        int databaseSizeBeforeUpdate = tagCustomValueRepository.findAll().size();
        tagCustomValue.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTagCustomValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tagCustomValue.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomValue))
            )
            .andExpect(status().isBadRequest());

        // Validate the TagCustomValue in the database
        List<TagCustomValue> tagCustomValueList = tagCustomValueRepository.findAll();
        assertThat(tagCustomValueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TagCustomValue in Elasticsearch
        verify(mockTagCustomValueSearchRepository, times(0)).save(tagCustomValue);
    }

    @Test
    @Transactional
    void putWithIdMismatchTagCustomValue() throws Exception {
        int databaseSizeBeforeUpdate = tagCustomValueRepository.findAll().size();
        tagCustomValue.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagCustomValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomValue))
            )
            .andExpect(status().isBadRequest());

        // Validate the TagCustomValue in the database
        List<TagCustomValue> tagCustomValueList = tagCustomValueRepository.findAll();
        assertThat(tagCustomValueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TagCustomValue in Elasticsearch
        verify(mockTagCustomValueSearchRepository, times(0)).save(tagCustomValue);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTagCustomValue() throws Exception {
        int databaseSizeBeforeUpdate = tagCustomValueRepository.findAll().size();
        tagCustomValue.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagCustomValueMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomValue))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TagCustomValue in the database
        List<TagCustomValue> tagCustomValueList = tagCustomValueRepository.findAll();
        assertThat(tagCustomValueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TagCustomValue in Elasticsearch
        verify(mockTagCustomValueSearchRepository, times(0)).save(tagCustomValue);
    }

    @Test
    @Transactional
    void partialUpdateTagCustomValueWithPatch() throws Exception {
        // Initialize the database
        tagCustomValueRepository.saveAndFlush(tagCustomValue);

        int databaseSizeBeforeUpdate = tagCustomValueRepository.findAll().size();

        // Update the tagCustomValue using partial update
        TagCustomValue partialUpdatedTagCustomValue = new TagCustomValue();
        partialUpdatedTagCustomValue.setId(tagCustomValue.getId());

        restTagCustomValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTagCustomValue.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTagCustomValue))
            )
            .andExpect(status().isOk());

        // Validate the TagCustomValue in the database
        List<TagCustomValue> tagCustomValueList = tagCustomValueRepository.findAll();
        assertThat(tagCustomValueList).hasSize(databaseSizeBeforeUpdate);
        TagCustomValue testTagCustomValue = tagCustomValueList.get(tagCustomValueList.size() - 1);
        assertThat(testTagCustomValue.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateTagCustomValueWithPatch() throws Exception {
        // Initialize the database
        tagCustomValueRepository.saveAndFlush(tagCustomValue);

        int databaseSizeBeforeUpdate = tagCustomValueRepository.findAll().size();

        // Update the tagCustomValue using partial update
        TagCustomValue partialUpdatedTagCustomValue = new TagCustomValue();
        partialUpdatedTagCustomValue.setId(tagCustomValue.getId());

        partialUpdatedTagCustomValue.value(UPDATED_VALUE);

        restTagCustomValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTagCustomValue.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTagCustomValue))
            )
            .andExpect(status().isOk());

        // Validate the TagCustomValue in the database
        List<TagCustomValue> tagCustomValueList = tagCustomValueRepository.findAll();
        assertThat(tagCustomValueList).hasSize(databaseSizeBeforeUpdate);
        TagCustomValue testTagCustomValue = tagCustomValueList.get(tagCustomValueList.size() - 1);
        assertThat(testTagCustomValue.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingTagCustomValue() throws Exception {
        int databaseSizeBeforeUpdate = tagCustomValueRepository.findAll().size();
        tagCustomValue.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTagCustomValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tagCustomValue.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomValue))
            )
            .andExpect(status().isBadRequest());

        // Validate the TagCustomValue in the database
        List<TagCustomValue> tagCustomValueList = tagCustomValueRepository.findAll();
        assertThat(tagCustomValueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TagCustomValue in Elasticsearch
        verify(mockTagCustomValueSearchRepository, times(0)).save(tagCustomValue);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTagCustomValue() throws Exception {
        int databaseSizeBeforeUpdate = tagCustomValueRepository.findAll().size();
        tagCustomValue.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagCustomValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomValue))
            )
            .andExpect(status().isBadRequest());

        // Validate the TagCustomValue in the database
        List<TagCustomValue> tagCustomValueList = tagCustomValueRepository.findAll();
        assertThat(tagCustomValueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TagCustomValue in Elasticsearch
        verify(mockTagCustomValueSearchRepository, times(0)).save(tagCustomValue);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTagCustomValue() throws Exception {
        int databaseSizeBeforeUpdate = tagCustomValueRepository.findAll().size();
        tagCustomValue.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagCustomValueMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tagCustomValue))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TagCustomValue in the database
        List<TagCustomValue> tagCustomValueList = tagCustomValueRepository.findAll();
        assertThat(tagCustomValueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TagCustomValue in Elasticsearch
        verify(mockTagCustomValueSearchRepository, times(0)).save(tagCustomValue);
    }

    @Test
    @Transactional
    void deleteTagCustomValue() throws Exception {
        // Initialize the database
        tagCustomValueRepository.saveAndFlush(tagCustomValue);

        int databaseSizeBeforeDelete = tagCustomValueRepository.findAll().size();

        // Delete the tagCustomValue
        restTagCustomValueMockMvc
            .perform(delete(ENTITY_API_URL_ID, tagCustomValue.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TagCustomValue> tagCustomValueList = tagCustomValueRepository.findAll();
        assertThat(tagCustomValueList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the TagCustomValue in Elasticsearch
        verify(mockTagCustomValueSearchRepository, times(1)).deleteById(tagCustomValue.getId());
    }

    @Test
    @Transactional
    void searchTagCustomValue() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        tagCustomValueRepository.saveAndFlush(tagCustomValue);
        when(mockTagCustomValueSearchRepository.search(queryStringQuery("id:" + tagCustomValue.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(tagCustomValue), PageRequest.of(0, 1), 1));

        // Search the tagCustomValue
        restTagCustomValueMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + tagCustomValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tagCustomValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }
}
