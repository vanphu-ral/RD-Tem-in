package com.mycompany.wmsral.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;
@Entity
@Table(name = "inventory_update_requests")
public class InventoryUpdateRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_code")
    private String requestCode;

    @Column(name = "created_time")
    private ZonedDateTime createdTime;

    @Column(name = "updated_time")
    private ZonedDateTime updatedTime;

    @Column(name = "requested_by")
    private String requestedBy;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "status")
    private String status;

    // Constructors
    public InventoryUpdateRequests() {}

    public InventoryUpdateRequests(Long id, String requestCode, ZonedDateTime createdTime, ZonedDateTime updatedTime, String requestedBy, String approvedBy, String status) {
        this.id = id;
        this.requestCode = requestCode;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.requestedBy = requestedBy;
        this.approvedBy = approvedBy;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Override toString method
    @Override
    public String toString() {
        return "InventoryUpdateRequests{" +
            "id=" + id +
            ", requestCode='" + requestCode + '\'' +
            ", createdTime=" + createdTime +
            ", updatedTime=" + updatedTime +
            ", updatedBy='" + requestedBy + '\'' +
            ", approvedBy='" + approvedBy + '\'' +
            ", status='" + status + '\'' +
            '}';
    }
}
