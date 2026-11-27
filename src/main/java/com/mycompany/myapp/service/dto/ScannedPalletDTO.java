package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ScannedPalletDTO implements Serializable {

    private String serialPallet;
    private List<WarehouseTransferDetailDTO> boxes;

    public String getSerialPallet() {
        return serialPallet;
    }

    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

    public List<WarehouseTransferDetailDTO> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<WarehouseTransferDetailDTO> boxes) {
        this.boxes = boxes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScannedPalletDTO that = (ScannedPalletDTO) o;
        return Objects.equals(serialPallet, that.serialPallet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialPallet);
    }
}
