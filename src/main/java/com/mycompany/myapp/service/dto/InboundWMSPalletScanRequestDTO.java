package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the InboundWMSPalletScanRequest.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InboundWMSPalletScanRequestDTO implements Serializable {

    private Long id;

    @JsonProperty("inbound_wms_session_id")
    private Integer inboundWmsSessionId;

    @JsonProperty("serial_pallet")
    @Size(max = 50)
    private String serialPallet;

    @JsonProperty("scaned_by")
    @Size(max = 15)
    private String scanedBy;

    @JsonProperty("scaned_at")
    private ZonedDateTime scanedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInboundWmsSessionId() {
        return inboundWmsSessionId;
    }

    public void setInboundWmsSessionId(Integer inboundWmsSessionId) {
        this.inboundWmsSessionId = inboundWmsSessionId;
    }

    public String getSerialPallet() {
        return serialPallet;
    }

    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

    public String getScanedBy() {
        return scanedBy;
    }

    public void setScanedBy(String scanedBy) {
        this.scanedBy = scanedBy;
    }

    public ZonedDateTime getScanedAt() {
        return scanedAt;
    }

    public void setScanedAt(ZonedDateTime scanedAt) {
        this.scanedAt = scanedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InboundWMSPalletScanRequestDTO)) {
            return false;
        }

        InboundWMSPalletScanRequestDTO inboundWMSPalletScanRequestDTO =
            (InboundWMSPalletScanRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, inboundWMSPalletScanRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "InboundWMSPalletScanRequestDTO{" +
            "id=" +
            getId() +
            ", inboundWmsSessionId=" +
            getInboundWmsSessionId() +
            ", serialPallet='" +
            getSerialPallet() +
            "'" +
            ", scanedBy='" +
            getScanedBy() +
            "'" +
            ", scanedAt='" +
            getScanedAt() +
            "'" +
            "}"
        );
    }
}
