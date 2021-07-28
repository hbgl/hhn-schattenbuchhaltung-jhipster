package dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import dev.hbgl.hhn.schattenbuchhaltung.domain.CostCenter;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class CostCenterVM {

    public Long id;

    public String no;

    public String name;

    public Integer rank;

    public static CostCenterVM fromEntity(CostCenter entity) {
        if (entity == null) {
            return null;
        }
        var vm = new CostCenterVM();
        vm.id = entity.getId();
        vm.no = entity.getNo();
        vm.name = entity.getName();
        vm.rank = entity.getRank();
        return vm;
    }
}
