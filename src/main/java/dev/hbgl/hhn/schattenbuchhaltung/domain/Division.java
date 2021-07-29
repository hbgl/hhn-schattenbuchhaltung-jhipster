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
 * A Division.
 */
@Entity
@Table(name = "division")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Division implements Serializable {

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

    @OneToMany(mappedBy = "division")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "comments", "tags", "costCenter1", "costCenter2", "costCenter3", "division", "costType" },
        allowSetters = true
    )
    private Set<LedgerEntry> ledgerEntries = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Division id(Long id) {
        this.id = id;
        return this;
    }

    public String getNo() {
        return this.no;
    }

    public Division no(String no) {
        this.no = no;
        return this;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return this.name;
    }

    public Division name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<LedgerEntry> getLedgerEntries() {
        return this.ledgerEntries;
    }

    public Division ledgerEntries(Set<LedgerEntry> ledgerEntries) {
        this.setLedgerEntries(ledgerEntries);
        return this;
    }

    public Division addLedgerEntries(LedgerEntry ledgerEntry) {
        this.ledgerEntries.add(ledgerEntry);
        ledgerEntry.setDivision(this);
        return this;
    }

    public Division removeLedgerEntries(LedgerEntry ledgerEntry) {
        this.ledgerEntries.remove(ledgerEntry);
        ledgerEntry.setDivision(null);
        return this;
    }

    public void setLedgerEntries(Set<LedgerEntry> ledgerEntries) {
        if (this.ledgerEntries != null) {
            this.ledgerEntries.forEach(i -> i.setDivision(null));
        }
        if (ledgerEntries != null) {
            ledgerEntries.forEach(i -> i.setDivision(this));
        }
        this.ledgerEntries = ledgerEntries;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Division)) {
            return false;
        }
        return id != null && id.equals(((Division) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Division{" +
            "id=" + getId() +
            ", no='" + getNo() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
}
