package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.RdMaterialAttributes} entity.
 */
@Schema(description = "Entity RdMaterialAttributes")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RdMaterialAttributesDTO implements Serializable {

    private Long id;

    @Size(max = 50)
    private String attributes;

    @Size(max = 255)
    private String description;

    private Integer attributesTypeId;

    @Size(max = 20)
    private String createdBy;

    private ZonedDateTime createdAt;

    @Size(max = 20)
    private String updatedBy;

    private ZonedDateTime updatedAt;

    private AttributesTypeDTO attributesType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAttributesTypeId() {
        return attributesTypeId;
    }

    public void setAttributesTypeId(Integer attributesTypeId) {
        this.attributesTypeId = attributesTypeId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public AttributesTypeDTO getAttributesType() {
        return attributesType;
    }

    public void setAttributesType(AttributesTypeDTO attributesType) {
        this.attributesType = attributesType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RdMaterialAttributesDTO)) {
            return false;
        }

        RdMaterialAttributesDTO rdMaterialAttributesDTO =
            (RdMaterialAttributesDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, rdMaterialAttributesDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RdMaterialAttributesDTO{" +
            "id=" + getId() +
            ", attributes='" + getAttributes() + "'" +
            ", description='" + getDescription() + "'" +
            ", attributesTypeId=" + getAttributesTypeId() +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", attributesType=" + getAttributesType() +
            "}";
    }
}
