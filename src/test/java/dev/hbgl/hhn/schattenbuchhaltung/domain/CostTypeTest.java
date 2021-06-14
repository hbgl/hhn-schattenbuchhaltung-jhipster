package dev.hbgl.hhn.schattenbuchhaltung.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.hbgl.hhn.schattenbuchhaltung.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CostTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CostType.class);
        CostType costType1 = new CostType();
        costType1.setId(1L);
        CostType costType2 = new CostType();
        costType2.setId(costType1.getId());
        assertThat(costType1).isEqualTo(costType2);
        costType2.setId(2L);
        assertThat(costType1).isNotEqualTo(costType2);
        costType1.setId(null);
        assertThat(costType1).isNotEqualTo(costType2);
    }
}
