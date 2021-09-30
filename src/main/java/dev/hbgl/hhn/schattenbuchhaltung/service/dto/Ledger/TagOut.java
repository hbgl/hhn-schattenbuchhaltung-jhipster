package dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import dev.hbgl.hhn.schattenbuchhaltung.domain.Tag;
import dev.hbgl.hhn.schattenbuchhaltung.domain.elasticsearch.ElasticTag;

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

    public static TagOut fromElastic(ElasticTag elasticTag) {
        if (elasticTag == null || elasticTag.content == null) {
            return null;
        }
        var vm = new TagOut();
        vm.id = elasticTag.id;
        vm.text = elasticTag.content.invariant;
        return vm;
    }
}
