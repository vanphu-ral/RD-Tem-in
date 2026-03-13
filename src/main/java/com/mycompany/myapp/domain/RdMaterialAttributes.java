package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Entity RdMaterialAttributes
 */
@Entity
@Table(name = "rd_material_attributes")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RdMaterialAttributes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequenceGenerator"
    )
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 50)
    @Column(name = "attributes", length = 50)
    private String attributes;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "attributes_type_id")
    private Integer attributesTypeId;

    @Size(max = 20)
    @Column(name = "created_by", length = 20)
    private String createdBy;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Size(max = 20)
    @Column(name = "updated_by", length = 20)
    private String updatedBy;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "attributes_type_id",
        insertable = false,
        updatable = false
    )
    @JsonIgnoreProperties(
        value = { "rdMaterialAttributes" },
        allowSetters = true
    )
    private AttributesType attributesType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RdMaterialAttributes id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttributes() {
        return this.attributes;
    }

    public RdMaterialAttributes attributes(String attributes) {
        this.setAttributes(attributes);
        return this;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getDescription() {
        return this.description;
    }

    public RdMaterialAttributes description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAttributesTypeId() {
        return this.attributesTypeId;
    }

    public RdMaterialAttributes attributesTypeId(Integer attributesTypeId) {
        this.setAttributesTypeId(attributesTypeId);
        return this;
    }

    public void setAttributesTypeId(Integer attributesTypeId) {
        this.attributesTypeId = attributesTypeId;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public RdMaterialAttributes createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public RdMaterialAttributes createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public RdMaterialAttributes updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public RdMaterialAttributes updatedAt(ZonedDateTime updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public AttributesType getAttributesType() {
        return this.attributesType;
    }

    public void setAttributesType(AttributesType attributesType) {
        this.attributesType = attributesType;
    }

    public RdMaterialAttributes attributesType(AttributesType attributesType) {
        this.setAttributesType(attributesType);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RdMaterialAttributes)) {
            return false;
        }
        return (
            getId() != null &&
            getId().equals(((RdMaterialAttributes) o).getId())
        );
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RdMaterialAttributes{" +
            "id=" + getId() +
            ", attributes='" + getAttributes() + "'" +
            ", description='" + getDescription() + "'" +
            ", attributesTypeId=" + getAttributesTypeId() +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
