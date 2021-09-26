package dev.hbgl.hhn.schattenbuchhaltung.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CostCenter.
 */
@Entity
@Table(name = "cost_center")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CostCenter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "no", nullable = false, unique = true)
    private String no;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "rank", nullable = false)
    private Integer rank;

    @OneToMany(mappedBy = "parent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "children", "ledgerEntries1s", "ledgerEntries2s", "ledgerEntries3s", "parent" }, allowSetters = true)
    private Set<CostCenter> children = new HashSet<>();

    @OneToMany(mappedBy = "costCenter1")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "comments", "ledgerEntryTags", "costCenter1", "costCenter2", "costCenter3", "division", "costType" },
        allowSetters = true
    )
    private Set<LedgerEntry> ledgerEntries1s = new HashSet<>();

    @OneToMany(mappedBy = "costCenter2")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "comments", "ledgerEntryTags", "costCenter1", "costCenter2", "costCenter3", "division", "costType" },
        allowSetters = true
    )
    private Set<LedgerEntry> ledgerEntries2s = new HashSet<>();

    @OneToMany(mappedBy = "costCenter3")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "comments", "ledgerEntryTags", "costCenter1", "costCenter2", "costCenter3", "division", "costType" },
        allowSetters = true
    )
    private Set<LedgerEntry> ledgerEntries3s = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "children", "ledgerEntries1s", "ledgerEntries2s", "ledgerEntries3s", "parent" }, allowSetters = true)
    private CostCenter parent;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CostCenter id(Long id) {
        this.id = id;
        return this;
    }

    public String getNo() {
        return this.no;
    }

    public CostCenter no(String no) {
        this.no = no;
        return this;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return this.name;
    }

    public CostCenter name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRank() {
        return this.rank;
    }

    public CostCenter rank(Integer rank) {
        this.rank = rank;
        return this;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Set<CostCenter> getChildren() {
        return this.children;
    }

    public CostCenter children(Set<CostCenter> costCenters) {
        this.setChildren(costCenters);
        return this;
    }

    public CostCenter addChildren(CostCenter costCenter) {
        this.children.add(costCenter);
        costCenter.setParent(this);
        return this;
    }

    public CostCenter removeChildren(CostCenter costCenter) {
        this.children.remove(costCenter);
        costCenter.setParent(null);
        return this;
    }

    public void setChildren(Set<CostCenter> costCenters) {
        if (this.children != null) {
            this.children.forEach(i -> i.setParent(null));
        }
        if (costCenters != null) {
            costCenters.forEach(i -> i.setParent(this));
        }
        this.children = costCenters;
    }

    public Set<LedgerEntry> getLedgerEntries1s() {
        return this.ledgerEntries1s;
    }

    public CostCenter ledgerEntries1s(Set<LedgerEntry> ledgerEntries) {
        this.setLedgerEntries1s(ledgerEntries);
        return this;
    }

    public CostCenter addLedgerEntries1(LedgerEntry ledgerEntry) {
        this.ledgerEntries1s.add(ledgerEntry);
        ledgerEntry.setCostCenter1(this);
        return this;
    }

    public CostCenter removeLedgerEntries1(LedgerEntry ledgerEntry) {
        this.ledgerEntries1s.remove(ledgerEntry);
        ledgerEntry.setCostCenter1(null);
        return this;
    }

    public void setLedgerEntries1s(Set<LedgerEntry> ledgerEntries) {
        if (this.ledgerEntries1s != null) {
            this.ledgerEntries1s.forEach(i -> i.setCostCenter1(null));
        }
        if (ledgerEntries != null) {
            ledgerEntries.forEach(i -> i.setCostCenter1(this));
        }
        this.ledgerEntries1s = ledgerEntries;
    }

    public Set<LedgerEntry> getLedgerEntries2s() {
        return this.ledgerEntries2s;
    }

    public CostCenter ledgerEntries2s(Set<LedgerEntry> ledgerEntries) {
        this.setLedgerEntries2s(ledgerEntries);
        return this;
    }

    public CostCenter addLedgerEntries2(LedgerEntry ledgerEntry) {
        this.ledgerEntries2s.add(ledgerEntry);
        ledgerEntry.setCostCenter2(this);
        return this;
    }

    public CostCenter removeLedgerEntries2(LedgerEntry ledgerEntry) {
        this.ledgerEntries2s.remove(ledgerEntry);
        ledgerEntry.setCostCenter2(null);
        return this;
    }

    public void setLedgerEntries2s(Set<LedgerEntry> ledgerEntries) {
        if (this.ledgerEntries2s != null) {
            this.ledgerEntries2s.forEach(i -> i.setCostCenter2(null));
        }
        if (ledgerEntries != null) {
            ledgerEntries.forEach(i -> i.setCostCenter2(this));
        }
        this.ledgerEntries2s = ledgerEntries;
    }

    public Set<LedgerEntry> getLedgerEntries3s() {
        return this.ledgerEntries3s;
    }

    public CostCenter ledgerEntries3s(Set<LedgerEntry> ledgerEntries) {
        this.setLedgerEntries3s(ledgerEntries);
        return this;
    }

    public CostCenter addLedgerEntries3(LedgerEntry ledgerEntry) {
        this.ledgerEntries3s.add(ledgerEntry);
        ledgerEntry.setCostCenter3(this);
        return this;
    }

    public CostCenter removeLedgerEntries3(LedgerEntry ledgerEntry) {
        this.ledgerEntries3s.remove(ledgerEntry);
        ledgerEntry.setCostCenter3(null);
        return this;
    }

    public void setLedgerEntries3s(Set<LedgerEntry> ledgerEntries) {
        if (this.ledgerEntries3s != null) {
            this.ledgerEntries3s.forEach(i -> i.setCostCenter3(null));
        }
        if (ledgerEntries != null) {
            ledgerEntries.forEach(i -> i.setCostCenter3(this));
        }
        this.ledgerEntries3s = ledgerEntries;
    }

    public CostCenter getParent() {
        return this.parent;
    }

    public CostCenter parent(CostCenter costCenter) {
        this.setParent(costCenter);
        return this;
    }

    public void setParent(CostCenter costCenter) {
        this.parent = costCenter;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CostCenter)) {
            return false;
        }
        return id != null && id.equals(((CostCenter) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CostCenter{" +
            "id=" + getId() +
            ", no='" + getNo() + "'" +
            ", name='" + getName() + "'" +
            ", rank=" + getRank() +
            "}";
    }
}
