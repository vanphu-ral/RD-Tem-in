package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.SerialBoxPalletMapping}
 * entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SerialBoxPalletMappingDTO implements Serializable {

    private Long id;

    @Size(max = 50)
    @JsonProperty("serial_box")
    private String serialBox;

    @Size(max = 50)
    @JsonProperty("serial_pallet")
    private String serialPallet;

    private Instant updatedAt;

    @Size(max = 50)
    private String updatedBy;

    private Integer status;

    private Long maLenhSanXuatId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialBox() {
        return serialBox;
    }

    public void setSerialBox(String serialBox) {
        this.serialBox = serialBox;
    }

    public String getSerialPallet() {
        return serialPallet;
    }

    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getMaLenhSanXuatId() {
        return maLenhSanXuatId;
    }

    public void setMaLenhSanXuatId(Long maLenhSanXuatId) {
        this.maLenhSanXuatId = maLenhSanXuatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SerialBoxPalletMappingDTO)) {
            return false;
        }

        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO =
            (SerialBoxPalletMappingDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, serialBoxPalletMappingDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SerialBoxPalletMappingDTO{" +
                "id=" + getId() +
                ", serialBox='" + getSerialBox() + "'" +
                ", serialPallet='" + getSerialPallet() + "'" +
                ", updatedAt='" + getUpdatedAt() + "'" +
                ", updatedBy='" + getUpdatedBy() + "'" +
                ", status=" + getStatus() +
                ", maLenhSanXuatId=" + getMaLenhSanXuatId() +
                "}";
    }
}
