package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Formula;

/**
 * Entity ImportVendorTemTransactions
 */
@Entity
@Table(name = "po_import_tem")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PoImportTem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "quantity_container")
    private Integer quantityContainer;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "po_comments")
    private String poComments;

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

    // jhipster-needle-entity-add-field - JHipster will add fields here
    @OneToMany(mappedBy = "poImportTem", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    @JsonIgnoreProperties(value = { "poImportTem" }, allowSetters = true)
    private Set<ImportVendorTemTransactions> importVendorTemTransactions =
        new HashSet<>();

    @JsonIgnore
    public Set<ImportVendorTemTransactions> getImportVendorTemTransactions() {
        return importVendorTemTransactions;
    }

    public void setImportVendorTemTransactions(
        Set<ImportVendorTemTransactions> importVendorTemTransactions
    ) {
        if (this.importVendorTemTransactions != null) {
            this.importVendorTemTransactions.forEach(i ->
                i.setPoImportTem(null)
            );
        }
        if (importVendorTemTransactions != null) {
            importVendorTemTransactions.forEach(i -> i.setPoImportTem(this));
        }
        this.importVendorTemTransactions = importVendorTemTransactions;
    }

    public PoImportTem importVendorTemTransactions(
        Set<ImportVendorTemTransactions> importVendorTemTransactions
    ) {
        this.setImportVendorTemTransactions(importVendorTemTransactions);
        return this;
    }

    public PoImportTem addImportVendorTemTransaction(
        ImportVendorTemTransactions importVendorTemTransaction
    ) {
        this.importVendorTemTransactions.add(importVendorTemTransaction);
        importVendorTemTransaction.setPoImportTem(this);
        return this;
    }

    public PoImportTem removeImportVendorTemTransaction(
        ImportVendorTemTransactions importVendorTemTransaction
    ) {
        this.importVendorTemTransactions.remove(importVendorTemTransaction);
        importVendorTemTransaction.setPoImportTem(null);
        return this;
    }

    public Long getId() {
        return this.id;
    }

    public PoImportTem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPoNumber() {
        return this.poNumber;
    }

    public PoImportTem poNumber(String poNumber) {
        this.setPoNumber(poNumber);
        return this;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getVendorCode() {
        return this.vendorCode;
    }

    public PoImportTem vendorCode(String vendorCode) {
        this.setVendorCode(vendorCode);
        return this;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getVendorName() {
        return this.vendorName;
    }

    public PoImportTem vendorName(String vendorName) {
        this.setVendorName(vendorName);
        return this;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public LocalDate getEntryDate() {
        return this.entryDate;
    }

    public PoImportTem entryDate(LocalDate entryDate) {
        this.setEntryDate(entryDate);
        return this;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public Integer getQuantityContainer() {
        return this.quantityContainer;
    }

    public PoImportTem quantityContainer(Integer quantityContainer) {
        this.setQuantityContainer(quantityContainer);
        return this;
    }

    public void setQuantityContainer(Integer quantityContainer) {
        this.quantityContainer = quantityContainer;
    }

    public Integer getTotalQuantity() {
        return this.totalQuantity;
    }

    public PoImportTem totalQuantity(Integer totalQuantity) {
        this.setTotalQuantity(totalQuantity);
        return this;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getStatus() {
        return this.status;
    }

    public PoImportTem status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPoComments() {
        return this.poComments;
    }

    public PoImportTem poComments(String poComments) {
        this.setPoComments(poComments);
        return this;
    }

    public void setPoComments(String poComments) {
        this.poComments = poComments;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public PoImportTem createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public PoImportTem createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public PoImportTem updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public PoImportTem updatedAt(ZonedDateTime updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedBy() {
        return this.deletedBy;
    }

    public PoImportTem deletedBy(String deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public ZonedDateTime getDeletedAt() {
        return this.deletedAt;
    }

    public PoImportTem deletedAt(ZonedDateTime deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(ZonedDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Formula(
        "(SELECT SUM(v.initial_quantity) " +
        "FROM vendor_tem_detail v " +
        "JOIN import_vendor_tem_transactions t ON v.import_vendor_tem_transactions_id = t.id " +
        "WHERE t.po_import_tem_id = id)"
    )
    private Long totalScanQuantity;

    public Long getTotalScanQuantity() {
        return totalScanQuantity;
    }

    public void setTotalScanQuantity(Long totalScanQuantity) {
        this.totalScanQuantity = totalScanQuantity;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PoImportTem)) {
            return false;
        }
        return getId() != null && getId().equals(((PoImportTem) o).getId());
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
        return "PoImportTem{" +
                "id=" + getId() +
                ", poNumber='" + getPoNumber() + "'" +
                ", vendorCode='" + getVendorCode() + "'" +
                ", vendorName='" + getVendorName() + "'" +
                ", entryDate='" + getEntryDate() + "'" +
                ", quantityContainer=" + getQuantityContainer() +
                ", totalQuantity=" + getTotalQuantity() +
                ", status='" + getStatus() + "'" +
                ", poComments='" + getPoComments() + "'" +
                ", createdBy='" + getCreatedBy() + "'" +
                ", createdAt='" + getCreatedAt() + "'" +
                ", updatedBy='" + getUpdatedBy() + "'" +
                ", updatedAt='" + getUpdatedAt() + "'" +
                ", deletedBy='" + getDeletedBy() + "'" +
                ", deletedAt='" + getDeletedAt() + "'" +
                "}";
    }
}
