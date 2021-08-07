package dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import dev.hbgl.hhn.schattenbuchhaltung.domain.Comment;
import java.time.Instant;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class CommentOut {

    public Long id;

    public String contentHtml;

    public Instant createdAt;

    public UserOut author;

    public static CommentOut fromEntity(Comment entity) {
        if (entity == null) {
            return null;
        }
        var vm = new CommentOut();
        vm.id = entity.getId();
        vm.contentHtml = entity.getContentHtml();
        vm.createdAt = entity.getCreatedAt();
        vm.author = UserOut.fromEntity(entity.getAuthor());
        return vm;
    }
}
