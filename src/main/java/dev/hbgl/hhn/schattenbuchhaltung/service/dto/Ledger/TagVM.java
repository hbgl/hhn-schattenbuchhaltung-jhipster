package dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import dev.hbgl.hhn.schattenbuchhaltung.domain.Tag;
import dev.hbgl.hhn.schattenbuchhaltung.domain.enumeration.TagKind;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class TagVM {

    public Long id;

    public TagKind type;

    public String text;

    public UserVM person;

    public String customType;

    public String customValue;

    public static TagVM fromEntity(Tag entity) {
        if (entity == null) {
            return null;
        }
        var vm = new TagVM();
        vm.id = entity.getId();
        var customType = entity.getCustomType();
        var customValue = entity.getCustomValue();
        if (customType != null && customValue != null) {
            vm.customType = customType.getName();
            vm.customValue = customValue.getValue();
        }
        vm.text = entity.getText();
        vm.person = UserVM.fromEntity(entity.getPerson());
        return vm;
    }
}
