package dev.hbgl.hhn.schattenbuchhaltung.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LedgerEntry.
 */
@Entity
@Table(name = "ledger_entry")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LedgerEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "no", nullable = false, unique = true)
    private String no;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "a_no")
    private String aNo;

    @NotNull
    @Column(name = "booking_date", nullable = false)
    private Instant bookingDate;

    @NotNull
    @Column(name = "income", precision = 21, scale = 2, nullable = false)
    private BigDecimal income;

    @NotNull
    @Column(name = "expenditure", precision = 21, scale = 2, nullable = false)
    private BigDecimal expenditure;

    @NotNull
    @Column(name = "liability", precision = 21, scale = 2, nullable = false)
    private BigDecimal liability;

    @OneToMany(mappedBy = "ledgerEntry")
    @OrderBy("id DESC")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "author", "ledgerEntry", "children", "parent" }, allowSetters = true)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "ledgerEntry")
    @OrderBy("priority, id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "ledgerEntry", "tag" }, allowSetters = true)
    private Set<LedgerEntryTag> ledgerEntryTags = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "children", "ledgerEntries1s", "ledgerEntries2s", "ledgerEntries3s", "parent" }, allowSetters = true)
    private CostCenter costCenter1;

    @ManyToOne
    @JsonIgnoreProperties(value = { "children", "ledgerEntries1s", "ledgerEntries2s", "ledgerEntries3s", "parent" }, allowSetters = true)
    private CostCenter costCenter2;

    @ManyToOne
    @JsonIgnoreProperties(value = { "children", "ledgerEntries1s", "ledgerEntries2s", "ledgerEntries3s", "parent" }, allowSetters = true)
    private CostCenter costCenter3;

    @ManyToOne
    @JsonIgnoreProperties(value = { "ledgerEntries" }, allowSetters = true)
    private Division division;

    @ManyToOne
    @JsonIgnoreProperties(value = { "ledgerEntries" }, allowSetters = true)
    private CostType costType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LedgerEntry id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNo() {
        return this.no;
    }

    public LedgerEntry no(String no) {
        this.setNo(no);
        return this;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getDescription() {
        return this.description;
    }

    public LedgerEntry description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getaNo() {
        return this.aNo;
    }

    public LedgerEntry aNo(String aNo) {
        this.setaNo(aNo);
        return this;
    }

    public void setaNo(String aNo) {
        this.aNo = aNo;
    }

    public Instant getBookingDate() {
        return this.bookingDate;
    }

    public LedgerEntry bookingDate(Instant bookingDate) {
        this.setBookingDate(bookingDate);
        return this;
    }

    public void setBookingDate(Instant bookingDate) {
        this.bookingDate = bookingDate;
    }

    public BigDecimal getIncome() {
        return this.income;
    }

    public LedgerEntry income(BigDecimal income) {
        this.setIncome(income);
        return this;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpenditure() {
        return this.expenditure;
    }

    public LedgerEntry expenditure(BigDecimal expenditure) {
        this.setExpenditure(expenditure);
        return this;
    }

    public void setExpenditure(BigDecimal expenditure) {
        this.expenditure = expenditure;
    }

    public BigDecimal getLiability() {
        return this.liability;
    }

    public LedgerEntry liability(BigDecimal liability) {
        this.setLiability(liability);
        return this;
    }

    public void setLiability(BigDecimal liability) {
        this.liability = liability;
    }

    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setLedgerEntry(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setLedgerEntry(this));
        }
        this.comments = comments;
    }

    public LedgerEntry comments(Set<Comment> comments) {
        this.setComments(comments);
        return this;
    }

    public LedgerEntry addComments(Comment comment) {
        this.comments.add(comment);
        comment.setLedgerEntry(this);
        return this;
    }

    public LedgerEntry removeComments(Comment comment) {
        this.comments.remove(comment);
        comment.setLedgerEntry(null);
        return this;
    }

    public Set<LedgerEntryTag> getLedgerEntryTags() {
        return this.ledgerEntryTags;
    }

    public void setLedgerEntryTags(Set<LedgerEntryTag> ledgerEntryTags) {
        if (this.ledgerEntryTags != null) {
            this.ledgerEntryTags.forEach(i -> i.setLedgerEntry(null));
        }
        if (ledgerEntryTags != null) {
            ledgerEntryTags.forEach(i -> i.setLedgerEntry(this));
        }
        this.ledgerEntryTags = ledgerEntryTags;
    }

    public LedgerEntry ledgerEntryTags(Set<LedgerEntryTag> ledgerEntryTags) {
        this.setLedgerEntryTags(ledgerEntryTags);
        return this;
    }

    public LedgerEntry addLedgerEntryTags(LedgerEntryTag ledgerEntryTag) {
        this.ledgerEntryTags.add(ledgerEntryTag);
        ledgerEntryTag.setLedgerEntry(this);
        return this;
    }

    public LedgerEntry removeLedgerEntryTags(LedgerEntryTag ledgerEntryTag) {
        this.ledgerEntryTags.remove(ledgerEntryTag);
        ledgerEntryTag.setLedgerEntry(null);
        return this;
    }

    public CostCenter getCostCenter1() {
        return this.costCenter1;
    }

    public void setCostCenter1(CostCenter costCenter) {
        this.costCenter1 = costCenter;
    }

    public LedgerEntry costCenter1(CostCenter costCenter) {
        this.setCostCenter1(costCenter);
        return this;
    }

    public CostCenter getCostCenter2() {
        return this.costCenter2;
    }

    public void setCostCenter2(CostCenter costCenter) {
        this.costCenter2 = costCenter;
    }

    public LedgerEntry costCenter2(CostCenter costCenter) {
        this.setCostCenter2(costCenter);
        return this;
    }

    public CostCenter getCostCenter3() {
        return this.costCenter3;
    }

    public void setCostCenter3(CostCenter costCenter) {
        this.costCenter3 = costCenter;
    }

    public LedgerEntry costCenter3(CostCenter costCenter) {
        this.setCostCenter3(costCenter);
        return this;
    }

    public Division getDivision() {
        return this.division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public LedgerEntry division(Division division) {
        this.setDivision(division);
        return this;
    }

    public CostType getCostType() {
        return this.costType;
    }

    public void setCostType(CostType costType) {
        this.costType = costType;
    }

    public LedgerEntry costType(CostType costType) {
        this.setCostType(costType);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LedgerEntry)) {
            return false;
        }
        return id != null && id.equals(((LedgerEntry) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LedgerEntry{" +
            "id=" + getId() +
            ", no='" + getNo() + "'" +
            ", description='" + getDescription() + "'" +
            ", aNo='" + getaNo() + "'" +
            ", bookingDate='" + getBookingDate() + "'" +
            ", income=" + getIncome() +
            ", expenditure=" + getExpenditure() +
            ", liability=" + getLiability() +
            "}";
    }
}
