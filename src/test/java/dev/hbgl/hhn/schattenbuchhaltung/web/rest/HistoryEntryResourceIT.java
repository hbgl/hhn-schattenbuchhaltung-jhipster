package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.hbgl.hhn.schattenbuchhaltung.IntegrationTest;
import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntry;
import dev.hbgl.hhn.schattenbuchhaltung.domain.enumeration.HistoryAction;
import dev.hbgl.hhn.schattenbuchhaltung.repository.HistoryEntryRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link HistoryEntryResource} REST controller.
 */
@IntegrationTest
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

    private static final String DEFAULT_REC_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_REC_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_REC_ID = 1L;
    private static final Long UPDATED_REC_ID = 2L;

    private static final Long DEFAULT_REC_ID_2 = 1L;
    private static final Long UPDATED_REC_ID_2 = 2L;

    private static final String ENTITY_API_URL = "/api/history-entries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HistoryEntryRepository historyEntryRepository;

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
            .recType(DEFAULT_REC_TYPE)
            .recId(DEFAULT_REC_ID)
            .recId2(DEFAULT_REC_ID_2);
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
            .recType(UPDATED_REC_TYPE)
            .recId(UPDATED_REC_ID)
            .recId2(UPDATED_REC_ID_2);
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
            .andExpect(jsonPath("$.[*].recType").value(hasItem(DEFAULT_REC_TYPE)))
            .andExpect(jsonPath("$.[*].recId").value(hasItem(DEFAULT_REC_ID.intValue())))
            .andExpect(jsonPath("$.[*].recId2").value(hasItem(DEFAULT_REC_ID_2.intValue())));
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
            .andExpect(jsonPath("$.recType").value(DEFAULT_REC_TYPE))
            .andExpect(jsonPath("$.recId").value(DEFAULT_REC_ID.intValue()))
            .andExpect(jsonPath("$.recId2").value(DEFAULT_REC_ID_2.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingHistoryEntry() throws Exception {
        // Get the historyEntry
        restHistoryEntryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }
}
