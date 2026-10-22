package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.OwtqWarehouseTransferSession} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OwtqWarehouseTransferSessionDTO implements Serializable {

    private Long id;

    private Long warehouseTransferSessionId;

    private Long owtqDocEntry;

    private WarehouseTransferSessionDTO warehouseTransferSession;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwtqWarehouseTransferSessionId() {
        return warehouseTransferSessionId;
    }

    public void setOwtqWarehouseTransferSessionId(
        Long warehouseTransferSessionId
    ) {
        this.warehouseTransferSessionId = warehouseTransferSessionId;
    }

    public Long getOwtqDocEntry() {
        return owtqDocEntry;
    }

    public void setOwtqDocEntry(Long owtqDocEntry) {
        this.owtqDocEntry = owtqDocEntry;
    }

    public WarehouseTransferSessionDTO getWarehouseTransferSession() {
        return warehouseTransferSession;
    }

    public void setWarehouseTransferSession(
        WarehouseTransferSessionDTO warehouseTransferSession
    ) {
        this.warehouseTransferSession = warehouseTransferSession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OwtqWarehouseTransferSessionDTO)) {
            return false;
        }

        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO =
            (OwtqWarehouseTransferSessionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, owtqWarehouseTransferSessionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OwtqWarehouseTransferSessionDTO{" +
            "id=" + getId() +
            ", warehouseTransferSessionId=" + getOwtqWarehouseTransferSessionId() +
            ", owtqDocEntry=" + getOwtqDocEntry() +
            ", warehouseTransferSession=" + getWarehouseTransferSession() +
            "}";
    }
}
