package dev.hbgl.hhn.schattenbuchhaltung.web.rest;

import static dev.hbgl.hhn.schattenbuchhaltung.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.hbgl.hhn.schattenbuchhaltung.IntegrationTest;
import dev.hbgl.hhn.schattenbuchhaltung.domain.LedgerEntry;
import dev.hbgl.hhn.schattenbuchhaltung.repository.LedgerEntryRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
 * Integration tests for the {@link LedgerEntryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LedgerEntryResourceIT {

    private static final String DEFAULT_NO = "AAAAAAAAAA";
    private static final String UPDATED_NO = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_A_NO = "AAAAAAAAAA";
    private static final String UPDATED_A_NO = "BBBBBBBBBB";

    private static final Instant DEFAULT_BOOKING_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BOOKING_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_INCOME = new BigDecimal(1);
    private static final BigDecimal UPDATED_INCOME = new BigDecimal(2);

    private static final BigDecimal DEFAULT_EXPENDITURE = new BigDecimal(1);
    private static final BigDecimal UPDATED_EXPENDITURE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_LIABILITY = new BigDecimal(1);
    private static final BigDecimal UPDATED_LIABILITY = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/ledger-entries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LedgerEntryRepository ledgerEntryRepository;

    @Mock
    private LedgerEntryRepository ledgerEntryRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLedgerEntryMockMvc;

    private LedgerEntry ledgerEntry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LedgerEntry createEntity(EntityManager em) {
        LedgerEntry ledgerEntry = new LedgerEntry()
            .no(DEFAULT_NO)
            .description(DEFAULT_DESCRIPTION)
            .aNo(DEFAULT_A_NO)
            .bookingDate(DEFAULT_BOOKING_DATE)
            .income(DEFAULT_INCOME)
            .expenditure(DEFAULT_EXPENDITURE)
            .liability(DEFAULT_LIABILITY);
        return ledgerEntry;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LedgerEntry createUpdatedEntity(EntityManager em) {
        LedgerEntry ledgerEntry = new LedgerEntry()
            .no(UPDATED_NO)
            .description(UPDATED_DESCRIPTION)
            .aNo(UPDATED_A_NO)
            .bookingDate(UPDATED_BOOKING_DATE)
            .income(UPDATED_INCOME)
            .expenditure(UPDATED_EXPENDITURE)
            .liability(UPDATED_LIABILITY);
        return ledgerEntry;
    }

    @BeforeEach
    public void initTest() {
        ledgerEntry = createEntity(em);
    }

    @Test
    @Transactional
    void createLedgerEntry() throws Exception {
        int databaseSizeBeforeCreate = ledgerEntryRepository.findAll().size();
        // Create the LedgerEntry
        restLedgerEntryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ledgerEntry))
            )
            .andExpect(status().isCreated());

        // Validate the LedgerEntry in the database
        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeCreate + 1);
        LedgerEntry testLedgerEntry = ledgerEntryList.get(ledgerEntryList.size() - 1);
        assertThat(testLedgerEntry.getNo()).isEqualTo(DEFAULT_NO);
        assertThat(testLedgerEntry.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testLedgerEntry.getaNo()).isEqualTo(DEFAULT_A_NO);
        assertThat(testLedgerEntry.getBookingDate()).isEqualTo(DEFAULT_BOOKING_DATE);
        assertThat(testLedgerEntry.getIncome()).isEqualByComparingTo(DEFAULT_INCOME);
        assertThat(testLedgerEntry.getExpenditure()).isEqualByComparingTo(DEFAULT_EXPENDITURE);
        assertThat(testLedgerEntry.getLiability()).isEqualByComparingTo(DEFAULT_LIABILITY);
    }

    @Test
    @Transactional
    void createLedgerEntryWithExistingId() throws Exception {
        // Create the LedgerEntry with an existing ID
        ledgerEntry.setId(1L);

        int databaseSizeBeforeCreate = ledgerEntryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLedgerEntryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ledgerEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the LedgerEntry in the database
        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNoIsRequired() throws Exception {
        int databaseSizeBeforeTest = ledgerEntryRepository.findAll().size();
        // set the field null
        ledgerEntry.setNo(null);

        // Create the LedgerEntry, which fails.

        restLedgerEntryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ledgerEntry))
            )
            .andExpect(status().isBadRequest());

        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = ledgerEntryRepository.findAll().size();
        // set the field null
        ledgerEntry.setDescription(null);

        // Create the LedgerEntry, which fails.

        restLedgerEntryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ledgerEntry))
            )
            .andExpect(status().isBadRequest());

        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBookingDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = ledgerEntryRepository.findAll().size();
        // set the field null
        ledgerEntry.setBookingDate(null);

        // Create the LedgerEntry, which fails.

        restLedgerEntryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ledgerEntry))
            )
            .andExpect(status().isBadRequest());

        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIncomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = ledgerEntryRepository.findAll().size();
        // set the field null
        ledgerEntry.setIncome(null);

        // Create the LedgerEntry, which fails.

        restLedgerEntryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ledgerEntry))
            )
            .andExpect(status().isBadRequest());

        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExpenditureIsRequired() throws Exception {
        int databaseSizeBeforeTest = ledgerEntryRepository.findAll().size();
        // set the field null
        ledgerEntry.setExpenditure(null);

        // Create the LedgerEntry, which fails.

        restLedgerEntryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ledgerEntry))
            )
            .andExpect(status().isBadRequest());

        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLiabilityIsRequired() throws Exception {
        int databaseSizeBeforeTest = ledgerEntryRepository.findAll().size();
        // set the field null
        ledgerEntry.setLiability(null);

        // Create the LedgerEntry, which fails.

        restLedgerEntryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ledgerEntry))
            )
            .andExpect(status().isBadRequest());

        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLedgerEntries() throws Exception {
        // Initialize the database
        ledgerEntryRepository.saveAndFlush(ledgerEntry);

        // Get all the ledgerEntryList
        restLedgerEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ledgerEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].no").value(hasItem(DEFAULT_NO)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].aNo").value(hasItem(DEFAULT_A_NO)))
            .andExpect(jsonPath("$.[*].bookingDate").value(hasItem(DEFAULT_BOOKING_DATE.toString())))
            .andExpect(jsonPath("$.[*].income").value(hasItem(sameNumber(DEFAULT_INCOME))))
            .andExpect(jsonPath("$.[*].expenditure").value(hasItem(sameNumber(DEFAULT_EXPENDITURE))))
            .andExpect(jsonPath("$.[*].liability").value(hasItem(sameNumber(DEFAULT_LIABILITY))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLedgerEntriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(ledgerEntryRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLedgerEntryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ledgerEntryRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLedgerEntriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ledgerEntryRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLedgerEntryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ledgerEntryRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getLedgerEntry() throws Exception {
        // Initialize the database
        ledgerEntryRepository.saveAndFlush(ledgerEntry);

        // Get the ledgerEntry
        restLedgerEntryMockMvc
            .perform(get(ENTITY_API_URL_ID, ledgerEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ledgerEntry.getId().intValue()))
            .andExpect(jsonPath("$.no").value(DEFAULT_NO))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.aNo").value(DEFAULT_A_NO))
            .andExpect(jsonPath("$.bookingDate").value(DEFAULT_BOOKING_DATE.toString()))
            .andExpect(jsonPath("$.income").value(sameNumber(DEFAULT_INCOME)))
            .andExpect(jsonPath("$.expenditure").value(sameNumber(DEFAULT_EXPENDITURE)))
            .andExpect(jsonPath("$.liability").value(sameNumber(DEFAULT_LIABILITY)));
    }

    @Test
    @Transactional
    void getNonExistingLedgerEntry() throws Exception {
        // Get the ledgerEntry
        restLedgerEntryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLedgerEntry() throws Exception {
        // Initialize the database
        ledgerEntryRepository.saveAndFlush(ledgerEntry);

        int databaseSizeBeforeUpdate = ledgerEntryRepository.findAll().size();

        // Update the ledgerEntry
        LedgerEntry updatedLedgerEntry = ledgerEntryRepository.findById(ledgerEntry.getId()).get();
        // Disconnect from session so that the updates on updatedLedgerEntry are not directly saved in db
        em.detach(updatedLedgerEntry);
        updatedLedgerEntry
            .no(UPDATED_NO)
            .description(UPDATED_DESCRIPTION)
            .aNo(UPDATED_A_NO)
            .bookingDate(UPDATED_BOOKING_DATE)
            .income(UPDATED_INCOME)
            .expenditure(UPDATED_EXPENDITURE)
            .liability(UPDATED_LIABILITY);

        restLedgerEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLedgerEntry.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLedgerEntry))
            )
            .andExpect(status().isOk());

        // Validate the LedgerEntry in the database
        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeUpdate);
        LedgerEntry testLedgerEntry = ledgerEntryList.get(ledgerEntryList.size() - 1);
        assertThat(testLedgerEntry.getNo()).isEqualTo(UPDATED_NO);
        assertThat(testLedgerEntry.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testLedgerEntry.getaNo()).isEqualTo(UPDATED_A_NO);
        assertThat(testLedgerEntry.getBookingDate()).isEqualTo(UPDATED_BOOKING_DATE);
        assertThat(testLedgerEntry.getIncome()).isEqualTo(UPDATED_INCOME);
        assertThat(testLedgerEntry.getExpenditure()).isEqualTo(UPDATED_EXPENDITURE);
        assertThat(testLedgerEntry.getLiability()).isEqualTo(UPDATED_LIABILITY);
    }

    @Test
    @Transactional
    void putNonExistingLedgerEntry() throws Exception {
        int databaseSizeBeforeUpdate = ledgerEntryRepository.findAll().size();
        ledgerEntry.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLedgerEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ledgerEntry.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ledgerEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the LedgerEntry in the database
        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLedgerEntry() throws Exception {
        int databaseSizeBeforeUpdate = ledgerEntryRepository.findAll().size();
        ledgerEntry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLedgerEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ledgerEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the LedgerEntry in the database
        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLedgerEntry() throws Exception {
        int databaseSizeBeforeUpdate = ledgerEntryRepository.findAll().size();
        ledgerEntry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLedgerEntryMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ledgerEntry))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LedgerEntry in the database
        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLedgerEntryWithPatch() throws Exception {
        // Initialize the database
        ledgerEntryRepository.saveAndFlush(ledgerEntry);

        int databaseSizeBeforeUpdate = ledgerEntryRepository.findAll().size();

        // Update the ledgerEntry using partial update
        LedgerEntry partialUpdatedLedgerEntry = new LedgerEntry();
        partialUpdatedLedgerEntry.setId(ledgerEntry.getId());

        partialUpdatedLedgerEntry
            .aNo(UPDATED_A_NO)
            .bookingDate(UPDATED_BOOKING_DATE)
            .expenditure(UPDATED_EXPENDITURE)
            .liability(UPDATED_LIABILITY);

        restLedgerEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLedgerEntry.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLedgerEntry))
            )
            .andExpect(status().isOk());

        // Validate the LedgerEntry in the database
        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeUpdate);
        LedgerEntry testLedgerEntry = ledgerEntryList.get(ledgerEntryList.size() - 1);
        assertThat(testLedgerEntry.getNo()).isEqualTo(DEFAULT_NO);
        assertThat(testLedgerEntry.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testLedgerEntry.getaNo()).isEqualTo(UPDATED_A_NO);
        assertThat(testLedgerEntry.getBookingDate()).isEqualTo(UPDATED_BOOKING_DATE);
        assertThat(testLedgerEntry.getIncome()).isEqualByComparingTo(DEFAULT_INCOME);
        assertThat(testLedgerEntry.getExpenditure()).isEqualByComparingTo(UPDATED_EXPENDITURE);
        assertThat(testLedgerEntry.getLiability()).isEqualByComparingTo(UPDATED_LIABILITY);
    }

    @Test
    @Transactional
    void fullUpdateLedgerEntryWithPatch() throws Exception {
        // Initialize the database
        ledgerEntryRepository.saveAndFlush(ledgerEntry);

        int databaseSizeBeforeUpdate = ledgerEntryRepository.findAll().size();

        // Update the ledgerEntry using partial update
        LedgerEntry partialUpdatedLedgerEntry = new LedgerEntry();
        partialUpdatedLedgerEntry.setId(ledgerEntry.getId());

        partialUpdatedLedgerEntry
            .no(UPDATED_NO)
            .description(UPDATED_DESCRIPTION)
            .aNo(UPDATED_A_NO)
            .bookingDate(UPDATED_BOOKING_DATE)
            .income(UPDATED_INCOME)
            .expenditure(UPDATED_EXPENDITURE)
            .liability(UPDATED_LIABILITY);

        restLedgerEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLedgerEntry.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLedgerEntry))
            )
            .andExpect(status().isOk());

        // Validate the LedgerEntry in the database
        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeUpdate);
        LedgerEntry testLedgerEntry = ledgerEntryList.get(ledgerEntryList.size() - 1);
        assertThat(testLedgerEntry.getNo()).isEqualTo(UPDATED_NO);
        assertThat(testLedgerEntry.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testLedgerEntry.getaNo()).isEqualTo(UPDATED_A_NO);
        assertThat(testLedgerEntry.getBookingDate()).isEqualTo(UPDATED_BOOKING_DATE);
        assertThat(testLedgerEntry.getIncome()).isEqualByComparingTo(UPDATED_INCOME);
        assertThat(testLedgerEntry.getExpenditure()).isEqualByComparingTo(UPDATED_EXPENDITURE);
        assertThat(testLedgerEntry.getLiability()).isEqualByComparingTo(UPDATED_LIABILITY);
    }

    @Test
    @Transactional
    void patchNonExistingLedgerEntry() throws Exception {
        int databaseSizeBeforeUpdate = ledgerEntryRepository.findAll().size();
        ledgerEntry.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLedgerEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ledgerEntry.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ledgerEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the LedgerEntry in the database
        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLedgerEntry() throws Exception {
        int databaseSizeBeforeUpdate = ledgerEntryRepository.findAll().size();
        ledgerEntry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLedgerEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ledgerEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the LedgerEntry in the database
        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLedgerEntry() throws Exception {
        int databaseSizeBeforeUpdate = ledgerEntryRepository.findAll().size();
        ledgerEntry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLedgerEntryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ledgerEntry))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LedgerEntry in the database
        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLedgerEntry() throws Exception {
        // Initialize the database
        ledgerEntryRepository.saveAndFlush(ledgerEntry);

        int databaseSizeBeforeDelete = ledgerEntryRepository.findAll().size();

        // Delete the ledgerEntry
        restLedgerEntryMockMvc
            .perform(delete(ENTITY_API_URL_ID, ledgerEntry.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LedgerEntry> ledgerEntryList = ledgerEntryRepository.findAll();
        assertThat(ledgerEntryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
