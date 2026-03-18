package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Entity ImportVendorTemTransactions
 */
@Entity
@Table(name = "import_vendor_tem_transactions")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportVendorTemTransactions implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 20)
    @Column(name = "po_number", length = 20)
    private String poNumber;

    @Size(max = 20)
    @Column(name = "vendor_code", length = 20)
    private String vendorCode;

    @Size(max = 255)
    @Column(name = "vendor_name", length = 255)
    private String vendorName;

    @Column(name = "entry_date")
    private LocalDate entryDate;

    @Size(max = 50)
    @Column(name = "storage_unit", length = 50)
    private String storageUnit;

    @Column(name = "tem_identification_scenario_id")
    private Integer temIdentificationScenarioId;

    @Column(name = "mapping_config", columnDefinition = "TEXT")
    private String mappingConfig;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

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

    @Size(max = 20)
    @Column(name = "deleted_by", length = 20)
    private String deletedBy;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "importVendorTemTransactions")
    @JsonIgnoreProperties(
        value = { "vendorTemDetails", "importVendorTemTransactions" },
        allowSetters = true
    )
    private Set<PoDetail> poDetails = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ImportVendorTemTransactions id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPoNumber() {
        return this.poNumber;
    }

    public ImportVendorTemTransactions poNumber(String poNumber) {
        this.setPoNumber(poNumber);
        return this;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getVendorCode() {
        return this.vendorCode;
    }

    public ImportVendorTemTransactions vendorCode(String vendorCode) {
        this.setVendorCode(vendorCode);
        return this;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getVendorName() {
        return this.vendorName;
    }

    public ImportVendorTemTransactions vendorName(String vendorName) {
        this.setVendorName(vendorName);
        return this;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public LocalDate getEntryDate() {
        return this.entryDate;
    }

    public ImportVendorTemTransactions entryDate(LocalDate entryDate) {
        this.setEntryDate(entryDate);
        return this;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public String getStorageUnit() {
        return this.storageUnit;
    }

    public ImportVendorTemTransactions storageUnit(String storageUnit) {
        this.setStorageUnit(storageUnit);
        return this;
    }

    public void setStorageUnit(String storageUnit) {
        this.storageUnit = storageUnit;
    }

    public Integer getTemIdentificationScenarioId() {
        return this.temIdentificationScenarioId;
    }

    public ImportVendorTemTransactions temIdentificationScenarioId(
        Integer temIdentificationScenarioId
    ) {
        this.setTemIdentificationScenarioId(temIdentificationScenarioId);
        return this;
    }

    public void setTemIdentificationScenarioId(
        Integer temIdentificationScenarioId
    ) {
        this.temIdentificationScenarioId = temIdentificationScenarioId;
    }

    public String getMappingConfig() {
        return this.mappingConfig;
    }

    public ImportVendorTemTransactions mappingConfig(String mappingConfig) {
        this.setMappingConfig(mappingConfig);
        return this;
    }

    public void setMappingConfig(String mappingConfig) {
        this.mappingConfig = mappingConfig;
    }

    public String getStatus() {
        return this.status;
    }

    public ImportVendorTemTransactions status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public ImportVendorTemTransactions createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public ImportVendorTemTransactions createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public ImportVendorTemTransactions updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public ImportVendorTemTransactions updatedAt(ZonedDateTime updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedBy() {
        return this.deletedBy;
    }

    public ImportVendorTemTransactions deletedBy(String deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public ZonedDateTime getDeletedAt() {
        return this.deletedAt;
    }

    public ImportVendorTemTransactions deletedAt(ZonedDateTime deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(ZonedDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Set<PoDetail> getPoDetails() {
        return this.poDetails;
    }

    public void setPoDetails(Set<PoDetail> poDetails) {
        if (this.poDetails != null) {
            this.poDetails.forEach(i -> i.setImportVendorTemTransactions(null));
        }
        if (poDetails != null) {
            poDetails.forEach(i -> i.setImportVendorTemTransactions(this));
        }
        this.poDetails = poDetails;
    }

    public ImportVendorTemTransactions poDetails(Set<PoDetail> poDetails) {
        this.setPoDetails(poDetails);
        return this;
    }

    public ImportVendorTemTransactions addPoDetail(PoDetail poDetail) {
        this.poDetails.add(poDetail);
        poDetail.setImportVendorTemTransactions(this);
        return this;
    }

    public ImportVendorTemTransactions removePoDetail(PoDetail poDetail) {
        this.poDetails.remove(poDetail);
        poDetail.setImportVendorTemTransactions(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImportVendorTemTransactions)) {
            return false;
        }
        return (
            getId() != null &&
            getId().equals(((ImportVendorTemTransactions) o).getId())
        );
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportVendorTemTransactions{" +
                "id=" + getId() +
                ", poNumber='" + getPoNumber() + "'" +
                ", vendorCode='" + getVendorCode() + "'" +
                ", vendorName='" + getVendorName() + "'" +
                ", entryDate='" + getEntryDate() + "'" +
                ", storageUnit='" + getStorageUnit() + "'" +
                ", temIdentificationScenarioId='" + getTemIdentificationScenarioId() + "'" +
                ", mappingConfig='" + getMappingConfig() + "'" +
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
