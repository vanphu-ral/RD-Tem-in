package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.*;

/**
 * A DTO for creating WarehouseNoteInfoApproval with nested ReelIds.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseNoteInfoApprovalRequestDTO implements Serializable {

    @JsonProperty("warehouse_note_info_id")
    private Long warehouseNoteInfoId;

    @Size(max = 30)
    @JsonProperty("ma_lenh_san_xuat")
    private String maLenhSanXuat;

    @Size(max = 50)
    @JsonProperty("sap_code")
    private String sapCode;

    @Size(max = 150)
    @JsonProperty("sap_name")
    private String sapName;

    @Size(max = 50)
    @JsonProperty("work_order_code")
    private String workOrderCode;

    @Size(max = 50)
    private String version;

    @Size(max = 50)
    @JsonProperty("storage_code")
    private String storageCode;

    @JsonProperty("total_quantity")
    private Integer totalQuantity;

    @Size(max = 50)
    @JsonProperty("create_by")
    private String createBy;

    @Size(max = 50)
    @JsonProperty("trang_thai")
    private String trangThai;

    @Size(max = 150)
    private String comment;

    @Size(max = 20)
    @JsonProperty("group_name")
    private String groupName;

    @Size(max = 255)
    @JsonProperty("comment_2")
    private String comment2;

    @Size(max = 50)
    @JsonProperty("approver_by")
    private String approverBy;

    @Size(max = 50)
    private String branch;

    @Size(max = 50)
    @JsonProperty("product_type")
    private String productType;

    @JsonProperty("destination_warehouse")
    private Long destinationWarehouse;

    @JsonProperty("list_warehouse_note_detail")
    private List<ReelIdRequestDTO> listWarehouseNoteDetail;

    public WarehouseNoteInfoApprovalRequestDTO() {}

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

    public Long getDestinationWarehouse() {
        return this.destinationWarehouse;
    }

    public void setDestinationWarehouse(Long destinationWarehouse) {
        this.destinationWarehouse = destinationWarehouse;
    }

    public List<ReelIdRequestDTO> getListWarehouseNoteDetail() {
        return this.listWarehouseNoteDetail;
    }

    public void setListWarehouseNoteDetail(
        List<ReelIdRequestDTO> listWarehouseNoteDetail
    ) {
        this.listWarehouseNoteDetail = listWarehouseNoteDetail;
    }

    @Override
    public String toString() {
        return (
            "WarehouseNoteInfoApprovalRequestDTO{" +
            "warehouseNoteInfoId=" +
            getWarehouseNoteInfoId() +
            ", maLenhSanXuat='" +
            getMaLenhSanXuat() +
            "'" +
            ", sapCode='" +
            getSapCode() +
            "'" +
            ", sapName='" +
            getSapName() +
            "'" +
            ", workOrderCode='" +
            getWorkOrderCode() +
            "'" +
            ", version='" +
            getVersion() +
            "'" +
            ", storageCode='" +
            getStorageCode() +
            "'" +
            ", totalQuantity=" +
            getTotalQuantity() +
            ", createBy='" +
            getCreateBy() +
            "'" +
            ", trangThai='" +
            getTrangThai() +
            "'" +
            ", comment='" +
            getComment() +
            "'" +
            ", groupName='" +
            getGroupName() +
            "'" +
            ", comment2='" +
            getComment2() +
            "'" +
            ", approverBy='" +
            getApproverBy() +
            "'" +
            ", branch='" +
            getBranch() +
            "'" +
            ", productType='" +
            getProductType() +
            "'" +
            ", destinationWarehouse=" +
            getDestinationWarehouse() +
            ", listWarehouseNoteDetail=" +
            getListWarehouseNoteDetail() +
            "}"
        );
    }
}
