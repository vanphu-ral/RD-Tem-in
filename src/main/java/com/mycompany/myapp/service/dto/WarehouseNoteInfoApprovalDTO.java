package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("warehouse_note_info_id")
    private Long warehouseNoteInfoId;

    @JsonProperty("ma_lenh_san_xuat")
    @Size(max = 30)
    private String maLenhSanXuat;

    @JsonProperty("sap_code")
    @Size(max = 50)
    private String sapCode;

    @JsonProperty("sap_name")
    @Size(max = 150)
    private String sapName;

    @JsonProperty("work_order_code")
    @Size(max = 50)
    private String workOrderCode;

    @JsonProperty("version")
    @Size(max = 50)
    private String version;

    @JsonProperty("storage_code")
    @Size(max = 50)
    private String storageCode;

    @JsonProperty("total_quantity")
    private Integer totalQuantity;

    @JsonProperty("create_by")
    @Size(max = 50)
    private String createBy;

    @JsonProperty("entry_time")
    private Instant entryTime;

    @JsonProperty("trang_thai")
    @Size(max = 50)
    private String trangThai;

    @JsonProperty("comment")
    @Size(max = 150)
    private String comment;

    @JsonProperty("time_update")
    private Instant timeUpdate;

    @JsonProperty("group_name")
    @Size(max = 20)
    private String groupName;

    @JsonProperty("comment_2")
    @Size(max = 255)
    private String comment2;

    @JsonProperty("approver_by")
    @Size(max = 50)
    private String approverBy;

    @JsonProperty("branch")
    @Size(max = 50)
    private String branch;

    @JsonProperty("product_type")
    @Size(max = 50)
    private String productType;

    @JsonProperty("deleted_at")
    private Instant deletedAt;

    @JsonProperty("deleted_by")
    @Size(max = 50)
    private String deletedBy;

    @JsonProperty("destination_warehouse")
    private Long destinationWarehouse;

    @JsonProperty("reel_ids")
    private Set<ReelIdInWarehouseNoteInfoApprovalDTO> reelIds = new HashSet<>();

    public WarehouseNoteInfoApprovalDTO() {}

    @JsonProperty("id")
    public Long getId() {
        return this.id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("warehouse_note_info_id")
    public Long getWarehouseNoteInfoId() {
        return this.warehouseNoteInfoId;
    }

    @JsonProperty("warehouse_note_info_id")
    public void setWarehouseNoteInfoId(Long warehouseNoteInfoId) {
        this.warehouseNoteInfoId = warehouseNoteInfoId;
    }

    @JsonProperty("ma_lenh_san_xuat")
    public String getMaLenhSanXuat() {
        return this.maLenhSanXuat;
    }

    @JsonProperty("ma_lenh_san_xuat")
    public void setMaLenhSanXuat(String maLenhSanXuat) {
        this.maLenhSanXuat = maLenhSanXuat;
    }

    @JsonProperty("sap_code")
    public String getSapCode() {
        return this.sapCode;
    }

    @JsonProperty("sap_code")
    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    @JsonProperty("sap_name")
    public String getSapName() {
        return this.sapName;
    }

    @JsonProperty("sap_name")
    public void setSapName(String sapName) {
        this.sapName = sapName;
    }

    @JsonProperty("work_order_code")
    public String getWorkOrderCode() {
        return this.workOrderCode;
    }

    @JsonProperty("work_order_code")
    public void setWorkOrderCode(String workOrderCode) {
        this.workOrderCode = workOrderCode;
    }

    @JsonProperty("version")
    public String getVersion() {
        return this.version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("storage_code")
    public String getStorageCode() {
        return this.storageCode;
    }

    @JsonProperty("storage_code")
    public void setStorageCode(String storageCode) {
        this.storageCode = storageCode;
    }

    @JsonProperty("total_quantity")
    public Integer getTotalQuantity() {
        return this.totalQuantity;
    }

    @JsonProperty("total_quantity")
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    @JsonProperty("create_by")
    public String getCreateBy() {
        return this.createBy;
    }

    @JsonProperty("create_by")
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @JsonProperty("entry_time")
    public Instant getEntryTime() {
        return this.entryTime;
    }

    @JsonProperty("entry_time")
    public void setEntryTime(Instant entryTime) {
        this.entryTime = entryTime;
    }

    @JsonProperty("trang_thai")
    public String getTrangThai() {
        return this.trangThai;
    }

    @JsonProperty("trang_thai")
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @JsonProperty("comment")
    public String getComment() {
        return this.comment;
    }

    @JsonProperty("comment")
    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty("time_update")
    public Instant getTimeUpdate() {
        return this.timeUpdate;
    }

    @JsonProperty("time_update")
    public void setTimeUpdate(Instant timeUpdate) {
        this.timeUpdate = timeUpdate;
    }

    @JsonProperty("group_name")
    public String getGroupName() {
        return this.groupName;
    }

    @JsonProperty("group_name")
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @JsonProperty("comment_2")
    public String getComment2() {
        return this.comment2;
    }

    @JsonProperty("comment_2")
    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    @JsonProperty("approver_by")
    public String getApproverBy() {
        return this.approverBy;
    }

    @JsonProperty("approver_by")
    public void setApproverBy(String approverBy) {
        this.approverBy = approverBy;
    }

    @JsonProperty("branch")
    public String getBranch() {
        return this.branch;
    }

    @JsonProperty("branch")
    public void setBranch(String branch) {
        this.branch = branch;
    }

    @JsonProperty("product_type")
    public String getProductType() {
        return this.productType;
    }

    @JsonProperty("product_type")
    public void setProductType(String productType) {
        this.productType = productType;
    }

    @JsonProperty("deleted_at")
    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    @JsonProperty("deleted_at")
    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @JsonProperty("deleted_by")
    public String getDeletedBy() {
        return this.deletedBy;
    }

    @JsonProperty("deleted_by")
    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    @JsonProperty("destination_warehouse")
    public Long getDestinationWarehouse() {
        return this.destinationWarehouse;
    }

    @JsonProperty("destination_warehouse")
    public void setDestinationWarehouse(Long destinationWarehouse) {
        this.destinationWarehouse = destinationWarehouse;
    }

    @JsonProperty("reel_ids")
    public Set<ReelIdInWarehouseNoteInfoApprovalDTO> getReelIds() {
        return this.reelIds;
    }

    @JsonProperty("reel_ids")
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
                ", warehouse_note_info_id=" + getWarehouseNoteInfoId() +
                ", ma_lenh_san_xuat='" + getMaLenhSanXuat() + "'" +
                ", sap_code='" + getSapCode() + "'" +
                ", sap_name='" + getSapName() + "'" +
                ", work_order_code='" + getWorkOrderCode() + "'" +
                ", version='" + getVersion() + "'" +
                ", storage_code='" + getStorageCode() + "'" +
                ", total_quantity=" + getTotalQuantity() +
                ", create_by='" + getCreateBy() + "'" +
                ", entry_time='" + getEntryTime() + "'" +
                ", trang_thai='" + getTrangThai() + "'" +
                ", comment='" + getComment() + "'" +
                ", time_update='" + getTimeUpdate() + "'" +
                ", group_name='" + getGroupName() + "'" +
                ", comment_2='" + getComment2() + "'" +
                ", approver_by='" + getApproverBy() + "'" +
                ", branch='" + getBranch() + "'" +
                ", product_type='" + getProductType() + "'" +
                ", deleted_at='" + getDeletedAt() + "'" +
                ", deleted_by='" + getDeletedBy() + "'" +
                ", destination_warehouse=" + getDestinationWarehouse() +
                "}";
    }
}
