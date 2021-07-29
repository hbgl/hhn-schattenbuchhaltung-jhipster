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
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A LedgerEntry.
 */
@Entity
@Table(name = "ledger_entry")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "ledgerentry")
public class LedgerEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
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
    @OrderBy("id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "author", "ledgerEntry", "children", "parent" }, allowSetters = true)
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany
    @OrderBy("id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(
        name = "rel_ledger_entry__tags",
        joinColumns = @JoinColumn(name = "ledger_entry_id"),
        inverseJoinColumns = @JoinColumn(name = "tags_id")
    )
    @JsonIgnoreProperties(value = { "person", "customType", "customValue", "ledgerEntries" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

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
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LedgerEntry id(Long id) {
        this.id = id;
        return this;
    }

    public String getNo() {
        return this.no;
    }

    public LedgerEntry no(String no) {
        this.no = no;
        return this;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getDescription() {
        return this.description;
    }

    public LedgerEntry description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getaNo() {
        return this.aNo;
    }

    public LedgerEntry aNo(String aNo) {
        this.aNo = aNo;
        return this;
    }

    public void setaNo(String aNo) {
        this.aNo = aNo;
    }

    public Instant getBookingDate() {
        return this.bookingDate;
    }

    public LedgerEntry bookingDate(Instant bookingDate) {
        this.bookingDate = bookingDate;
        return this;
    }

    public void setBookingDate(Instant bookingDate) {
        this.bookingDate = bookingDate;
    }

    public BigDecimal getIncome() {
        return this.income;
    }

    public LedgerEntry income(BigDecimal income) {
        this.income = income;
        return this;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpenditure() {
        return this.expenditure;
    }

    public LedgerEntry expenditure(BigDecimal expenditure) {
        this.expenditure = expenditure;
        return this;
    }

    public void setExpenditure(BigDecimal expenditure) {
        this.expenditure = expenditure;
    }

    public BigDecimal getLiability() {
        return this.liability;
    }

    public LedgerEntry liability(BigDecimal liability) {
        this.liability = liability;
        return this;
    }

    public void setLiability(BigDecimal liability) {
        this.liability = liability;
    }

    public Set<Comment> getComments() {
        return this.comments;
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

    public void setComments(Set<Comment> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setLedgerEntry(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setLedgerEntry(this));
        }
        this.comments = comments;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public LedgerEntry tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public LedgerEntry addTags(Tag tag) {
        this.tags.add(tag);
        tag.getLedgerEntries().add(this);
        return this;
    }

    public LedgerEntry removeTags(Tag tag) {
        this.tags.remove(tag);
        tag.getLedgerEntries().remove(this);
        return this;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public CostCenter getCostCenter1() {
        return this.costCenter1;
    }

    public LedgerEntry costCenter1(CostCenter costCenter) {
        this.setCostCenter1(costCenter);
        return this;
    }

    public void setCostCenter1(CostCenter costCenter) {
        this.costCenter1 = costCenter;
    }

    public CostCenter getCostCenter2() {
        return this.costCenter2;
    }

    public LedgerEntry costCenter2(CostCenter costCenter) {
        this.setCostCenter2(costCenter);
        return this;
    }

    public void setCostCenter2(CostCenter costCenter) {
        this.costCenter2 = costCenter;
    }

    public CostCenter getCostCenter3() {
        return this.costCenter3;
    }

    public LedgerEntry costCenter3(CostCenter costCenter) {
        this.setCostCenter3(costCenter);
        return this;
    }

    public void setCostCenter3(CostCenter costCenter) {
        this.costCenter3 = costCenter;
    }

    public Division getDivision() {
        return this.division;
    }

    public LedgerEntry division(Division division) {
        this.setDivision(division);
        return this;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public CostType getCostType() {
        return this.costType;
    }

    public LedgerEntry costType(CostType costType) {
        this.setCostType(costType);
        return this;
    }

    public void setCostType(CostType costType) {
        this.costType = costType;
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
