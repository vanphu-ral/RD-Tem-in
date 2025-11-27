package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.WarehouseNoteInfo} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseStampInfoDTO implements Serializable {

    private Long id;

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

    private Integer destinationWarehouse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaLenhSanXuat() {
        return maLenhSanXuat;
    }

    public void setMaLenhSanXuat(String maLenhSanXuat) {
        this.maLenhSanXuat = maLenhSanXuat;
    }

    public String getSapCode() {
        return sapCode;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public String getSapName() {
        return sapName;
    }

    public void setSapName(String sapName) {
        this.sapName = sapName;
    }

    public String getWorkOrderCode() {
        return workOrderCode;
    }

    public void setWorkOrderCode(String workOrderCode) {
        this.workOrderCode = workOrderCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStorageCode() {
        return storageCode;
    }

    public void setStorageCode(String storageCode) {
        this.storageCode = storageCode;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Instant getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(Instant entryTime) {
        this.entryTime = entryTime;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getTimeUpdate() {
        return timeUpdate;
    }

    public void setTimeUpdate(Instant timeUpdate) {
        this.timeUpdate = timeUpdate;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getComment2() {
        return comment2;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    public String getApproverBy() {
        return approverBy;
    }

    public void setApproverBy(String approverBy) {
        this.approverBy = approverBy;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Integer getDestinationWarehouse() {
        return destinationWarehouse;
    }

    public void setDestinationWarehouse(Integer destinationWarehouse) {
        this.destinationWarehouse = destinationWarehouse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WarehouseStampInfoDTO)) {
            return false;
        }

        WarehouseStampInfoDTO warehouseStampInfoDTO = (WarehouseStampInfoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, warehouseStampInfoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WarehouseStampInfoDTO{" +
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
                "}";
    }
}
