package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A WarehouseNoteInfo.
 */
@Entity
@Table(name = "warehouse_note_info")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseNoteInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 30)
    @Column(name = "ma_lenh_san_xuat", length = 30)
    private String maLenhSanXuat;

    @Size(max = 50)
    @Column(name = "sap_code", length = 50)
    private String sapCode;

    @Size(max = 150)
    @Column(name = "sap_name", length = 150)
    private String sapName;

    @Size(max = 50)
    @Column(name = "work_order_code", length = 50)
    private String workOrderCode;

    @Size(max = 50)
    @Column(name = "version", length = 50)
    private String version;

    @Size(max = 50)
    @Column(name = "storage_code", length = 50)
    private String storageCode;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Size(max = 50)
    @Column(name = "create_by", length = 50)
    private String createBy;

    @Column(name = "entry_time")
    private Instant entryTime;

    @Size(max = 50)
    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Size(max = 150)
    @Column(name = "comment", length = 150)
    private String comment;

    @Column(name = "time_update")
    private Instant timeUpdate;

    @Size(max = 20)
    @Column(name = "group_name", length = 20)
    private String groupName;

    @Size(max = 255)
    @Column(name = "comment_2", length = 255)
    private String comment2;

    @Size(max = 50)
    @Column(name = "approver_by", length = 50)
    private String approverBy;

    @Size(max = 50)
    @Column(name = "branch", length = 50)
    private String branch;

    @Size(max = 50)
    @Column(name = "product_type", length = 50)
    private String productType;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Size(max = 50)
    @Column(name = "deleted_by", length = 50)
    private String deletedBy;

    @Column(name = "destination_warehouse")
    private Integer destinationWarehouse;

    @Column(name = "lot_number")
    private String lotNumber;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "maLenhSanXuat")
    @JsonIgnoreProperties(value = { "maLenhSanXuat" }, allowSetters = true)
    private Set<GenTemConfig> genTemConfigs = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "maLenhSanXuat")
    @JsonIgnoreProperties(value = { "maLenhSanXuat" }, allowSetters = true)
    private Set<SerialBoxPalletMapping> serialMappings = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "maLenhSanXuat")
    @JsonIgnoreProperties(value = { "maLenhSanXuat" }, allowSetters = true)
    private Set<WarehouseNoteInfoDetail> details = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WarehouseNoteInfo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaLenhSanXuat() {
        return this.maLenhSanXuat;
    }

    public WarehouseNoteInfo maLenhSanXuat(String maLenhSanXuat) {
        this.setMaLenhSanXuat(maLenhSanXuat);
        return this;
    }

    public void setMaLenhSanXuat(String maLenhSanXuat) {
        this.maLenhSanXuat = maLenhSanXuat;
    }

    public String getSapCode() {
        return this.sapCode;
    }

    public WarehouseNoteInfo sapCode(String sapCode) {
        this.setSapCode(sapCode);
        return this;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public String getSapName() {
        return this.sapName;
    }

    public WarehouseNoteInfo sapName(String sapName) {
        this.setSapName(sapName);
        return this;
    }

    public void setSapName(String sapName) {
        this.sapName = sapName;
    }

    public String getWorkOrderCode() {
        return this.workOrderCode;
    }

    public WarehouseNoteInfo workOrderCode(String workOrderCode) {
        this.setWorkOrderCode(workOrderCode);
        return this;
    }

    public void setWorkOrderCode(String workOrderCode) {
        this.workOrderCode = workOrderCode;
    }

    public String getVersion() {
        return this.version;
    }

    public WarehouseNoteInfo version(String version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStorageCode() {
        return this.storageCode;
    }

    public WarehouseNoteInfo storageCode(String storageCode) {
        this.setStorageCode(storageCode);
        return this;
    }

    public void setStorageCode(String storageCode) {
        this.storageCode = storageCode;
    }

    public Integer getTotalQuantity() {
        return this.totalQuantity;
    }

    public WarehouseNoteInfo totalQuantity(Integer totalQuantity) {
        this.setTotalQuantity(totalQuantity);
        return this;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public WarehouseNoteInfo createBy(String createBy) {
        this.setCreateBy(createBy);
        return this;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Instant getEntryTime() {
        return this.entryTime;
    }

    public WarehouseNoteInfo entryTime(Instant entryTime) {
        this.setEntryTime(entryTime);
        return this;
    }

    public void setEntryTime(Instant entryTime) {
        this.entryTime = entryTime;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public WarehouseNoteInfo trangThai(String trangThai) {
        this.setTrangThai(trangThai);
        return this;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getComment() {
        return this.comment;
    }

    public WarehouseNoteInfo comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getTimeUpdate() {
        return this.timeUpdate;
    }

    public WarehouseNoteInfo timeUpdate(Instant timeUpdate) {
        this.setTimeUpdate(timeUpdate);
        return this;
    }

    public void setTimeUpdate(Instant timeUpdate) {
        this.timeUpdate = timeUpdate;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public WarehouseNoteInfo groupName(String groupName) {
        this.setGroupName(groupName);
        return this;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getComment2() {
        return this.comment2;
    }

    public WarehouseNoteInfo comment2(String comment2) {
        this.setComment2(comment2);
        return this;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    public String getApproverBy() {
        return this.approverBy;
    }

    public WarehouseNoteInfo approverBy(String approverBy) {
        this.setApproverBy(approverBy);
        return this;
    }

    public void setApproverBy(String approverBy) {
        this.approverBy = approverBy;
    }

    public String getBranch() {
        return this.branch;
    }

    public WarehouseNoteInfo branch(String branch) {
        this.setBranch(branch);
        return this;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getProductType() {
        return this.productType;
    }

    public WarehouseNoteInfo productType(String productType) {
        this.setProductType(productType);
        return this;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public WarehouseNoteInfo deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedBy() {
        return this.deletedBy;
    }

    public WarehouseNoteInfo deletedBy(String deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Integer getDestinationWarehouse() {
        return this.destinationWarehouse;
    }

    public WarehouseNoteInfo destinationWarehouse(
        Integer destinationWarehouse
    ) {
        this.setDestinationWarehouse(destinationWarehouse);
        return this;
    }

    public void setDestinationWarehouse(Integer destinationWarehouse) {
        this.destinationWarehouse = destinationWarehouse;
    }

    public String getLotNumber() {
        return this.lotNumber;
    }

    public WarehouseNoteInfo lotNumber(String lotNumber) {
        this.setLotNumber(lotNumber);
        return this;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public Set<GenTemConfig> getGenTemConfigs() {
        return this.genTemConfigs;
    }

    public void setGenTemConfigs(Set<GenTemConfig> genTemConfigs) {
        if (this.genTemConfigs != null) {
            this.genTemConfigs.forEach(i -> i.setMaLenhSanXuat(null));
        }
        if (genTemConfigs != null) {
            genTemConfigs.forEach(i -> i.setMaLenhSanXuat(this));
        }
        this.genTemConfigs = genTemConfigs;
    }

    public WarehouseNoteInfo genTemConfigs(Set<GenTemConfig> genTemConfigs) {
        this.setGenTemConfigs(genTemConfigs);
        return this;
    }

    public WarehouseNoteInfo addGenTemConfigs(GenTemConfig genTemConfig) {
        this.genTemConfigs.add(genTemConfig);
        genTemConfig.setMaLenhSanXuat(this);
        return this;
    }

    public WarehouseNoteInfo removeGenTemConfigs(GenTemConfig genTemConfig) {
        this.genTemConfigs.remove(genTemConfig);
        genTemConfig.setMaLenhSanXuat(null);
        return this;
    }

    public Set<SerialBoxPalletMapping> getSerialMappings() {
        return this.serialMappings;
    }

    public void setSerialMappings(
        Set<SerialBoxPalletMapping> serialBoxPalletMappings
    ) {
        if (this.serialMappings != null) {
            this.serialMappings.forEach(i -> i.setMaLenhSanXuat(null));
        }
        if (serialBoxPalletMappings != null) {
            serialBoxPalletMappings.forEach(i -> i.setMaLenhSanXuat(this));
        }
        this.serialMappings = serialBoxPalletMappings;
    }

    public WarehouseNoteInfo serialMappings(
        Set<SerialBoxPalletMapping> serialBoxPalletMappings
    ) {
        this.setSerialMappings(serialBoxPalletMappings);
        return this;
    }

    public WarehouseNoteInfo addSerialMappings(
        SerialBoxPalletMapping serialBoxPalletMapping
    ) {
        this.serialMappings.add(serialBoxPalletMapping);
        serialBoxPalletMapping.setMaLenhSanXuat(this);
        return this;
    }

    public WarehouseNoteInfo removeSerialMappings(
        SerialBoxPalletMapping serialBoxPalletMapping
    ) {
        this.serialMappings.remove(serialBoxPalletMapping);
        serialBoxPalletMapping.setMaLenhSanXuat(null);
        return this;
    }

    public Set<WarehouseNoteInfoDetail> getDetails() {
        return this.details;
    }

    public void setDetails(
        Set<WarehouseNoteInfoDetail> warehouseStampInfoDetails
    ) {
        if (this.details != null) {
            this.details.forEach(i -> i.setMaLenhSanXuat(null));
        }
        if (warehouseStampInfoDetails != null) {
            warehouseStampInfoDetails.forEach(i -> i.setMaLenhSanXuat(this));
        }
        this.details = warehouseStampInfoDetails;
    }

    public WarehouseNoteInfo details(
        Set<WarehouseNoteInfoDetail> warehouseStampInfoDetails
    ) {
        this.setDetails(warehouseStampInfoDetails);
        return this;
    }

    public WarehouseNoteInfo addDetails(
        WarehouseNoteInfoDetail warehouseStampInfoDetail
    ) {
        this.details.add(warehouseStampInfoDetail);
        warehouseStampInfoDetail.setMaLenhSanXuat(this);
        return this;
    }

    public WarehouseNoteInfo removeDetails(
        WarehouseNoteInfoDetail warehouseStampInfoDetail
    ) {
        this.details.remove(warehouseStampInfoDetail);
        warehouseStampInfoDetail.setMaLenhSanXuat(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WarehouseNoteInfo)) {
            return false;
        }
        return (
            getId() != null && getId().equals(((WarehouseNoteInfo) o).getId())
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
        return "WarehouseNoteInfo{" +
                "id=" + getId() +
                ", maLenhSanXuat='" + getMaLenhSanXuat() + "'" +
                ", sapCode='" + getSapCode() + "'" +
                ", sapName='" + getSapName() + "'" +
                ", workOrderCode='" + getWorkOrderCode() + "'" +
                ", version='" + getVersion() + "'" +
                ", storageCode='" + getStorageCode() + "'" +
                ", totalQuantity=" + getTotalQuantity() +
                ", createBy='" + getCreateBy() + "'" +
                ", entryTime='" + getEntryTime() + "'" +
                ", trangThai='" + getTrangThai() + "'" +
                ", comment='" + getComment() + "'" +
                ", timeUpdate='" + getTimeUpdate() + "'" +
                ", groupName='" + getGroupName() + "'" +
                ", comment2='" + getComment2() + "'" +
                ", approverBy='" + getApproverBy() + "'" +
                ", branch='" + getBranch() + "'" +
                ", productType='" + getProductType() + "'" +
                ", deletedAt='" + getDeletedAt() + "'" +
                ", deletedBy='" + getDeletedBy() + "'" +
                ", destinationWarehouse=" + getDestinationWarehouse() +
                ", lotNumber='" + getLotNumber() + "'" +
                "}";
    }
}
