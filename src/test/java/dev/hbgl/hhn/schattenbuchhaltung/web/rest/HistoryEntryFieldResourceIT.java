package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.hbgl.hhn.schattenbuchhaltung.IntegrationTest;
import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntry;
import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntryField;
import dev.hbgl.hhn.schattenbuchhaltung.repository.HistoryEntryFieldRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.search.HistoryEntryFieldSearchRepository;
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
 * Integration tests for the {@link HistoryEntryFieldResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class HistoryEntryFieldResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_TRANS_KEY = "AAAAAAAAAA";
    private static final String UPDATED_TRANS_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_OLD_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_OLD_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_NEW_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_NEW_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/history-entry-fields";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/history-entry-fields";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HistoryEntryFieldRepository historyEntryFieldRepository;

    /**
     * This repository is mocked in the dev.hbgl.hhn.schattenbuchhaltung.repository.search test package.
     *
     * @see dev.hbgl.hhn.schattenbuchhaltung.repository.search.HistoryEntryFieldSearchRepositoryMockConfiguration
     */
    @Autowired
    private HistoryEntryFieldSearchRepository mockHistoryEntryFieldSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHistoryEntryFieldMockMvc;

    private HistoryEntryField historyEntryField;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistoryEntryField createEntity(EntityManager em) {
        HistoryEntryField historyEntryField = new HistoryEntryField()
            .type(DEFAULT_TYPE)
            .transKey(DEFAULT_TRANS_KEY)
            .oldValue(DEFAULT_OLD_VALUE)
            .newValue(DEFAULT_NEW_VALUE);
        // Add required entity
        HistoryEntry historyEntry;
        if (TestUtil.findAll(em, HistoryEntry.class).isEmpty()) {
            historyEntry = HistoryEntryResourceIT.createEntity(em);
            em.persist(historyEntry);
            em.flush();
        } else {
            historyEntry = TestUtil.findAll(em, HistoryEntry.class).get(0);
        }
        historyEntryField.setEntry(historyEntry);
        return historyEntryField;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistoryEntryField createUpdatedEntity(EntityManager em) {
        HistoryEntryField historyEntryField = new HistoryEntryField()
            .type(UPDATED_TYPE)
            .transKey(UPDATED_TRANS_KEY)
            .oldValue(UPDATED_OLD_VALUE)
            .newValue(UPDATED_NEW_VALUE);
        // Add required entity
        HistoryEntry historyEntry;
        if (TestUtil.findAll(em, HistoryEntry.class).isEmpty()) {
            historyEntry = HistoryEntryResourceIT.createUpdatedEntity(em);
            em.persist(historyEntry);
            em.flush();
        } else {
            historyEntry = TestUtil.findAll(em, HistoryEntry.class).get(0);
        }
        historyEntryField.setEntry(historyEntry);
        return historyEntryField;
    }

    @BeforeEach
    public void initTest() {
        historyEntryField = createEntity(em);
    }

    @Test
    @Transactional
    void getAllHistoryEntryFields() throws Exception {
        // Initialize the database
        historyEntryFieldRepository.saveAndFlush(historyEntryField);

        // Get all the historyEntryFieldList
        restHistoryEntryFieldMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(historyEntryField.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].transKey").value(hasItem(DEFAULT_TRANS_KEY)))
            .andExpect(jsonPath("$.[*].oldValue").value(hasItem(DEFAULT_OLD_VALUE)))
            .andExpect(jsonPath("$.[*].newValue").value(hasItem(DEFAULT_NEW_VALUE)));
    }

    @Test
    @Transactional
    void getHistoryEntryField() throws Exception {
        // Initialize the database
        historyEntryFieldRepository.saveAndFlush(historyEntryField);

        // Get the historyEntryField
        restHistoryEntryFieldMockMvc
            .perform(get(ENTITY_API_URL_ID, historyEntryField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(historyEntryField.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.transKey").value(DEFAULT_TRANS_KEY))
            .andExpect(jsonPath("$.oldValue").value(DEFAULT_OLD_VALUE))
            .andExpect(jsonPath("$.newValue").value(DEFAULT_NEW_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingHistoryEntryField() throws Exception {
        // Get the historyEntryField
        restHistoryEntryFieldMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void searchHistoryEntryField() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        historyEntryFieldRepository.saveAndFlush(historyEntryField);
        when(mockHistoryEntryFieldSearchRepository.search(queryStringQuery("id:" + historyEntryField.getId())))
            .thenReturn(Collections.singletonList(historyEntryField));

        // Search the historyEntryField
        restHistoryEntryFieldMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + historyEntryField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(historyEntryField.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].transKey").value(hasItem(DEFAULT_TRANS_KEY)))
            .andExpect(jsonPath("$.[*].oldValue").value(hasItem(DEFAULT_OLD_VALUE)))
            .andExpect(jsonPath("$.[*].newValue").value(hasItem(DEFAULT_NEW_VALUE)));
    }
}
