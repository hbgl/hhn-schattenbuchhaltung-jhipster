package dev.hbgl.hhn.schattenbuchhaltung.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.hbgl.hhn.schattenbuchhaltung.domain.enumeration.HistoryAction;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A HistoryEntry.
 */
@Entity
@Table(name = "history_entry")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HistoryEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "instant", nullable = false)
    private Instant instant;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private HistoryAction action;

    @Lob
    @Column(name = "patch")
    private byte[] patch;

    @Column(name = "patch_content_type")
    private String patchContentType;

    @NotNull
    @Column(name = "rec_type", nullable = false)
    private String recType;

    @NotNull
    @Column(name = "rec_id", nullable = false)
    private Long recId;

    @Column(name = "rec_id_2")
    private Long recId2;

    @OneToMany(mappedBy = "entry")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "entry" }, allowSetters = true)
    private Set<HistoryEntryField> fields = new HashSet<>();

    @ManyToOne
    private User author;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HistoryEntry id(Long id) {
        this.id = id;
        return this;
    }

    public Instant getInstant() {
        return this.instant;
    }

    public HistoryEntry instant(Instant instant) {
        this.instant = instant;
        return this;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public HistoryAction getAction() {
        return this.action;
    }

    public HistoryEntry action(HistoryAction action) {
        this.action = action;
        return this;
    }

    public void setAction(HistoryAction action) {
        this.action = action;
    }

    public byte[] getPatch() {
        return this.patch;
    }

    public HistoryEntry patch(byte[] patch) {
        this.patch = patch;
        return this;
    }

    public void setPatch(byte[] patch) {
        this.patch = patch;
    }

    public String getPatchContentType() {
        return this.patchContentType;
    }

    public HistoryEntry patchContentType(String patchContentType) {
        this.patchContentType = patchContentType;
        return this;
    }

    public void setPatchContentType(String patchContentType) {
        this.patchContentType = patchContentType;
    }

    public String getRecType() {
        return this.recType;
    }

    public HistoryEntry recType(String recType) {
        this.recType = recType;
        return this;
    }

    public void setRecType(String recType) {
        this.recType = recType;
    }

    public Long getRecId() {
        return this.recId;
    }

    public HistoryEntry recId(Long recId) {
        this.recId = recId;
        return this;
    }

    public void setRecId(Long recId) {
        this.recId = recId;
    }

    public Long getRecId2() {
        return this.recId2;
    }

    public HistoryEntry recId2(Long recId2) {
        this.recId2 = recId2;
        return this;
    }

    public void setRecId2(Long recId2) {
        this.recId2 = recId2;
    }

    public Set<HistoryEntryField> getFields() {
        return this.fields;
    }

    public HistoryEntry fields(Set<HistoryEntryField> historyEntryFields) {
        this.setFields(historyEntryFields);
        return this;
    }

    public HistoryEntry addFields(HistoryEntryField historyEntryField) {
        this.fields.add(historyEntryField);
        historyEntryField.setEntry(this);
        return this;
    }

    public HistoryEntry removeFields(HistoryEntryField historyEntryField) {
        this.fields.remove(historyEntryField);
        historyEntryField.setEntry(null);
        return this;
    }

    public void setFields(Set<HistoryEntryField> historyEntryFields) {
        if (this.fields != null) {
            this.fields.forEach(i -> i.setEntry(null));
        }
        if (historyEntryFields != null) {
            historyEntryFields.forEach(i -> i.setEntry(this));
        }
        this.fields = historyEntryFields;
    }

    public User getAuthor() {
        return this.author;
    }

    public HistoryEntry author(User user) {
        this.setAuthor(user);
        return this;
    }

    public void setAuthor(User user) {
        this.author = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HistoryEntry)) {
            return false;
        }
        return id != null && id.equals(((HistoryEntry) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistoryEntry{" +
            "id=" + getId() +
            ", instant='" + getInstant() + "'" +
            ", action='" + getAction() + "'" +
            ", patch='" + getPatch() + "'" +
            ", patchContentType='" + getPatchContentType() + "'" +
            ", recType='" + getRecType() + "'" +
            ", recId=" + getRecId() +
            ", recId2=" + getRecId2() +
            "}";
    }
}
