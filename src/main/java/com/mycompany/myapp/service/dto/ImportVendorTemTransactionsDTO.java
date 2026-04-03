package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.ImportVendorTemTransactions}
 * entity.
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

    private Integer temIdentificationScenarioId;

    private String mappingConfig;

    @Size(max = 50)
    private String status;

    @Size(max = 510)
    private String note;

    @Size(max = 50)
    private String approver;

    @Size(max = 20)
    private String createdBy;

    private ZonedDateTime createdAt;

    @Size(max = 20)
    private String updatedBy;

    private ZonedDateTime updatedAt;

    @Size(max = 20)
    private String deletedBy;

    private ZonedDateTime deletedAt;

    private Boolean panaSendStatus;

    private Long poImportTemId;

    private Integer totalQuantityCalculated;

    private Integer totalScanQuantity;

    private Set<PoDetailDTO> poDetails;

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

    public Integer getTemIdentificationScenarioId() {
        return temIdentificationScenarioId;
    }

    public void setTemIdentificationScenarioId(
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
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

    public void setPanaSendStatus(Boolean panaSendStatus) {
        this.panaSendStatus = panaSendStatus;
    }

    public Boolean getPanaSendStatus() {
        return panaSendStatus;
    }

    public Long getPoImportTemId() {
        return poImportTemId;
    }

    public void setPoImportTemId(Long poImportTemId) {
        this.poImportTemId = poImportTemId;
    }

    public Integer getTotalQuantityCalculated() {
        return totalQuantityCalculated;
    }

    public void setTotalQuantityCalculated(Integer totalQuantityCalculated) {
        this.totalQuantityCalculated = totalQuantityCalculated;
    }

    public Integer getTotalScanQuantity() {
        return totalScanQuantity;
    }

    public void setTotalScanQuantity(Integer totalScanQuantity) {
        this.totalScanQuantity = totalScanQuantity;
    }

    public Set<PoDetailDTO> getPoDetails() {
        return poDetails;
    }

    public void setPoDetails(Set<PoDetailDTO> poDetails) {
        this.poDetails = poDetails;
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
                ", temIdentificationScenarioId=" + getTemIdentificationScenarioId() +
                ", mappingConfig='" + getMappingConfig() + "'" +
                ", status='" + getStatus() + "'" +
                ", createdBy='" + getCreatedBy() + "'" +
                ", createdAt='" + getCreatedAt() + "'" +
                ", updatedBy='" + getUpdatedBy() + "'" +
                ", updatedAt='" + getUpdatedAt() + "'" +
                ", deletedBy='" + getDeletedBy() + "'" +
                ", deletedAt='" + getDeletedAt() + "'" +
                ", panaSendStatus='" + getPanaSendStatus() + "'" +
                ", note='" + getNote() + "'" +
                ", approver='" + getApprover() + "'" +
                "}";
    }
}
