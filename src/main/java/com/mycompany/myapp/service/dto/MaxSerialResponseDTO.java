package com.mycompany.myapp.service.dto;

import java.io.Serializable;

/**
 * DTO for returning max serial numbers.
 */
public class MaxSerialResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String lastSerialPallet;
    private String lastSerialBox;

    public MaxSerialResponseDTO() {}

    public MaxSerialResponseDTO(String lastSerialPallet, String lastSerialBox) {
        this.lastSerialPallet = lastSerialPallet;
        this.lastSerialBox = lastSerialBox;
    }

    public String getLastSerialPallet() {
        return lastSerialPallet;
    }

    public void setLastSerialPallet(String lastSerialPallet) {
        this.lastSerialPallet = lastSerialPallet;
    }

    public String getLastSerialBox() {
        return lastSerialBox;
    }

    public void setLastSerialBox(String lastSerialBox) {
        this.lastSerialBox = lastSerialBox;
    }

    @Override
    public String toString() {
        return (
            "MaxSerialResponseDTO{" +
            "latestSerialPallet='" +
            lastSerialPallet +
            '\'' +
            ", latestSerialBox='" +
            lastSerialBox +
            '\'' +
            '}'
        );
    }
}
