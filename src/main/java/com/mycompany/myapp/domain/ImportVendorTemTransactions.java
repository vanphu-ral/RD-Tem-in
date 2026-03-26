package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Formula;

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

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "approver", columnDefinition = "TEXT")
    private String approver;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "pana_send_status")
    private Boolean panaSendStatus;

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

    @Column(name = "po_import_tem_id")
    private Long poImportTemId;

    // Tính tổng số lượng từ các PoDetail thuộc Transaction này
    @Formula(
        "(SELECT SUM(pd.total_quantity) FROM po_detail pd WHERE pd.import_vendor_tem_transactions_id = id)"
    )
    @Column(
        name = "total_quantity_calculated",
        insertable = false,
        updatable = false
    )
    private Integer totalQuantityCalculated;

    // Tính tổng số lượng thực tế đã quét từ VendorTemDetail
    @Formula(
        "(SELECT SUM(v.initial_quantity) FROM vendor_tem_detail v WHERE v.import_vendor_tem_transactions_id = id)"
    )
    @Column(name = "total_scan_quantity", insertable = false, updatable = false)
    private Integer totalScanQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "importVendorTemTransactions" },
        allowSetters = true
    )
    @JoinColumn(
        name = "po_import_tem_id",
        insertable = false,
        updatable = false
    )
    private PoImportTem poImportTem;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "importVendorTemTransactions")
    @BatchSize(size = 20)
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

    // note

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ImportVendorTemTransactions note(String note) {
        this.setNote(note);
        return this;
    }

    // approver
    public String getApprover() {
        return this.approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public ImportVendorTemTransactions approver(String approver) {
        this.setApprover(approver);
        return this;
    }

    // pana_send_status
    public Boolean getPanaSendStatus() {
        return this.panaSendStatus;
    }

    public void setPanaSendStatus(Boolean panaSendStatus) {
        this.panaSendStatus = panaSendStatus;
    }

    public ImportVendorTemTransactions panaSendStatus(Boolean panaSendStatus) {
        this.setPanaSendStatus(panaSendStatus);
        return this;
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

    public Long getPoImportTemId() {
        return this.poImportTemId;
    }

    public ImportVendorTemTransactions poImportTemId(Long poImportTemId) {
        this.setPoImportTemId(poImportTemId);
        return this;
    }

    public void setPoImportTemId(Long poImportTemId) {
        this.poImportTemId = poImportTemId;
    }

    public PoImportTem getPoImportTem() {
        return this.poImportTem;
    }

    public void setPoImportTem(PoImportTem poImportTem) {
        this.poImportTem = poImportTem;
    }

    public ImportVendorTemTransactions poImportTem(PoImportTem poImportTem) {
        this.setPoImportTem(poImportTem);
        return this;
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

    // jhipster-needle-entity-add-getters-setters - JHipster will add fields here

    public Integer getTotalQuantityCalculated() {
        return totalQuantityCalculated;
    }

    public Integer getTotalScanQuantity() {
        return totalScanQuantity;
    }

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
                ", panaSendStatus='" + getPanaSendStatus() + "'" +
                ", poImportTemId='" + getPoImportTemId() + "'" +
                "}";
    }
}
