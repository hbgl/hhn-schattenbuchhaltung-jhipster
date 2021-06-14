package dev.hbgl.hhn.schattenbuchhaltung.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.hbgl.hhn.schattenbuchhaltung.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CostCenterTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CostCenter.class);
        CostCenter costCenter1 = new CostCenter();
        costCenter1.setId(1L);
        CostCenter costCenter2 = new CostCenter();
        costCenter2.setId(costCenter1.getId());
        assertThat(costCenter1).isEqualTo(costCenter2);
        costCenter2.setId(2L);
        assertThat(costCenter1).isNotEqualTo(costCenter2);
        costCenter1.setId(null);
        assertThat(costCenter1).isNotEqualTo(costCenter2);
    }
}
