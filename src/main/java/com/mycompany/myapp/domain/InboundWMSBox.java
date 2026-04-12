package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.checkerframework.checker.units.qual.C;

/**
 * A InboundWMSBox.
 */
@Entity
@Table(name = "inbound_wms_box")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InboundWMSBox implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "warehouse_note_info_id")
    private Integer warehouseNoteInfoId;

    @Column(name = "inbound_wms_session_id")
    private Integer inboundWMSSessionId;

    @Size(max = 50)
    @Column(name = "serial_box", length = 50)
    private String serialBox;

    @Column(name = "wms_send_status")
    private Boolean wmsSendStatus;

    @Size(max = 15)
    @Column(name = "created_by", length = 15)
    private String createdBy;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InboundWMSBox id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getWarehouseNoteInfoId() {
        return this.warehouseNoteInfoId;
    }

    public InboundWMSBox warehouseNoteInfoId(Integer warehouseNoteInfoId) {
        this.setWarehouseNoteInfoId(warehouseNoteInfoId);
        return this;
    }

    public void setWarehouseNoteInfoId(Integer warehouseNoteInfoId) {
        this.warehouseNoteInfoId = warehouseNoteInfoId;
    }

    public Integer getInboundWMSSessionId() {
        return this.inboundWMSSessionId;
    }

    public InboundWMSBox inboundWMSSessionId(Integer inboundWMSSessionId) {
        this.setInboundWMSSessionId(inboundWMSSessionId);
        return this;
    }

    public void setInboundWMSSessionId(Integer inboundWMSSessionId) {
        this.inboundWMSSessionId = inboundWMSSessionId;
    }

    public String getSerialBox() {
        return this.serialBox;
    }

    public InboundWMSBox serialBox(String serialBox) {
        this.setSerialBox(serialBox);
        return this;
    }

    public void setSerialBox(String serialBox) {
        this.serialBox = serialBox;
    }

    public Boolean getWmsSendStatus() {
        return this.wmsSendStatus;
    }

    public InboundWMSBox wmsSendStatus(Boolean wmsSendStatus) {
        this.setWmsSendStatus(wmsSendStatus);
        return this;
    }

    public void setWmsSendStatus(Boolean wmsSendStatus) {
        this.wmsSendStatus = wmsSendStatus;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public InboundWMSBox createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public InboundWMSBox createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InboundWMSBox)) {
            return false;
        }
        return getId() != null && getId().equals(((InboundWMSBox) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InboundWMSBox{" +
            "id=" + getId() +
            ", warehouseNoteInfoId=" + getWarehouseNoteInfoId() +
            ", serialBox='" + getSerialBox() + "'" +
            ", wmsSendStatus='" + getWmsSendStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
