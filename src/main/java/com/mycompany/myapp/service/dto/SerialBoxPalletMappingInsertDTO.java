package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * A DTO for inserting SerialBoxPalletMapping.
 */
public class SerialBoxPalletMappingInsertDTO implements Serializable {

    @Size(max = 50)
    @NotNull
    @JsonProperty("serial_box")
    private String serialBox;

    @Size(max = 50)
    @NotNull
    @JsonProperty("serial_pallet")
    private String serialPallet;

    private Integer status;

    @JsonProperty("updated_by")
    private String updatedBy;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return (
            "SerialBoxPalletMappingInsertDTO{" +
            "serialBox='" +
            serialBox +
            '\'' +
            ", serialPallet='" +
            serialPallet +
            '\'' +
            ", status=" +
            status +
            ", updatedBy='" +
            updatedBy +
            '\'' +
            '}'
        );
    }
}
