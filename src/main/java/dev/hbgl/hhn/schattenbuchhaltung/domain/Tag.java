package dev.hbgl.hhn.schattenbuchhaltung.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.hbgl.hhn.schattenbuchhaltung.domain.enumeration.TagKind;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Tag.
 */
@Entity
@Table(name = "tag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "tag")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TagKind type;

    @Column(name = "text")
    private String text;

    @ManyToOne(optional = false)
    @NotNull
    private User author;

    @ManyToOne
    private User person;

    @ManyToOne
    @JsonIgnoreProperties(value = { "values", "tags" }, allowSetters = true)
    private TagCustomType customType;

    @ManyToOne
    @JsonIgnoreProperties(value = { "tags", "type" }, allowSetters = true)
    private TagCustomValue customValue;

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

    public TagKind getType() {
        return this.type;
    }

    public Tag type(TagKind type) {
        this.type = type;
        return this;
    }

    public void setType(TagKind type) {
        this.type = type;
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

    public User getAuthor() {
        return this.author;
    }

    public Tag author(User user) {
        this.setAuthor(user);
        return this;
    }

    public void setAuthor(User user) {
        this.author = user;
    }

    public User getPerson() {
        return this.person;
    }

    public Tag person(User user) {
        this.setPerson(user);
        return this;
    }

    public void setPerson(User user) {
        this.person = user;
    }

    public TagCustomType getCustomType() {
        return this.customType;
    }

    public Tag customType(TagCustomType tagCustomType) {
        this.setCustomType(tagCustomType);
        return this;
    }

    public void setCustomType(TagCustomType tagCustomType) {
        this.customType = tagCustomType;
    }

    public TagCustomValue getCustomValue() {
        return this.customValue;
    }

    public Tag customValue(TagCustomValue tagCustomValue) {
        this.setCustomValue(tagCustomValue);
        return this;
    }

    public void setCustomValue(TagCustomValue tagCustomValue) {
        this.customValue = tagCustomValue;
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
            ", type='" + getType() + "'" +
            ", text='" + getText() + "'" +
            "}";
    }
}
