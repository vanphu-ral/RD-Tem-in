package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A ReelIdInWarehouseNoteInfoApproval.
 */
@Entity
@Table(name = "reelid_in_warehouse_note_info_approval")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReelIdInWarehouseNoteInfoApproval implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(
        name = "warehouse_note_info_approval_id",
        insertable = false,
        updatable = false
    )
    private Long warehouseNoteInfoApprovalId;

    @Column(name = "create_at")
    private Instant createAt;

    @Size(max = 50)
    @Column(name = "create_by", length = 50)
    private String createBy;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_note_info_approval_id")
    @JsonIgnoreProperties(value = { "reelIds" }, allowSetters = true)
    private WarehouseNoteInfoApproval warehouseNoteInfoApproval;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public ReelIdInWarehouseNoteInfoApproval id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getWarehouseNoteInfoApprovalId() {
        return this.warehouseNoteInfoApprovalId;
    }

    public ReelIdInWarehouseNoteInfoApproval warehouseNoteInfoApprovalId(
        Long warehouseNoteInfoApprovalId
    ) {
        this.setWarehouseNoteInfoApprovalId(warehouseNoteInfoApprovalId);
        return this;
    }

    public void setWarehouseNoteInfoApprovalId(
        Long warehouseNoteInfoApprovalId
    ) {
        this.warehouseNoteInfoApprovalId = warehouseNoteInfoApprovalId;
    }

    public Instant getCreateAt() {
        return this.createAt;
    }

    public ReelIdInWarehouseNoteInfoApproval createAt(Instant createAt) {
        this.setCreateAt(createAt);
        return this;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public ReelIdInWarehouseNoteInfoApproval createBy(String createBy) {
        this.setCreateBy(createBy);
        return this;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getStatus() {
        return this.status;
    }

    public ReelIdInWarehouseNoteInfoApproval status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public WarehouseNoteInfoApproval getWarehouseNoteInfoApproval() {
        return this.warehouseNoteInfoApproval;
    }

    public void setWarehouseNoteInfoApproval(
        WarehouseNoteInfoApproval warehouseNoteInfoApproval
    ) {
        this.warehouseNoteInfoApproval = warehouseNoteInfoApproval;
    }

    public ReelIdInWarehouseNoteInfoApproval warehouseNoteInfoApproval(
        WarehouseNoteInfoApproval warehouseNoteInfoApproval
    ) {
        this.setWarehouseNoteInfoApproval(warehouseNoteInfoApproval);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReelIdInWarehouseNoteInfoApproval)) {
            return false;
        }
        return (
            getId() != null &&
            getId().equals(((ReelIdInWarehouseNoteInfoApproval) o).getId())
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
        return "ReelIdInWarehouseNoteInfoApproval{" +
                "id='" + getId() + "'" +
                ", warehouseNoteInfoApprovalId=" + getWarehouseNoteInfoApprovalId() +
                ", createAt='" + getCreateAt() + "'" +
                ", createBy='" + getCreateBy() + "'" +
                ", status='" + getStatus() + "'" +
                "}";
    }
}
