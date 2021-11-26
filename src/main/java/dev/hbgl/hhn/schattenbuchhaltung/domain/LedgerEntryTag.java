package dev.hbgl.hhn.schattenbuchhaltung.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.hbgl.hhn.schattenbuchhaltung.domain.enumeration.HistoryAction;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LedgerEntryTag.
 */
@Entity
@Table(name = "ledger_entry_tag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LedgerEntryTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "ledger_entry_no", nullable = false)
    private String ledgerEntryNo;

    @NotNull
    @Column(name = "priority", nullable = false)
    private Integer priority;

    @ManyToOne
    @JsonIgnoreProperties(
        value = { "comments", "ledgerEntryTags", "costCenter1", "costCenter2", "costCenter3", "division", "costType" },
        allowSetters = true
    )
    private LedgerEntry ledgerEntry;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "ledgerEntryTags" }, allowSetters = true)
    private Tag tag;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LedgerEntryTag id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLedgerEntryNo() {
        return this.ledgerEntryNo;
    }

    public LedgerEntryTag ledgerEntryNo(String ledgerEntryNo) {
        this.setLedgerEntryNo(ledgerEntryNo);
        return this;
    }

    public void setLedgerEntryNo(String ledgerEntryNo) {
        this.ledgerEntryNo = ledgerEntryNo;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public LedgerEntryTag priority(Integer priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LedgerEntry getLedgerEntry() {
        return this.ledgerEntry;
    }

    public void setLedgerEntry(LedgerEntry ledgerEntry) {
        this.ledgerEntry = ledgerEntry;
    }

    public LedgerEntryTag ledgerEntry(LedgerEntry ledgerEntry) {
        this.setLedgerEntry(ledgerEntry);
        return this;
    }

    public Tag getTag() {
        return this.tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public LedgerEntryTag tag(Tag tag) {
        this.setTag(tag);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    public static HistoryEntry historyEntryCreate(LedgerEntryTag ledgerEntryTag, User user) {
        var entry = new HistoryEntry();
        entry.setUuid(UUID.randomUUID());
        entry.setAuthor(user);
        entry.setAction(HistoryAction.CREATE);
        entry.setRecId1(ledgerEntryTag.getLedgerEntryNo());
        entry.setRecId2(ledgerEntryTag.getTag().getTextNormalized());
        entry.setRecType(HistoryEntryRecType.LEDGER_ENTRY_TAG);
        entry.setInstant(Instant.now());

        var fieldPriority = new HistoryEntryField();
        fieldPriority.setName("priority");
        fieldPriority.setEntry(entry);
        fieldPriority.setOldValue(null);
        fieldPriority.setNewValue(ledgerEntryTag.getPriority().toString());

        var fields = Set.of(fieldPriority);
        entry.setFields(fields);

        return entry;
    }

    public static HistoryEntry historyEntryDelete(LedgerEntryTag ledgerEntryTag, User user) {
        var entry = new HistoryEntry();
        entry.setUuid(UUID.randomUUID());
        entry.setAuthor(user);
        entry.setAction(HistoryAction.DELETE);
        entry.setRecId1(ledgerEntryTag.getLedgerEntryNo());
        entry.setRecId2(ledgerEntryTag.getTag().getTextNormalized());
        entry.setRecType(HistoryEntryRecType.LEDGER_ENTRY_TAG);
        entry.setInstant(Instant.now());
        entry.setFields(Set.of());
        return entry;
    }

    public static HistoryEntry historyEntryModify(LedgerEntryTag oldLedgerEntryTag, LedgerEntryTag newLedgerEntryTag, User user) {
        var fields = new HashSet<HistoryEntryField>();

        if (!Objects.equals(oldLedgerEntryTag.getPriority(), newLedgerEntryTag.getPriority())) {
            fields.add(
                new HistoryEntryField()
                    .name("priority")
                    .oldValue(oldLedgerEntryTag.getPriority().toString())
                    .newValue(newLedgerEntryTag.getPriority().toString())
            );
        }

        if (fields.isEmpty()) {
            return null;
        }

        var entry = new HistoryEntry();
        entry.setUuid(UUID.randomUUID());
        entry.setAuthor(user);
        entry.setAction(HistoryAction.MODIFY);
        entry.setRecId1(oldLedgerEntryTag.getLedgerEntryNo());
        entry.setRecId2(oldLedgerEntryTag.getTag().getTextNormalized());
        entry.setRecType(HistoryEntryRecType.LEDGER_ENTRY_TAG);
        entry.setInstant(Instant.now());

        for (var field : fields) {
            field.setEntry(entry);
        }

        entry.setFields(fields);
        return entry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LedgerEntryTag)) {
            return false;
        }
        return id != null && id.equals(((LedgerEntryTag) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LedgerEntryTag{" +
            "id=" + getId() +
            ", ledgerEntryNo='" + getLedgerEntryNo() + "'" +
            ", priority=" + getPriority() +
            "}";
    }
}
