package dev.hbgl.hhn.schattenbuchhaltung.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.hbgl.hhn.schattenbuchhaltung.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TagCustomTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TagCustomType.class);
        TagCustomType tagCustomType1 = new TagCustomType();
        tagCustomType1.setId(1L);
        TagCustomType tagCustomType2 = new TagCustomType();
        tagCustomType2.setId(tagCustomType1.getId());
        assertThat(tagCustomType1).isEqualTo(tagCustomType2);
        tagCustomType2.setId(2L);
        assertThat(tagCustomType1).isNotEqualTo(tagCustomType2);
        tagCustomType1.setId(null);
        assertThat(tagCustomType1).isNotEqualTo(tagCustomType2);
    }
}
