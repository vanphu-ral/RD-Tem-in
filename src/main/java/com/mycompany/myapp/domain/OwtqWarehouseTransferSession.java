package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A OwtqWarehouseTransferSession.
 */
@Entity
@Table(name = "owtq_warehouse_transfer_session")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OwtqWarehouseTransferSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(
        name = "warehouse_transfer_session_id",
        insertable = false,
        updatable = false
    )
    private Long warehouseTransferSessionId;

    @Column(name = "owtq_doc_entry")
    private Long owtqDocEntry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_transfer_session_id")
    @JsonIgnoreProperties(value = { "details" }, allowSetters = true)
    private WarehouseTransferSession warehouseTransferSession;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OwtqWarehouseTransferSession id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwtqWarehouseTransferSessionId() {
        return this.warehouseTransferSessionId;
    }

    public OwtqWarehouseTransferSession warehouseTransferSessionId(
        Long warehouseTransferSessionId
    ) {
        this.setOwtqWarehouseTransferSessionId(warehouseTransferSessionId);
        return this;
    }

    public void setOwtqWarehouseTransferSessionId(
        Long warehouseTransferSessionId
    ) {
        this.warehouseTransferSessionId = warehouseTransferSessionId;
    }

    public Long getOwtqDocEntry() {
        return this.owtqDocEntry;
    }

    public OwtqWarehouseTransferSession owtqDocEntry(Long owtqDocEntry) {
        this.setOwtqDocEntry(owtqDocEntry);
        return this;
    }

    public void setOwtqDocEntry(Long owtqDocEntry) {
        this.owtqDocEntry = owtqDocEntry;
    }

    public WarehouseTransferSession getWarehouseTransferSession() {
        return this.warehouseTransferSession;
    }

    public void setWarehouseTransferSession(
        WarehouseTransferSession warehouseTransferSession
    ) {
        this.warehouseTransferSession = warehouseTransferSession;
    }

    public OwtqWarehouseTransferSession warehouseTransferSession(
        WarehouseTransferSession warehouseTransferSession
    ) {
        this.setWarehouseTransferSession(warehouseTransferSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OwtqWarehouseTransferSession)) {
            return false;
        }
        return (
            getId() != null &&
            getId().equals(((OwtqWarehouseTransferSession) o).getId())
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
        return "OwtqWarehouseTransferSession{" +
            "id=" + getId() +
            ", warehouseTransferSessionId=" + getOwtqWarehouseTransferSessionId() +
            ", owtqDocEntry=" + getOwtqDocEntry() +
            "}";
    }
}
