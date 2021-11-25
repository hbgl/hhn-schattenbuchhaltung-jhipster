package dev.hbgl.hhn.schattenbuchhaltung.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Comment.
 */
@Entity
@Table(name = "comment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int CONTENT_HTML_LENGTH = 10000;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "ledger_entry_no", nullable = false)
    private String ledgerEntryNo;

    @NotNull
    @Column(name = "content_html", nullable = false, length = CONTENT_HTML_LENGTH)
    private String contentHtml;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "uuid", nullable = false)
    private UUID uuid;

    @ManyToOne(optional = false)
    @NotNull
    private User author;

    @ManyToOne
    @JsonIgnoreProperties(
        value = { "comments", "ledgerEntryTags", "costCenter1", "costCenter2", "costCenter3", "division", "costType" },
        allowSetters = true
    )
    private LedgerEntry ledgerEntry;

    @OneToMany(mappedBy = "parent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "author", "ledgerEntry", "children", "parent" }, allowSetters = true)
    private Set<Comment> children = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "author", "ledgerEntry", "children", "parent" }, allowSetters = true)
    private Comment parent;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Comment id(Long id) {
        this.id = id;
        return this;
    }

    public String getLedgerEntryNo() {
        return this.ledgerEntryNo;
    }

    public Comment ledgerEntryNo(String ledgerEntryNo) {
        this.ledgerEntryNo = ledgerEntryNo;
        return this;
    }

    public void setLedgerEntryNo(String ledgerEntryNo) {
        this.ledgerEntryNo = ledgerEntryNo;
    }

    public String getContentHtml() {
        return this.contentHtml;
    }

    public Comment contentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
        return this;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Comment createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Comment uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public User getAuthor() {
        return this.author;
    }

    public Comment author(User user) {
        this.setAuthor(user);
        return this;
    }

    public void setAuthor(User user) {
        this.author = user;
    }

    public LedgerEntry getLedgerEntry() {
        return this.ledgerEntry;
    }

    public Comment ledgerEntry(LedgerEntry ledgerEntry) {
        this.setLedgerEntry(ledgerEntry);
        return this;
    }

    public void setLedgerEntry(LedgerEntry ledgerEntry) {
        this.ledgerEntry = ledgerEntry;
    }

    public Set<Comment> getChildren() {
        return this.children;
    }

    public Comment children(Set<Comment> comments) {
        this.setChildren(comments);
        return this;
    }

    public Comment addChildren(Comment comment) {
        this.children.add(comment);
        comment.setParent(this);
        return this;
    }

    public Comment removeChildren(Comment comment) {
        this.children.remove(comment);
        comment.setParent(null);
        return this;
    }

    public void setChildren(Set<Comment> comments) {
        if (this.children != null) {
            this.children.forEach(i -> i.setParent(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setParent(this));
        }
        this.children = comments;
    }

    public Comment getParent() {
        return this.parent;
    }

    public Comment parent(Comment comment) {
        this.setParent(comment);
        return this;
    }

    public void setParent(Comment comment) {
        this.parent = comment;
    }

    public boolean isOwnedBy(User user) {
        return this.getAuthor().getId().equals(user.getId());
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Comment)) {
            return false;
        }
        return id != null && id.equals(((Comment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Comment{" +
            "id=" + getId() +
            ", ledgerEntryNo='" + getLedgerEntryNo() + "'" +
            ", contentHtml='" + getContentHtml() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
