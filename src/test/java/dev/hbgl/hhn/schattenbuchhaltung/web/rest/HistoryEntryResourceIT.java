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
import dev.hbgl.hhn.schattenbuchhaltung.domain.enumeration.HistoryAction;
import dev.hbgl.hhn.schattenbuchhaltung.repository.HistoryEntryRepository;
import dev.hbgl.hhn.schattenbuchhaltung.repository.search.HistoryEntrySearchRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link HistoryEntryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class HistoryEntryResourceIT {

    private static final Instant DEFAULT_INSTANT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_INSTANT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final HistoryAction DEFAULT_ACTION = HistoryAction.CREATE;
    private static final HistoryAction UPDATED_ACTION = HistoryAction.MODIFY;

    private static final byte[] DEFAULT_PATCH = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PATCH = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PATCH_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PATCH_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_ENTITY_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ENTITY_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_ENTITY_ID = 1L;
    private static final Long UPDATED_ENTITY_ID = 2L;

    private static final String ENTITY_API_URL = "/api/history-entries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/history-entries";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HistoryEntryRepository historyEntryRepository;

    /**
     * This repository is mocked in the dev.hbgl.hhn.schattenbuchhaltung.repository.search test package.
     *
     * @see dev.hbgl.hhn.schattenbuchhaltung.repository.search.HistoryEntrySearchRepositoryMockConfiguration
     */
    @Autowired
    private HistoryEntrySearchRepository mockHistoryEntrySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHistoryEntryMockMvc;

    private HistoryEntry historyEntry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistoryEntry createEntity(EntityManager em) {
        HistoryEntry historyEntry = new HistoryEntry()
            .instant(DEFAULT_INSTANT)
            .action(DEFAULT_ACTION)
            .patch(DEFAULT_PATCH)
            .patchContentType(DEFAULT_PATCH_CONTENT_TYPE)
            .entityType(DEFAULT_ENTITY_TYPE)
            .entityId(DEFAULT_ENTITY_ID);
        return historyEntry;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistoryEntry createUpdatedEntity(EntityManager em) {
        HistoryEntry historyEntry = new HistoryEntry()
            .instant(UPDATED_INSTANT)
            .action(UPDATED_ACTION)
            .patch(UPDATED_PATCH)
            .patchContentType(UPDATED_PATCH_CONTENT_TYPE)
            .entityType(UPDATED_ENTITY_TYPE)
            .entityId(UPDATED_ENTITY_ID);
        return historyEntry;
    }

    @BeforeEach
    public void initTest() {
        historyEntry = createEntity(em);
    }

    @Test
    @Transactional
    void getAllHistoryEntries() throws Exception {
        // Initialize the database
        historyEntryRepository.saveAndFlush(historyEntry);

        // Get all the historyEntryList
        restHistoryEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(historyEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].instant").value(hasItem(DEFAULT_INSTANT.toString())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())))
            .andExpect(jsonPath("$.[*].patchContentType").value(hasItem(DEFAULT_PATCH_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].patch").value(hasItem(Base64Utils.encodeToString(DEFAULT_PATCH))))
            .andExpect(jsonPath("$.[*].entityType").value(hasItem(DEFAULT_ENTITY_TYPE)))
            .andExpect(jsonPath("$.[*].entityId").value(hasItem(DEFAULT_ENTITY_ID.intValue())));
    }

    @Test
    @Transactional
    void getHistoryEntry() throws Exception {
        // Initialize the database
        historyEntryRepository.saveAndFlush(historyEntry);

        // Get the historyEntry
        restHistoryEntryMockMvc
            .perform(get(ENTITY_API_URL_ID, historyEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(historyEntry.getId().intValue()))
            .andExpect(jsonPath("$.instant").value(DEFAULT_INSTANT.toString()))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION.toString()))
            .andExpect(jsonPath("$.patchContentType").value(DEFAULT_PATCH_CONTENT_TYPE))
            .andExpect(jsonPath("$.patch").value(Base64Utils.encodeToString(DEFAULT_PATCH)))
            .andExpect(jsonPath("$.entityType").value(DEFAULT_ENTITY_TYPE))
            .andExpect(jsonPath("$.entityId").value(DEFAULT_ENTITY_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingHistoryEntry() throws Exception {
        // Get the historyEntry
        restHistoryEntryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void searchHistoryEntry() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        historyEntryRepository.saveAndFlush(historyEntry);
        when(mockHistoryEntrySearchRepository.search(queryStringQuery("id:" + historyEntry.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(historyEntry), PageRequest.of(0, 1), 1));

        // Search the historyEntry
        restHistoryEntryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + historyEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(historyEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].instant").value(hasItem(DEFAULT_INSTANT.toString())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())))
            .andExpect(jsonPath("$.[*].patchContentType").value(hasItem(DEFAULT_PATCH_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].patch").value(hasItem(Base64Utils.encodeToString(DEFAULT_PATCH))))
            .andExpect(jsonPath("$.[*].entityType").value(hasItem(DEFAULT_ENTITY_TYPE)))
            .andExpect(jsonPath("$.[*].entityId").value(hasItem(DEFAULT_ENTITY_ID.intValue())));
    }
}
