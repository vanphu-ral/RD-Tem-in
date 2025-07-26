package com.mycompany.wmsral.domain;

import java.time.ZonedDateTime;
import javax.persistence.*;

@Entity
@Table(name = "inventory_update_requests_history")
public class InventoryUpdateRequestsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "material_id", nullable = false, unique = true)
    private String materialId;

    @Column(name = "partnumber", length = 100)
    private String partnumber;

    @Column(name = "requested_time", nullable = false)
    private ZonedDateTime requestedTime;

    @Column(name = "approved_time")
    private ZonedDateTime approvedTime;

    @Column(name = "requested_by", nullable = false, length = 50)
    private String requestedBy;

    @Column(name = "approved_by", length = 50)
    private String approvedBy;

    @Column(name = "req_approver", length = 50)
    private String reqApprover;

    @Column(
        name = "request_code",
        nullable = false,
        unique = true,
        length = 100
    )
    private String requestCode;

    @Column(name = "old_location")
    private String oldLocation;

    @Column(name = "new_location")
    private String newLocation;

    @Column(name = "expired_time")
    private Long expiredTime;

    @Column(name = "request_type", nullable = false)
    private String requestType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "quantity_change")
    private Integer quantityChange;

    @Column(name = "status", nullable = false)
    private String status;

    public InventoryUpdateRequestsHistory(
        Long id,
        String materialId,
        String partnumber,
        ZonedDateTime requestedTime,
        ZonedDateTime approvedTime,
        String requestedBy,
        String approvedBy,
        String reqApprover,
        String requestCode,
        String oldLocation,
        String newLocation,
        Long expiredTime,
        String requestType,
        Integer quantity,
        Integer quantityChange,
        String status
    ) {
        this.id = id;
        this.materialId = materialId;
        this.partnumber = partnumber;
        this.requestedTime = requestedTime;
        this.approvedTime = approvedTime;
        this.requestedBy = requestedBy;
        this.approvedBy = approvedBy;
        this.reqApprover = reqApprover;
        this.requestCode = requestCode;
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
        this.expiredTime = expiredTime;
        this.requestType = requestType;
        this.quantity = quantity;
        this.quantityChange = quantityChange;
        this.status = status;
    }

    public InventoryUpdateRequestsHistory() {}

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

    public String getPartnumber() {
        return partnumber;
    }

    public void setPartnumber(String partnumber) {
        this.partnumber = partnumber;
    }

    public ZonedDateTime getApprovedTime() {
        return approvedTime;
    }

    public void setApprovedTime(ZonedDateTime approvedTime) {
        this.approvedTime = approvedTime;
    }

    public ZonedDateTime getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(ZonedDateTime requestedTime) {
        this.requestedTime = requestedTime;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getReqApprover() {
        return reqApprover;
    }

    public void setReqApprover(String reqApprover) {
        this.reqApprover = reqApprover;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public String getOldLocation() {
        return oldLocation;
    }

    public void setOldLocation(String oldLocation) {
        this.oldLocation = oldLocation;
    }

    public String getNewLocation() {
        return newLocation;
    }

    public void setNewLocation(String newLocation) {
        this.newLocation = newLocation;
    }

    public Long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
