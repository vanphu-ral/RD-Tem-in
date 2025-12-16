package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.WarehouseNoteInfoApproval}
 * entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseNoteInfoApprovalDTO implements Serializable {

    private Long id;

    private Long warehouseNoteInfoId;

    @Size(max = 30)
    private String maLenhSanXuat;

    @Size(max = 50)
    private String sapCode;

    @Size(max = 150)
    private String sapName;

    @Size(max = 50)
    private String workOrderCode;

    @Size(max = 50)
    private String version;

    @Size(max = 50)
    private String storageCode;

    private Integer totalQuantity;

    @Size(max = 50)
    private String createBy;

    private Instant entryTime;

    @Size(max = 50)
    private String trangThai;

    @Size(max = 150)
    private String comment;

    private Instant timeUpdate;

    @Size(max = 20)
    private String groupName;

    @Size(max = 255)
    private String comment2;

    @Size(max = 50)
    private String approverBy;

    @Size(max = 50)
    private String branch;

    @Size(max = 50)
    private String productType;

    private Instant deletedAt;

    @Size(max = 50)
    private String deletedBy;

    private Long destinationWarehouse;

    private Set<ReelIdInWarehouseNoteInfoApprovalDTO> reelIds = new HashSet<>();

    public WarehouseNoteInfoApprovalDTO() {}

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWarehouseNoteInfoId() {
        return this.warehouseNoteInfoId;
    }

    public void setWarehouseNoteInfoId(Long warehouseNoteInfoId) {
        this.warehouseNoteInfoId = warehouseNoteInfoId;
    }

    public String getMaLenhSanXuat() {
        return this.maLenhSanXuat;
    }

    public void setMaLenhSanXuat(String maLenhSanXuat) {
        this.maLenhSanXuat = maLenhSanXuat;
    }

    public String getSapCode() {
        return this.sapCode;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public String getSapName() {
        return this.sapName;
    }

    public void setSapName(String sapName) {
        this.sapName = sapName;
    }

    public String getWorkOrderCode() {
        return this.workOrderCode;
    }

    public void setWorkOrderCode(String workOrderCode) {
        this.workOrderCode = workOrderCode;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStorageCode() {
        return this.storageCode;
    }

    public void setStorageCode(String storageCode) {
        this.storageCode = storageCode;
    }

    public Integer getTotalQuantity() {
        return this.totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Instant getEntryTime() {
        return this.entryTime;
    }

    public void setEntryTime(Instant entryTime) {
        this.entryTime = entryTime;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getTimeUpdate() {
        return this.timeUpdate;
    }

    public void setTimeUpdate(Instant timeUpdate) {
        this.timeUpdate = timeUpdate;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getComment2() {
        return this.comment2;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    public String getApproverBy() {
        return this.approverBy;
    }

    public void setApproverBy(String approverBy) {
        this.approverBy = approverBy;
    }

    public String getBranch() {
        return this.branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getProductType() {
        return this.productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedBy() {
        return this.deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Long getDestinationWarehouse() {
        return this.destinationWarehouse;
    }

    public void setDestinationWarehouse(Long destinationWarehouse) {
        this.destinationWarehouse = destinationWarehouse;
    }

    public Set<ReelIdInWarehouseNoteInfoApprovalDTO> getReelIds() {
        return this.reelIds;
    }

    public void setReelIds(Set<ReelIdInWarehouseNoteInfoApprovalDTO> reelIds) {
        this.reelIds = reelIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WarehouseNoteInfoApprovalDTO)) {
            return false;
        }

        WarehouseNoteInfoApprovalDTO warehouseNoteInfoApprovalDTO =
            (WarehouseNoteInfoApprovalDTO) o;
        if (this.id == null) {
            return false;
        }
        return id.equals(warehouseNoteInfoApprovalDTO.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WarehouseNoteInfoApprovalDTO{" +
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
