package dev.hbgl.hhn.schattenbuchhaltung.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.hbgl.hhn.schattenbuchhaltung.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HistoryEntryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HistoryEntry.class);
        HistoryEntry historyEntry1 = new HistoryEntry();
        historyEntry1.setId(1L);
        HistoryEntry historyEntry2 = new HistoryEntry();
        historyEntry2.setId(historyEntry1.getId());
        assertThat(historyEntry1).isEqualTo(historyEntry2);
        historyEntry2.setId(2L);
        assertThat(historyEntry1).isNotEqualTo(historyEntry2);
        historyEntry1.setId(null);
        assertThat(historyEntry1).isNotEqualTo(historyEntry2);
    }
}
