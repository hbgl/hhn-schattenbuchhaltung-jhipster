package dev.hbgl.hhn.schattenbuchhaltung.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.hbgl.hhn.schattenbuchhaltung.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LedgerEntryTagTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LedgerEntryTag.class);
        LedgerEntryTag ledgerEntryTag1 = new LedgerEntryTag();
        ledgerEntryTag1.setId(1L);
        LedgerEntryTag ledgerEntryTag2 = new LedgerEntryTag();
        ledgerEntryTag2.setId(ledgerEntryTag1.getId());
        assertThat(ledgerEntryTag1).isEqualTo(ledgerEntryTag2);
        ledgerEntryTag2.setId(2L);
        assertThat(ledgerEntryTag1).isNotEqualTo(ledgerEntryTag2);
        ledgerEntryTag1.setId(null);
        assertThat(ledgerEntryTag1).isNotEqualTo(ledgerEntryTag2);
    }
}
