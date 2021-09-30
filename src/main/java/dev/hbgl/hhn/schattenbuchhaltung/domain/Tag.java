package dev.hbgl.hhn.schattenbuchhaltung.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Tag.
 */
@Entity
@Table(name = "tag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "text")
    private String text;

    @NotNull
    @Column(name = "textNormalized")
    private String textNormalized;

    @OneToMany(mappedBy = "tag")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "ledgerEntry", "tag" }, allowSetters = true)
    private Set<LedgerEntryTag> ledgerEntryTags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tag id(Long id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return this.text;
    }

    public Tag text(String text) {
        this.text = text;
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextNormalized() {
        return this.textNormalized;
    }

    public Tag textNormalized(String textNormalized) {
        this.textNormalized = textNormalized;
        return this;
    }

    public void setTextNormalized(String textNormalized) {
        this.textNormalized = textNormalized;
    }

    public Set<LedgerEntryTag> getLedgerEntryTags() {
        return this.ledgerEntryTags;
    }

    public Tag ledgerEntryTags(Set<LedgerEntryTag> ledgerEntryTags) {
        this.setLedgerEntryTags(ledgerEntryTags);
        return this;
    }

    public Tag addLedgerEntryTags(LedgerEntryTag ledgerEntryTag) {
        this.ledgerEntryTags.add(ledgerEntryTag);
        ledgerEntryTag.setTag(this);
        return this;
    }

    public Tag removeLedgerEntryTags(LedgerEntryTag ledgerEntryTag) {
        this.ledgerEntryTags.remove(ledgerEntryTag);
        ledgerEntryTag.setTag(null);
        return this;
    }

    public void setLedgerEntryTags(Set<LedgerEntryTag> ledgerEntryTags) {
        if (this.ledgerEntryTags != null) {
            this.ledgerEntryTags.forEach(i -> i.setTag(null));
        }
        if (ledgerEntryTags != null) {
            ledgerEntryTags.forEach(i -> i.setTag(this));
        }
        this.ledgerEntryTags = ledgerEntryTags;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        return id != null && id.equals(((Tag) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tag{" +
            "id=" + getId() +
            ", text='" + getText() + "'" +
            ", text='" + getTextNormalized() + "'" +
            "}";
    }

    public Tag normalized() {
        this.setTextNormalized(Tag.normalizeText(this.text));
        return this;
    }

    public static String normalizeText(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().toLowerCase(Locale.ROOT).replace(",", "");
    }
}
