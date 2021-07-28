package dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import dev.hbgl.hhn.schattenbuchhaltung.domain.Comment;
import java.time.Instant;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class CommentVM {

    public Long id;

    public String contentHtml;

    public Instant createdAt;

    public UserVM author;

    public static CommentVM fromEntity(Comment entity) {
        if (entity == null) {
            return null;
        }
        var vm = new CommentVM();
        vm.id = entity.getId();
        vm.contentHtml = entity.getContentHtml();
        vm.createdAt = entity.getCreatedAt();
        vm.author = UserVM.fromEntity(entity.getAuthor());
        return vm;
    }
}
