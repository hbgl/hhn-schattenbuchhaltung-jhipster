package dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import dev.hbgl.hhn.schattenbuchhaltung.domain.Comment;
import javax.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class CommentCreateIn {

    public Long parentId;

    @Size(min = 1, max = Comment.CONTENT_HTML_LENGTH)
    public String contentHtml;

    public String ledgerEntryNo;
}
