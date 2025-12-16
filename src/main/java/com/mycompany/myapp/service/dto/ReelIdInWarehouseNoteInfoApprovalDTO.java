package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;

/**
 * A DTO for the
 * {@link com.mycompany.myapp.domain.ReelIdInWarehouseNoteInfoApproval} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReelIdInWarehouseNoteInfoApprovalDTO implements Serializable {

    private Long id;

    private Long warehouseNoteInfoApprovalId;

    private Instant createAt;

    @Size(max = 50)
    private String createBy;

    @Size(max = 50)
    private String status;

    public ReelIdInWarehouseNoteInfoApprovalDTO() {}

    public ReelIdInWarehouseNoteInfoApprovalDTO(
        Long id,
        Instant createAt,
        String createBy
    ) {
        this.id = id;
        this.createAt = createAt;
        this.createBy = createBy;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWarehouseNoteInfoApprovalId() {
        return this.warehouseNoteInfoApprovalId;
    }

    public void setWarehouseNoteInfoApprovalId(
        Long warehouseNoteInfoApprovalId
    ) {
        this.warehouseNoteInfoApprovalId = warehouseNoteInfoApprovalId;
    }

    public Instant getCreateAt() {
        return this.createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReelIdInWarehouseNoteInfoApprovalDTO)) {
            return false;
        }

        ReelIdInWarehouseNoteInfoApprovalDTO reelIdInWarehouseNoteInfoApprovalDTO =
            (ReelIdInWarehouseNoteInfoApprovalDTO) o;
        if (this.id == null) {
            return false;
        }
        return id.equals(reelIdInWarehouseNoteInfoApprovalDTO.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReelIdInWarehouseNoteInfoApprovalDTO{" +
                "id='" + getId() + "'" +
                ", warehouseNoteInfoApprovalId=" + getWarehouseNoteInfoApprovalId() +
                ", createAt='" + getCreateAt() + "'" +
                ", createBy='" + getCreateBy() + "'" +
                ", status='" + getStatus() + "'" +
                "}";
    }
}
