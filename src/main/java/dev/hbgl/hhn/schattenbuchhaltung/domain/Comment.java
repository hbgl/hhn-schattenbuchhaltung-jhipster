package dev.hbgl.hhn.schattenbuchhaltung.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
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
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "content_html", nullable = false)
    private String contentHtml;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(optional = false)
    @NotNull
    private User author;

    @ManyToOne
    @JsonIgnoreProperties(
        value = { "comments", "tags", "costCenter1", "costCenter2", "costCenter3", "division", "costType" },
        allowSetters = true
    )
    private LedgerEntry ledgerEntry;

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
            ", contentHtml='" + getContentHtml() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
