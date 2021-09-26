package dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import dev.hbgl.hhn.schattenbuchhaltung.domain.Tag;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class TagOut {

    public Long id;

    public String text;

    public static TagOut fromEntity(Tag entity) {
        if (entity == null) {
            return null;
        }
        var vm = new TagOut();
        vm.id = entity.getId();
        vm.text = entity.getText();
        return vm;
    }
}
