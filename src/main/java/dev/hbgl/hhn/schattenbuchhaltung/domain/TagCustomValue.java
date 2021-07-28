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
 * A TagCustomValue.
 */
@Entity
@Table(name = "tag_custom_value")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TagCustomValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "value", nullable = false)
    private String value;

    @OneToMany(mappedBy = "customValue")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "person", "customType", "customValue", "ledgerEntries" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "values", "tags" }, allowSetters = true)
    private TagCustomType type;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TagCustomValue id(Long id) {
        this.id = id;
        return this;
    }

    public String getValue() {
        return this.value;
    }

    public TagCustomValue value(String value) {
        this.value = value;
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public TagCustomValue tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public TagCustomValue addTag(Tag tag) {
        this.tags.add(tag);
        tag.setCustomValue(this);
        return this;
    }

    public TagCustomValue removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.setCustomValue(null);
        return this;
    }

    public void setTags(Set<Tag> tags) {
        if (this.tags != null) {
            this.tags.forEach(i -> i.setCustomValue(null));
        }
        if (tags != null) {
            tags.forEach(i -> i.setCustomValue(this));
        }
        this.tags = tags;
    }

    public TagCustomType getType() {
        return this.type;
    }

    public TagCustomValue type(TagCustomType tagCustomType) {
        this.setType(tagCustomType);
        return this;
    }

    public void setType(TagCustomType tagCustomType) {
        this.type = tagCustomType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TagCustomValue)) {
            return false;
        }
        return id != null && id.equals(((TagCustomValue) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TagCustomValue{" +
            "id=" + getId() +
            ", value='" + getValue() + "'" +
            "}";
    }
}
