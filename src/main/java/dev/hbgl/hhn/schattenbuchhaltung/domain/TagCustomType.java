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
 * A TagCustomType.
 */
@Entity
@Table(name = "tag_custom_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "tagcustomtype")
public class TagCustomType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @OneToMany(mappedBy = "type")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tags", "type" }, allowSetters = true)
    private Set<TagCustomValue> values = new HashSet<>();

    @OneToMany(mappedBy = "customType")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "author", "person", "customType", "customValue" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TagCustomType id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public TagCustomType name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public TagCustomType enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Set<TagCustomValue> getValues() {
        return this.values;
    }

    public TagCustomType values(Set<TagCustomValue> tagCustomValues) {
        this.setValues(tagCustomValues);
        return this;
    }

    public TagCustomType addValues(TagCustomValue tagCustomValue) {
        this.values.add(tagCustomValue);
        tagCustomValue.setType(this);
        return this;
    }

    public TagCustomType removeValues(TagCustomValue tagCustomValue) {
        this.values.remove(tagCustomValue);
        tagCustomValue.setType(null);
        return this;
    }

    public void setValues(Set<TagCustomValue> tagCustomValues) {
        if (this.values != null) {
            this.values.forEach(i -> i.setType(null));
        }
        if (tagCustomValues != null) {
            tagCustomValues.forEach(i -> i.setType(this));
        }
        this.values = tagCustomValues;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public TagCustomType tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public TagCustomType addTag(Tag tag) {
        this.tags.add(tag);
        tag.setCustomType(this);
        return this;
    }

    public TagCustomType removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.setCustomType(null);
        return this;
    }

    public void setTags(Set<Tag> tags) {
        if (this.tags != null) {
            this.tags.forEach(i -> i.setCustomType(null));
        }
        if (tags != null) {
            tags.forEach(i -> i.setCustomType(this));
        }
        this.tags = tags;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TagCustomType)) {
            return false;
        }
        return id != null && id.equals(((TagCustomType) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TagCustomType{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", enabled='" + getEnabled() + "'" +
            "}";
    }
}
