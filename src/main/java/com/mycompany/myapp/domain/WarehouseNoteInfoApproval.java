package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A WarehouseNoteInfoApproval.
 */
@Entity
@Table(name = "warehouse_note_info_approval")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseNoteInfoApproval implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "warehouse_note_info_id")
    private Long warehouseNoteInfoId;

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
    private Long destinationWarehouse;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "warehouseNoteInfoApproval",
        cascade = CascadeType.ALL
    )
    @JsonIgnoreProperties(
        value = { "warehouseNoteInfoApproval" },
        allowSetters = true
    )
    private Set<ReelIdInWarehouseNoteInfoApproval> reelIds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WarehouseNoteInfoApproval id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWarehouseNoteInfoId() {
        return this.warehouseNoteInfoId;
    }

    public WarehouseNoteInfoApproval warehouseNoteInfoId(
        Long warehouseNoteInfoId
    ) {
        this.setWarehouseNoteInfoId(warehouseNoteInfoId);
        return this;
    }

    public void setWarehouseNoteInfoId(Long warehouseNoteInfoId) {
        this.warehouseNoteInfoId = warehouseNoteInfoId;
    }

    public String getMaLenhSanXuat() {
        return this.maLenhSanXuat;
    }

    public WarehouseNoteInfoApproval maLenhSanXuat(String maLenhSanXuat) {
        this.setMaLenhSanXuat(maLenhSanXuat);
        return this;
    }

    public void setMaLenhSanXuat(String maLenhSanXuat) {
        this.maLenhSanXuat = maLenhSanXuat;
    }

    public String getSapCode() {
        return this.sapCode;
    }

    public WarehouseNoteInfoApproval sapCode(String sapCode) {
        this.setSapCode(sapCode);
        return this;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public String getSapName() {
        return this.sapName;
    }

    public WarehouseNoteInfoApproval sapName(String sapName) {
        this.setSapName(sapName);
        return this;
    }

    public void setSapName(String sapName) {
        this.sapName = sapName;
    }

    public String getWorkOrderCode() {
        return this.workOrderCode;
    }

    public WarehouseNoteInfoApproval workOrderCode(String workOrderCode) {
        this.setWorkOrderCode(workOrderCode);
        return this;
    }

    public void setWorkOrderCode(String workOrderCode) {
        this.workOrderCode = workOrderCode;
    }

    public String getVersion() {
        return this.version;
    }

    public WarehouseNoteInfoApproval version(String version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStorageCode() {
        return this.storageCode;
    }

    public WarehouseNoteInfoApproval storageCode(String storageCode) {
        this.setStorageCode(storageCode);
        return this;
    }

    public void setStorageCode(String storageCode) {
        this.storageCode = storageCode;
    }

    public Integer getTotalQuantity() {
        return this.totalQuantity;
    }

    public WarehouseNoteInfoApproval totalQuantity(Integer totalQuantity) {
        this.setTotalQuantity(totalQuantity);
        return this;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public WarehouseNoteInfoApproval createBy(String createBy) {
        this.setCreateBy(createBy);
        return this;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Instant getEntryTime() {
        return this.entryTime;
    }

    public WarehouseNoteInfoApproval entryTime(Instant entryTime) {
        this.setEntryTime(entryTime);
        return this;
    }

    public void setEntryTime(Instant entryTime) {
        this.entryTime = entryTime;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public WarehouseNoteInfoApproval trangThai(String trangThai) {
        this.setTrangThai(trangThai);
        return this;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getComment() {
        return this.comment;
    }

    public WarehouseNoteInfoApproval comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getTimeUpdate() {
        return this.timeUpdate;
    }

    public WarehouseNoteInfoApproval timeUpdate(Instant timeUpdate) {
        this.setTimeUpdate(timeUpdate);
        return this;
    }

    public void setTimeUpdate(Instant timeUpdate) {
        this.timeUpdate = timeUpdate;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public WarehouseNoteInfoApproval groupName(String groupName) {
        this.setGroupName(groupName);
        return this;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getComment2() {
        return this.comment2;
    }

    public WarehouseNoteInfoApproval comment2(String comment2) {
        this.setComment2(comment2);
        return this;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    public String getApproverBy() {
        return this.approverBy;
    }

    public WarehouseNoteInfoApproval approverBy(String approverBy) {
        this.setApproverBy(approverBy);
        return this;
    }

    public void setApproverBy(String approverBy) {
        this.approverBy = approverBy;
    }

    public String getBranch() {
        return this.branch;
    }

    public WarehouseNoteInfoApproval branch(String branch) {
        this.setBranch(branch);
        return this;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getProductType() {
        return this.productType;
    }

    public WarehouseNoteInfoApproval productType(String productType) {
        this.setProductType(productType);
        return this;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public WarehouseNoteInfoApproval deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedBy() {
        return this.deletedBy;
    }

    public WarehouseNoteInfoApproval deletedBy(String deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Long getDestinationWarehouse() {
        return this.destinationWarehouse;
    }

    public WarehouseNoteInfoApproval destinationWarehouse(
        Long destinationWarehouse
    ) {
        this.setDestinationWarehouse(destinationWarehouse);
        return this;
    }

    public void setDestinationWarehouse(Long destinationWarehouse) {
        this.destinationWarehouse = destinationWarehouse;
    }

    public Set<ReelIdInWarehouseNoteInfoApproval> getReelIds() {
        return this.reelIds;
    }

    public void setReelIds(
        Set<
            ReelIdInWarehouseNoteInfoApproval
        > reelIdInWarehouseNoteInfoApprovals
    ) {
        if (this.reelIds != null) {
            this.reelIds.forEach(i -> i.setWarehouseNoteInfoApproval(null));
        }
        if (reelIdInWarehouseNoteInfoApprovals != null) {
            reelIdInWarehouseNoteInfoApprovals.forEach(i ->
                i.setWarehouseNoteInfoApproval(this)
            );
        }
        this.reelIds = reelIdInWarehouseNoteInfoApprovals;
    }

    public WarehouseNoteInfoApproval reelIds(
        Set<
            ReelIdInWarehouseNoteInfoApproval
        > reelIdInWarehouseNoteInfoApprovals
    ) {
        this.setReelIds(reelIdInWarehouseNoteInfoApprovals);
        return this;
    }

    public WarehouseNoteInfoApproval addReelIds(
        ReelIdInWarehouseNoteInfoApproval reelIdInWarehouseNoteInfoApproval
    ) {
        this.reelIds.add(reelIdInWarehouseNoteInfoApproval);
        reelIdInWarehouseNoteInfoApproval.setWarehouseNoteInfoApproval(this);
        return this;
    }

    public WarehouseNoteInfoApproval removeReelIds(
        ReelIdInWarehouseNoteInfoApproval reelIdInWarehouseNoteInfoApproval
    ) {
        this.reelIds.remove(reelIdInWarehouseNoteInfoApproval);
        reelIdInWarehouseNoteInfoApproval.setWarehouseNoteInfoApproval(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WarehouseNoteInfoApproval)) {
            return false;
        }
        return (
            getId() != null &&
            getId().equals(((WarehouseNoteInfoApproval) o).getId())
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
        return "WarehouseNoteInfoApproval{" +
                "id=" + getId() +
                ", warehouseNoteInfoId=" + getWarehouseNoteInfoId() +
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
                "}";
    }
}
