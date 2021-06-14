package dev.hbgl.hhn.schattenbuchhaltung.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.hbgl.hhn.schattenbuchhaltung.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LedgerEntryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LedgerEntry.class);
        LedgerEntry ledgerEntry1 = new LedgerEntry();
        ledgerEntry1.setId(1L);
        LedgerEntry ledgerEntry2 = new LedgerEntry();
        ledgerEntry2.setId(ledgerEntry1.getId());
        assertThat(ledgerEntry1).isEqualTo(ledgerEntry2);
        ledgerEntry2.setId(2L);
        assertThat(ledgerEntry1).isNotEqualTo(ledgerEntry2);
        ledgerEntry1.setId(null);
        assertThat(ledgerEntry1).isNotEqualTo(ledgerEntry2);
    }
}
