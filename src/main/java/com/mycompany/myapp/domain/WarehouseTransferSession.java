package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A WarehouseTransferSession.
 */
@Entity
@Table(name = "warehouse_transfer_session")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseTransferSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @Size(max = 255)
    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "created_at")
    private Instant createdAt;

    @Size(max = 50)
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Size(max = 50)
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Size(max = 50)
    @Column(name = "deleted_by", length = 50)
    private String deletedBy;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "warehouseTransferSession")
    @JsonIgnoreProperties(
        value = { "warehouseTransferSession" },
        allowSetters = true
    )
    private Set<WarehouseTransferDetail> details = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WarehouseTransferSession id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return this.status;
    }

    public WarehouseTransferSession status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return this.note;
    }

    public WarehouseTransferSession note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public WarehouseTransferSession createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public WarehouseTransferSession createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public WarehouseTransferSession updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public WarehouseTransferSession updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public WarehouseTransferSession deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedBy() {
        return this.deletedBy;
    }

    public WarehouseTransferSession deletedBy(String deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Set<WarehouseTransferDetail> getDetails() {
        return this.details;
    }

    public void setDetails(
        Set<WarehouseTransferDetail> warehouseTransferDetails
    ) {
        if (this.details != null) {
            this.details.forEach(i -> i.setWarehouseTransferSession(null));
        }
        if (warehouseTransferDetails != null) {
            warehouseTransferDetails.forEach(i ->
                i.setWarehouseTransferSession(this)
            );
        }
        this.details = warehouseTransferDetails;
    }

    public WarehouseTransferSession details(
        Set<WarehouseTransferDetail> warehouseTransferDetails
    ) {
        this.setDetails(warehouseTransferDetails);
        return this;
    }

    public WarehouseTransferSession addDetails(
        WarehouseTransferDetail warehouseTransferDetail
    ) {
        this.details.add(warehouseTransferDetail);
        warehouseTransferDetail.setWarehouseTransferSession(this);
        return this;
    }

    public WarehouseTransferSession removeDetails(
        WarehouseTransferDetail warehouseTransferDetail
    ) {
        this.details.remove(warehouseTransferDetail);
        warehouseTransferDetail.setWarehouseTransferSession(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WarehouseTransferSession)) {
            return false;
        }
        return (
            getId() != null &&
            getId().equals(((WarehouseTransferSession) o).getId())
        );
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WarehouseTransferSession{" +
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
