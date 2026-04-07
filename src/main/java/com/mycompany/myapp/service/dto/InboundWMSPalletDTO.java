package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.InboundWMSPallet} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InboundWMSPalletDTO implements Serializable {

    private Long id;

    private WarehouseStampInfoDTO warehouseNoteInfo; // (dung lay thong tin ma lenh, san pham...)

    private Integer inboundWMSSessionId;

    private Integer warehouseNoteInfoId;

    @Size(max = 50)
    private String serialPallet;

    @Size(max = 50)
    private String wmsSendStatus;

    @Size(max = 15)
    private String createdBy;

    private ZonedDateTime createdAt;

    private List<BoxInfoDTO> listBox;

    // private InboundWMSSessionDTO inboundWMSSession;

    public WarehouseStampInfoDTO getWarehouseNoteInfo() {
        return warehouseNoteInfo;
    }

    public void setWarehouseNoteInfo(WarehouseStampInfoDTO warehouseNoteInfo) {
        this.warehouseNoteInfo = warehouseNoteInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInboundWMSSessionId() {
        return inboundWMSSessionId;
    }

    public void setInboundWMSSessionId(Integer inboundWMSSessionId) {
        this.inboundWMSSessionId = inboundWMSSessionId;
    }

    public Integer getWarehouseNoteInfoId() {
        return warehouseNoteInfoId;
    }

    public void setWarehouseNoteInfoId(Integer warehouseNoteInfoId) {
        this.warehouseNoteInfoId = warehouseNoteInfoId;
    }

    public String getSerialPallet() {
        return serialPallet;
    }

    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

    public String getWmsSendStatus() {
        return wmsSendStatus;
    }

    public void setWmsSendStatus(String wmsSendStatus) {
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

    public List<BoxInfoDTO> getListBox() {
        return listBox;
    }

    public void setListBox(List<BoxInfoDTO> listBox) {
        this.listBox = listBox;
    }

    // public InboundWMSSessionDTO getInboundWMSSession() {
    //     return inboundWMSSession;
    // }

    // public void setInboundWMSSession(InboundWMSSessionDTO inboundWMSSession) {
    //     this.inboundWMSSession = inboundWMSSession;
    // }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InboundWMSPalletDTO)) {
            return false;
        }

        InboundWMSPalletDTO inboundWMSPalletDTO = (InboundWMSPalletDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, inboundWMSPalletDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InboundWMSPalletDTO{" +
            "id=" + getId() +
            ", inboundWMSSessionId=" + getInboundWMSSessionId() +
            ", warehouseNoteInfoId=" + getWarehouseNoteInfoId() +
            ", serialPallet='" + getSerialPallet() + "'" +
            ", wmsSendStatus='" + getWmsSendStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", listBox=" + getListBox() +
            // ", inboundWMSSession=" + getInboundWMSSession() +
            "}";
    }
}
