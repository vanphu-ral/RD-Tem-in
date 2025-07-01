package com.mycompany.wmsral.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "inventory_update_requests_detail")
public class InventoryUpdateRequestsDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "material_id")
    private String materialId;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_time")
    private ZonedDateTime createdTime;

    @Column(name = "updated_time")
    private ZonedDateTime updatedTime;
    @Column(name = "expired_time")
    private ZonedDateTime expiredTime;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "quantity_change")
    private Integer quantityChange;

    @Column(name = "type")
    private String type;

    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "status")
    private String status;

    @Column(name = "request_id")
    private Long requestId;

    // Constructors
    public InventoryUpdateRequestsDetail() {}

    public InventoryUpdateRequestsDetail(Long id, String materialId, String updatedBy, ZonedDateTime createdTime, ZonedDateTime updatedTime, ZonedDateTime expiredTime, String productCode, String productName, Integer quantity, Integer quantityChange, String type, Long locationId, String locationName, String status, Long requestId) {
        this.id = id;
        this.materialId = materialId;
        this.updatedBy = updatedBy;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.expiredTime = expiredTime;
        this.productCode = productCode;
        this.productName = productName;
        this.quantity = quantity;
        this.quantityChange = quantityChange;
        this.type = type;
        this.locationId = locationId;
        this.locationName = locationName;
        this.status = status;
        this.requestId = requestId;
    }

    public ZonedDateTime getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(ZonedDateTime expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }
// Getters and Setters

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(ZonedDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public ZonedDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(ZonedDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Override toString method
    @Override
    public String toString() {
        return "InventoryUpdateRequestsDetail{" +
            "id=" + id +
            ", materialId='" + materialId + '\'' +
            ", updatedBy='" + updatedBy + '\'' +
            ", createdTime=" + createdTime +
            ", updatedTime=" + updatedTime +
            ", productCode='" + productCode + '\'' +
            ", productName='" + productName + '\'' +
            ", quantity=" + quantity +
            ", type='" + type + '\'' +
            ", locationId='" + locationId + '\'' +
            ", locationName='" + locationName + '\'' +
            ", status='" + status + '\'' +
            '}';
    }
}
