package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.WarehouseNoteInfo;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.InboundWMSBox} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InboundWMSBoxDTO implements Serializable {

    private Long id;

    private Integer warehouseNoteInfoId;

    private Integer inboundWMSSessionId;

    @Size(max = 50)
    private String serialBox;

    private Boolean wmsSendStatus;

    @Size(max = 15)
    private String createdBy;

    private ZonedDateTime createdAt;

    private WarehouseNoteInfo warehouseNoteInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getWarehouseNoteInfoId() {
        return warehouseNoteInfoId;
    }

    public void setWarehouseNoteInfoId(Integer warehouseNoteInfoId) {
        this.warehouseNoteInfoId = warehouseNoteInfoId;
    }

    public Integer getInboundWMSSessionId() {
        return inboundWMSSessionId;
    }

    public void setInboundWMSSessionId(Integer inboundWMSSessionId) {
        this.inboundWMSSessionId = inboundWMSSessionId;
    }

    public String getSerialBox() {
        return serialBox;
    }

    public void setSerialBox(String serialBox) {
        this.serialBox = serialBox;
    }

    public Boolean getWmsSendStatus() {
        return wmsSendStatus;
    }

    public void setWmsSendStatus(Boolean wmsSendStatus) {
        this.wmsSendStatus = wmsSendStatus;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public WarehouseNoteInfo getWarehouseNoteInfo() {
        return warehouseNoteInfo;
    }

    public void setWarehouseNoteInfo(WarehouseNoteInfo warehouseNoteInfo) {
        this.warehouseNoteInfo = warehouseNoteInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InboundWMSBoxDTO)) {
            return false;
        }

        InboundWMSBoxDTO inboundWMSBoxDTO = (InboundWMSBoxDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, inboundWMSBoxDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InboundWMSBoxDTO{" +
            "id=" + getId() +
            ", warehouseNoteInfoId=" + getWarehouseNoteInfoId() +
            ", inboundWMSSessionId=" + getInboundWMSSessionId() +
            ", serialBox='" + getSerialBox() + "'" +
            ", wmsSendStatus='" + getWmsSendStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", warehouseNoteInfo='" + getWarehouseNoteInfo() + "'" +
            "}";
    }
}
