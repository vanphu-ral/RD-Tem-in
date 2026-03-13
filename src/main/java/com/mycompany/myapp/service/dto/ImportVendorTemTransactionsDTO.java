package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.ImportVendorTemTransactions} entity.
 */
@Schema(description = "Entity ImportVendorTemTransactions")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportVendorTemTransactionsDTO implements Serializable {

    private Long id;

    @Size(max = 20)
    private String poNumber;

    @Size(max = 20)
    private String vendorCode;

    @Size(max = 255)
    private String vendorName;

    private LocalDate entryDate;

    @Size(max = 50)
    private String storageUnit;

    private String importTemProfile;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getImportTemProfile() {
        return importTemProfile;
    }

    public void setImportTemProfile(String importTemProfile) {
        this.importTemProfile = importTemProfile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImportVendorTemTransactionsDTO)) {
            return false;
        }

        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO =
            (ImportVendorTemTransactionsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, importVendorTemTransactionsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportVendorTemTransactionsDTO{" +
            "id=" + getId() +
            ", poNumber='" + getPoNumber() + "'" +
            ", vendorCode='" + getVendorCode() + "'" +
            ", vendorName='" + getVendorName() + "'" +
            ", entryDate='" + getEntryDate() + "'" +
            ", storageUnit='" + getStorageUnit() + "'" +
            ", importTemProfile='" + getImportTemProfile() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            "}";
    }
}
