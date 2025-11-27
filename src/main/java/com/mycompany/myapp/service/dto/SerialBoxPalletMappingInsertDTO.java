package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * A DTO for inserting SerialBoxPalletMapping.
 */
public class SerialBoxPalletMappingInsertDTO implements Serializable {

    @Size(max = 50)
    @NotNull
    private String serialBox;

    @Size(max = 50)
    @NotNull
    private String serialPallet;

    private Integer status;

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
            '}'
        );
    }
}
