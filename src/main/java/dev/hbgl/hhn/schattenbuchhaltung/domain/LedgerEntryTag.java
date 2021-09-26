package dev.hbgl.hhn.schattenbuchhaltung.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
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
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LedgerEntryTag id(Long id) {
        this.id = id;
        return this;
    }

    public String getLedgerEntryNo() {
        return this.ledgerEntryNo;
    }

    public LedgerEntryTag ledgerEntryNo(String ledgerEntryNo) {
        this.ledgerEntryNo = ledgerEntryNo;
        return this;
    }

    public void setLedgerEntryNo(String ledgerEntryNo) {
        this.ledgerEntryNo = ledgerEntryNo;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public LedgerEntryTag priority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LedgerEntry getLedgerEntry() {
        return this.ledgerEntry;
    }

    public LedgerEntryTag ledgerEntry(LedgerEntry ledgerEntry) {
        this.setLedgerEntry(ledgerEntry);
        return this;
    }

    public void setLedgerEntry(LedgerEntry ledgerEntry) {
        this.ledgerEntry = ledgerEntry;
    }

    public Tag getTag() {
        return this.tag;
    }

    public LedgerEntryTag tag(Tag tag) {
        this.setTag(tag);
        return this;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

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
