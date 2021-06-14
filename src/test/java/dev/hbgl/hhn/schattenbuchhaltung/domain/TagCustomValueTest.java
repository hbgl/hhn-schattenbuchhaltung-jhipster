package dev.hbgl.hhn.schattenbuchhaltung.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.hbgl.hhn.schattenbuchhaltung.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TagCustomValueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TagCustomValue.class);
        TagCustomValue tagCustomValue1 = new TagCustomValue();
        tagCustomValue1.setId(1L);
        TagCustomValue tagCustomValue2 = new TagCustomValue();
        tagCustomValue2.setId(tagCustomValue1.getId());
        assertThat(tagCustomValue1).isEqualTo(tagCustomValue2);
        tagCustomValue2.setId(2L);
        assertThat(tagCustomValue1).isNotEqualTo(tagCustomValue2);
        tagCustomValue1.setId(null);
        assertThat(tagCustomValue1).isNotEqualTo(tagCustomValue2);
    }
}
