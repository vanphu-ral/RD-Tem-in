package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * Entity AttributesType
 */
@Entity
@Table(name = "attributes_type")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AttributesType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequenceGenerator"
    )
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "attributesType")
    @JsonIgnoreProperties(value = { "attributesType" }, allowSetters = true)
    private Set<RdMaterialAttributes> rdMaterialAttributes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AttributesType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public AttributesType description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<RdMaterialAttributes> getRdMaterialAttributes() {
        return this.rdMaterialAttributes;
    }

    public void setRdMaterialAttributes(
        Set<RdMaterialAttributes> rdMaterialAttributes
    ) {
        if (this.rdMaterialAttributes != null) {
            this.rdMaterialAttributes.forEach(i -> i.setAttributesType(null));
        }
        if (rdMaterialAttributes != null) {
            rdMaterialAttributes.forEach(i -> i.setAttributesType(this));
        }
        this.rdMaterialAttributes = rdMaterialAttributes;
    }

    public AttributesType rdMaterialAttributes(
        Set<RdMaterialAttributes> rdMaterialAttributes
    ) {
        this.setRdMaterialAttributes(rdMaterialAttributes);
        return this;
    }

    public AttributesType addRdMaterialAttribute(
        RdMaterialAttributes rdMaterialAttributes
    ) {
        this.rdMaterialAttributes.add(rdMaterialAttributes);
        rdMaterialAttributes.setAttributesType(this);
        return this;
    }

    public AttributesType removeRdMaterialAttribute(
        RdMaterialAttributes rdMaterialAttributes
    ) {
        this.rdMaterialAttributes.remove(rdMaterialAttributes);
        rdMaterialAttributes.setAttributesType(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttributesType)) {
            return false;
        }
        return getId() != null && getId().equals(((AttributesType) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AttributesType{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
