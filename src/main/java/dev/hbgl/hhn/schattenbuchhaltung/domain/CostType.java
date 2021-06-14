package dev.hbgl.hhn.schattenbuchhaltung.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A CostType.
 */
@Entity
@Table(name = "cost_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "costtype")
public class CostType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "no", nullable = false)
    private String no;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "costType")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "comments", "costCenter1", "costCenter2", "costCenter3", "division", "costType" }, allowSetters = true)
    private Set<LedgerEntry> ledgerEntries = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CostType id(Long id) {
        this.id = id;
        return this;
    }

    public String getNo() {
        return this.no;
    }

    public CostType no(String no) {
        this.no = no;
        return this;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return this.name;
    }

    public CostType name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<LedgerEntry> getLedgerEntries() {
        return this.ledgerEntries;
    }

    public CostType ledgerEntries(Set<LedgerEntry> ledgerEntries) {
        this.setLedgerEntries(ledgerEntries);
        return this;
    }

    public CostType addLedgerEntries(LedgerEntry ledgerEntry) {
        this.ledgerEntries.add(ledgerEntry);
        ledgerEntry.setCostType(this);
        return this;
    }

    public CostType removeLedgerEntries(LedgerEntry ledgerEntry) {
        this.ledgerEntries.remove(ledgerEntry);
        ledgerEntry.setCostType(null);
        return this;
    }

    public void setLedgerEntries(Set<LedgerEntry> ledgerEntries) {
        if (this.ledgerEntries != null) {
            this.ledgerEntries.forEach(i -> i.setCostType(null));
        }
        if (ledgerEntries != null) {
            ledgerEntries.forEach(i -> i.setCostType(this));
        }
        this.ledgerEntries = ledgerEntries;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CostType)) {
            return false;
        }
        return id != null && id.equals(((CostType) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CostType{" +
            "id=" + getId() +
            ", no='" + getNo() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
}
