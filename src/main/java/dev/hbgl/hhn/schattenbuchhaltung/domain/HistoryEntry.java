package dev.hbgl.hhn.schattenbuchhaltung.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.hbgl.hhn.schattenbuchhaltung.domain.enumeration.HistoryAction;
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
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "instant", nullable = false)
    private Instant instant;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private HistoryAction action;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "rec_type", nullable = false)
    private HistoryEntryRecType recType;

    @NotNull
    @Column(name = "rec_id_1", nullable = false)
    private String recId1;

    @Column(name = "rec_id_2", nullable = false)
    private String recId2;

    @NotNull
    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @OneToMany(mappedBy = "entry")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "entry" }, allowSetters = true)
    private Set<HistoryEntryField> fields = new HashSet<>();

    @ManyToOne
    private User author;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public HistoryEntry id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getInstant() {
        return this.instant;
    }

    public HistoryEntry instant(Instant instant) {
        this.setInstant(instant);
        return this;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public HistoryAction getAction() {
        return this.action;
    }

    public HistoryEntry action(HistoryAction action) {
        this.setAction(action);
        return this;
    }

    public void setAction(HistoryAction action) {
        this.action = action;
    }

    public HistoryEntryRecType getRecType() {
        return this.recType;
    }

    public HistoryEntry recType(HistoryEntryRecType recType) {
        this.setRecType(recType);
        return this;
    }

    public void setRecType(HistoryEntryRecType recType) {
        this.recType = recType;
    }

    public String getRecId1() {
        return this.recId1;
    }

    public HistoryEntry recId1(String recId1) {
        this.setRecId1(recId1);
        return this;
    }

    public void setRecId1(String recId1) {
        this.recId1 = recId1;
    }

    public String getRecId2() {
        return this.recId2;
    }

    public HistoryEntry recId2(String recId2) {
        this.setRecId2(recId2);
        return this;
    }

    public void setRecId2(String recId2) {
        this.recId2 = recId2;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public HistoryEntry uuid(UUID uuid) {
        this.setUuid(uuid);
        return this;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Set<HistoryEntryField> getFields() {
        return this.fields;
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

    public User getAuthor() {
        return this.author;
    }

    public void setAuthor(User user) {
        this.author = user;
    }

    public HistoryEntry author(User user) {
        this.setAuthor(user);
        return this;
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
            ", recType='" + getRecType() + "'" +
            ", recId1='" + getRecId1() + "'" +
            ", recId2='" + getRecId2() + "'" +
            ", uuid='" + getUuid() + "'" +
            "}";
    }
}
