package dev.hbgl.hhn.schattenbuchhaltung.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.hbgl.hhn.schattenbuchhaltung.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HistoryEntryFieldTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HistoryEntryField.class);
        HistoryEntryField historyEntryField1 = new HistoryEntryField();
        historyEntryField1.setId(1L);
        HistoryEntryField historyEntryField2 = new HistoryEntryField();
        historyEntryField2.setId(historyEntryField1.getId());
        assertThat(historyEntryField1).isEqualTo(historyEntryField2);
        historyEntryField2.setId(2L);
        assertThat(historyEntryField1).isNotEqualTo(historyEntryField2);
        historyEntryField1.setId(null);
        assertThat(historyEntryField1).isNotEqualTo(historyEntryField2);
    }
}
