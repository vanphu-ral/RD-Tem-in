package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import javax.validation.constraints.Size;

/**
 * DTO for receiving PO Import request payload.
 * Handles both Case 1 (poNumber is null) and Case 2 (poNumber exists).
 */
@Schema(description = "PO Import Request DTO")
public class PoImportRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 20)
    private String poNumber;

    @Size(max = 20)
    private String vendorCode;

    @Size(max = 255)
    private String vendorName;

    private LocalDate entryDate;

    @Size(max = 510)
    private String note;

    @Size(max = 50)
    private String storageUnit;

    private Integer temIdentificationScenarioId;

    private String mappingConfig;

    @Size(max = 50)
    private String status;

    @Size(max = 20)
    private String createdBy;

    private ZonedDateTime createdAt;

    @Size(max = 20)
    private String updatedBy;

    private ZonedDateTime updatedAt;

    @Size(max = 20)
    private String deletedBy;

    private ZonedDateTime deletedAt;

    public PoImportRequestDTO() {}

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
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

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public String getStorageUnit() {
        return storageUnit;
    }

    public void setStorageUnit(String storageUnit) {
        this.storageUnit = storageUnit;
    }

    public Integer getTemIdentificationScenarioId() {
        return temIdentificationScenarioId;
    }

    public void settemIdentificationScenarioId(
        Integer temIdentificationScenarioId
    ) {
        this.temIdentificationScenarioId = temIdentificationScenarioId;
    }

    public String getMappingConfig() {
        return mappingConfig;
    }

    public void setMappingConfig(String mappingConfig) {
        this.mappingConfig = mappingConfig;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public ZonedDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(ZonedDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return (
            "PoImportRequestDTO{" +
            "poNumber='" +
            poNumber +
            '\'' +
            ", vendorCode='" +
            vendorCode +
            '\'' +
            ", vendorName='" +
            vendorName +
            '\'' +
            ", entryDate=" +
            entryDate +
            ", storageUnit='" +
            storageUnit +
            '\'' +
            ", temIdentificationScenarioId=" +
            temIdentificationScenarioId +
            ", mappingConfig='" +
            mappingConfig +
            '\'' +
            ", status='" +
            status +
            '\'' +
            ", note='" +
            note +
            '\'' +
            ", createdBy='" +
            createdBy +
            '\'' +
            ", createdAt=" +
            createdAt +
            ", updatedBy='" +
            updatedBy +
            '\'' +
            ", updatedAt=" +
            updatedAt +
            ", deletedBy='" +
            deletedBy +
            '\'' +
            ", deletedAt=" +
            deletedAt +
            '}'
        );
    }
}
