package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.WarehouseTransferSession} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseTransferSessionDTO implements Serializable {

    private Long id;

    @Size(max = 50)
    private String status;

    @Size(max = 255)
    private String note;

    private Instant createdAt;

    @Size(max = 50)
    private String createdBy;

    private Instant updatedAt;

    @Size(max = 50)
    private String updatedBy;

    private Instant deletedAt;

    @Size(max = 50)
    private String deletedBy;

    private java.util.List<Long> owtqList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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

    public java.util.List<Long> getOwtqList() {
        return owtqList;
    }

    public void setOwtqList(java.util.List<Long> owtqList) {
        this.owtqList = owtqList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WarehouseTransferSessionDTO)) {
            return false;
        }

        WarehouseTransferSessionDTO warehouseTransferSessionDTO =
            (WarehouseTransferSessionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, warehouseTransferSessionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WarehouseTransferSessionDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", note='" + getNote() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            "}";
    }
}
