package dev.hbgl.hhn.schattenbuchhaltung.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.hbgl.hhn.schattenbuchhaltung.domain.enumeration.HistoryAction;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
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
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "text", nullable = false)
    private String text;

    @NotNull
    @Column(name = "text_normalized", nullable = false, unique = true)
    private String textNormalized;

    @OneToMany(mappedBy = "tag")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "ledgerEntry", "tag" }, allowSetters = true)
    private Set<LedgerEntryTag> ledgerEntryTags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tag id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public Tag text(String text) {
        this.setText(text);
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextNormalized() {
        return this.textNormalized;
    }

    public Tag textNormalized(String textNormalized) {
        this.setTextNormalized(textNormalized);
        return this;
    }

    public void setTextNormalized(String textNormalized) {
        this.textNormalized = textNormalized;
    }

    public Set<LedgerEntryTag> getLedgerEntryTags() {
        return this.ledgerEntryTags;
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

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    public static HistoryEntry historyEntryCreate(Tag tag, User user) {
        var entry = new HistoryEntry();
        entry.setUuid(UUID.randomUUID());
        entry.setAuthor(user);
        entry.setAction(HistoryAction.CREATE);
        entry.setRecId1(tag.getTextNormalized());
        entry.setRecId2("");
        entry.setRecType(HistoryEntryRecType.TAG);
        entry.setInstant(Instant.now());

        var fieldText = new HistoryEntryField();
        fieldText.setName("text");
        fieldText.setEntry(entry);
        fieldText.setOldValue(null);
        fieldText.setNewValue(tag.getText());

        var fields = Set.of(fieldText);
        entry.setFields(fields);

        return entry;
    }

    public static HistoryEntry historyEntryDelete(Tag tag, User user) {
        var entry = new HistoryEntry();
        entry.setUuid(UUID.randomUUID());
        entry.setAuthor(user);
        entry.setAction(HistoryAction.DELETE);
        entry.setRecId1(tag.getTextNormalized());
        entry.setRecId2("");
        entry.setRecType(HistoryEntryRecType.TAG);
        entry.setInstant(Instant.now());
        entry.setFields(Set.of());
        return entry;
    }

    public static HistoryEntry historyEntryModify(Tag oldTag, Tag newTag, User user) {
        var fields = new HashSet<HistoryEntryField>();

        if (!Objects.equals(oldTag.getText(), newTag.getText())) {
            fields.add(new HistoryEntryField().name("text").oldValue(oldTag.getText()).newValue(newTag.getText()));
        }

        if (fields.isEmpty()) {
            return null;
        }

        var entry = new HistoryEntry();
        entry.setUuid(UUID.randomUUID());
        entry.setAuthor(user);
        entry.setAction(HistoryAction.MODIFY);
        entry.setRecId1(oldTag.getTextNormalized());
        entry.setRecId2("");
        entry.setRecType(HistoryEntryRecType.TAG);
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
            ", textNormalized='" + getTextNormalized() + "'" +
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
