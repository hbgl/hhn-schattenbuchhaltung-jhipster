package dev.hbgl.hhn.schattenbuchhaltung.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A HistoryEntryField.
 */
@Entity
@Table(name = "history_entry_field")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "historyentryfield")
public class HistoryEntryField implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "type", nullable = false)
    private String type;

    @NotNull
    @Column(name = "trans_key", nullable = false)
    private String transKey;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "fields", "author" }, allowSetters = true)
    private HistoryEntry entry;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HistoryEntryField id(Long id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return this.type;
    }

    public HistoryEntryField type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTransKey() {
        return this.transKey;
    }

    public HistoryEntryField transKey(String transKey) {
        this.transKey = transKey;
        return this;
    }

    public void setTransKey(String transKey) {
        this.transKey = transKey;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public HistoryEntryField oldValue(String oldValue) {
        this.oldValue = oldValue;
        return this;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public HistoryEntryField newValue(String newValue) {
        this.newValue = newValue;
        return this;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public HistoryEntry getEntry() {
        return this.entry;
    }

    public HistoryEntryField entry(HistoryEntry historyEntry) {
        this.setEntry(historyEntry);
        return this;
    }

    public void setEntry(HistoryEntry historyEntry) {
        this.entry = historyEntry;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HistoryEntryField)) {
            return false;
        }
        return id != null && id.equals(((HistoryEntryField) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistoryEntryField{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", transKey='" + getTransKey() + "'" +
            ", oldValue='" + getOldValue() + "'" +
            ", newValue='" + getNewValue() + "'" +
            "}";
    }
}
