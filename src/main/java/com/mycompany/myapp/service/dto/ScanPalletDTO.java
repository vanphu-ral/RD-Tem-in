package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.ScanPallet} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScanPalletDTO implements Serializable {

    private Long id;

    @Size(max = 30)
    private String serialPallet;

    private Long businessSourceId;

    @Size(max = 50)
    private String businessType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialPallet() {
        return serialPallet;
    }

    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

    public Long getBusinessSourceId() {
        return businessSourceId;
    }

    public void setBusinessSourceId(Long businessSourceId) {
        this.businessSourceId = businessSourceId;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScanPalletDTO)) {
            return false;
        }

        ScanPalletDTO scanPalletDTO = (ScanPalletDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, scanPalletDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScanPalletDTO{" +
            "id=" + getId() +
            ", serialPallet='" + getSerialPallet() + "'" +
            ", businessSourceId=" + getBusinessSourceId() +
            ", businessType='" + getBusinessType() + "'" +
            "}";
    }
}
