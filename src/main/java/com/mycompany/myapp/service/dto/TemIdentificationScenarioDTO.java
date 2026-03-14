package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TemIdentificationScenario} entity.
 */
@Schema(description = "Entity TemIdentificationScenario")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TemIdentificationScenarioDTO implements Serializable {

    private Long id;

    @Size(max = 50)
    private String vendorCode;

    @Size(max = 255)
    private String vendorName;

    private String mappingConfig;

    @Size(max = 20)
    private String createdBy;

    private ZonedDateTime createdAt;

    @Size(max = 20)
    private String updatedBy;

    private ZonedDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getMappingConfig() {
        return mappingConfig;
    }

    public void setMappingConfig(String mappingConfig) {
        this.mappingConfig = mappingConfig;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TemIdentificationScenarioDTO)) {
            return false;
        }

        TemIdentificationScenarioDTO temIdentificationScenarioDTO =
            (TemIdentificationScenarioDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, temIdentificationScenarioDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TemIdentificationScenarioDTO{" +
            "id=" + getId() +
            ", vendorCode='" + getVendorCode() + "'" +
            ", vendorName='" + getVendorName() + "'" +
            ", mappingConfig='" + getMappingConfig() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
