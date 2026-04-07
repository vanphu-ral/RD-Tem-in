package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the InboundWMSPalletScanResponse.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InboundWMSPalletScanResponseDTO implements Serializable {

    private WarehouseStampInfoDTO warehouseNoteInfo;

    private InboundWMSPalletDTO inboundpalletInfo;

    public InboundWMSPalletDTO getInboundpalletInfo() {
        return inboundpalletInfo;
    }

    public WarehouseStampInfoDTO getWarehouseNoteInfo() {
        return warehouseNoteInfo;
    }

    public void setWarehouseNoteInfo(WarehouseStampInfoDTO warehouseNoteInfo) {
        this.warehouseNoteInfo = warehouseNoteInfo;
    }

    public void setInboundpalletInfo(InboundWMSPalletDTO inboundpalletInfo) {
        this.inboundpalletInfo = inboundpalletInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InboundWMSPalletScanResponseDTO)) {
            return false;
        }

        InboundWMSPalletScanResponseDTO inboundWMSPalletScanResponseDTO =
            (InboundWMSPalletScanResponseDTO) o;
        if (this.warehouseNoteInfo == null) {
            return false;
        }
        return Objects.equals(
            this.warehouseNoteInfo,
            inboundWMSPalletScanResponseDTO.warehouseNoteInfo
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.warehouseNoteInfo);
    }

    @Override
    public String toString() {
        return (
            "InboundWMSPalletScanResponseDTO{" +
            "inboundpalletInfo=" +
            getInboundpalletInfo() +
            ", warehouseNoteInfo=" +
            getWarehouseNoteInfo() +
            "}"
        );
    }
}
