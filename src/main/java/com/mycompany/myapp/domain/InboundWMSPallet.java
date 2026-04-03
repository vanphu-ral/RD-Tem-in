package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A InboundWMSPallet.
 */
@Entity
@Table(name = "inbound_wms_pallet")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InboundWMSPallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequenceGenerator"
    )
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "inbound_wms_session_id")
    private Integer inboundWMSSessionId;

    @Column(name = "warehouse_note_info_id")
    private Integer warehouseNoteInfoId;

    @Size(max = 50)
    @Column(name = "serial_pallet", length = 50)
    private String serialPallet;

    @Size(max = 50)
    @Column(name = "wms_send_status", length = 50)
    private String wmsSendStatus;

    @Size(max = 15)
    @Column(name = "created_by", length = 15)
    private String createdBy;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "inboundWMSPallets" }, allowSetters = true)
    private InboundWMSSession inboundWMSSession;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InboundWMSPallet id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInboundWMSSessionId() {
        return this.inboundWMSSessionId;
    }

    public InboundWMSPallet inboundWMSSessionId(Integer inboundWMSSessionId) {
        this.setInboundWMSSessionId(inboundWMSSessionId);
        return this;
    }

    public void setInboundWMSSessionId(Integer inboundWMSSessionId) {
        this.inboundWMSSessionId = inboundWMSSessionId;
    }

    public Integer getWarehouseNoteInfoId() {
        return this.warehouseNoteInfoId;
    }

    public InboundWMSPallet warehouseNoteInfoId(Integer warehouseNoteInfoId) {
        this.setWarehouseNoteInfoId(warehouseNoteInfoId);
        return this;
    }

    public void setWarehouseNoteInfoId(Integer warehouseNoteInfoId) {
        this.warehouseNoteInfoId = warehouseNoteInfoId;
    }

    public String getSerialPallet() {
        return this.serialPallet;
    }

    public InboundWMSPallet serialPallet(String serialPallet) {
        this.setSerialPallet(serialPallet);
        return this;
    }

    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

    public String getWmsSendStatus() {
        return this.wmsSendStatus;
    }

    public InboundWMSPallet wmsSendStatus(String wmsSendStatus) {
        this.setWmsSendStatus(wmsSendStatus);
        return this;
    }

    public void setWmsSendStatus(String wmsSendStatus) {
        this.wmsSendStatus = wmsSendStatus;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public InboundWMSPallet createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public InboundWMSPallet createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public InboundWMSSession getInboundWMSSession() {
        return this.inboundWMSSession;
    }

    public void setInboundWMSSession(InboundWMSSession inboundWMSSession) {
        this.inboundWMSSession = inboundWMSSession;
    }

    public InboundWMSPallet inboundWMSSession(
        InboundWMSSession inboundWMSSession
    ) {
        this.setInboundWMSSession(inboundWMSSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InboundWMSPallet)) {
            return false;
        }
        return (
            getId() != null && getId().equals(((InboundWMSPallet) o).getId())
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
        return "InboundWMSPallet{" +
            "id=" + getId() +
            ", inboundWMSSessionId=" + getInboundWMSSessionId() +
            ", warehouseNoteInfoId=" + getWarehouseNoteInfoId() +
            ", serialPallet='" + getSerialPallet() + "'" +
            ", wmsSendStatus='" + getWmsSendStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
