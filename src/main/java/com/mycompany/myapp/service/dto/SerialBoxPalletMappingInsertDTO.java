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
            '}'
        );
    }
}
