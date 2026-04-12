package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A DTO for inbound WMS box scan request.
 */
public class InboundWMSBoxScanDTO implements Serializable {

    private Long inboundWmsSessionId;

    private String serialBox;

    private String scannedBy;

    private ZonedDateTime scannedAt;

    public InboundWMSBoxScanDTO() {}

    public InboundWMSBoxScanDTO(
        Long inboundWmsSessionId,
        String serialBox,
        String scannedBy,
        ZonedDateTime scannedAt
    ) {
        this.inboundWmsSessionId = inboundWmsSessionId;
        this.serialBox = serialBox;
        this.scannedBy = scannedBy;
        this.scannedAt = scannedAt;
    }

    public Long getInboundWmsSessionId() {
        return inboundWmsSessionId;
    }

    public void setInboundWmsSessionId(Long inboundWmsSessionId) {
        this.inboundWmsSessionId = inboundWmsSessionId;
    }

    public String getSerialBox() {
        return serialBox;
    }

    public void setSerialBox(String serialBox) {
        this.serialBox = serialBox;
    }

    public String getScannedBy() {
        return scannedBy;
    }

    public void setScannedBy(String scannedBy) {
        this.scannedBy = scannedBy;
    }

    public ZonedDateTime getScannedAt() {
        return scannedAt;
    }

    public void setScannedAt(ZonedDateTime scannedAt) {
        this.scannedAt = scannedAt;
    }

    @Override
    public String toString() {
        return (
            "InboundWMSBoxScanDTO{" +
            "inboundWmsSessionId=" +
            inboundWmsSessionId +
            ", serialBox='" +
            serialBox +
            "'" +
            ", scannedBy='" +
            scannedBy +
            "'" +
            ", scannedAt='" +
            scannedAt +
            "'" +
            "}"
        );
    }
}
