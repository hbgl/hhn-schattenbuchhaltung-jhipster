package dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import dev.hbgl.hhn.schattenbuchhaltung.domain.CostType;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class CostTypeVM {

    public Long id;

    public String no;

    public String name;

    public static CostTypeVM fromEntity(CostType entity) {
        if (entity == null) {
            return null;
        }
        var vm = new CostTypeVM();
        vm.id = entity.getId();
        vm.no = entity.getNo();
        vm.name = entity.getName();
        return vm;
    }
}
