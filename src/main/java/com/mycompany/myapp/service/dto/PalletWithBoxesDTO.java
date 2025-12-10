package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * A DTO for representing a Pallet with its associated Boxes.
 * Extends PalletInforDetailDTO to include all pallet fields directly.
 */
public class PalletWithBoxesDTO
    extends PalletInforDetailDTO
    implements Serializable {

    @JsonProperty("list_box")
    private List<WarehouseStampInfoDetailDTO> listBox;

    public PalletWithBoxesDTO() {
        super();
    }

    public PalletWithBoxesDTO(
        PalletInforDetailDTO palletInfo,
        List<WarehouseStampInfoDetailDTO> listBox
    ) {
        super();
        // Copy all fields from palletInfo
        if (palletInfo != null) {
            this.setId(palletInfo.getId());
            this.setSerialPallet(palletInfo.getSerialPallet());
            this.setMaLenhSanXuatId(palletInfo.getMaLenhSanXuatId());
            this.setQuantityPerBox(palletInfo.getQuantityPerBox());
            this.setNote(palletInfo.getNote());
            this.setNumBoxPerPallet(palletInfo.getNumBoxPerPallet());
            this.setCustomerName(palletInfo.getCustomerName());
            this.setPoNumber(palletInfo.getPoNumber());
            this.setDateCode(palletInfo.getDateCode());
            this.setItemNoSku(palletInfo.getItemNoSku());
            this.setQdsxNo(palletInfo.getQdsxNo());
            this.setProductionDate(palletInfo.getProductionDate());
            this.setInspectorName(palletInfo.getInspectorName());
            this.setInspectionResult(palletInfo.getInspectionResult());
            this.setScanProgress(palletInfo.getScanProgress());
            this.setNumBoxActual(palletInfo.getNumBoxActual());
            this.setNumBoxConfig(palletInfo.getNumBoxConfig());
            this.setUpdatedAt(palletInfo.getUpdatedAt());
            this.setUpdatedBy(palletInfo.getUpdatedBy());
            this.setWmsSendStatus(palletInfo.getWmsSendStatus());
            this.setPrintStatus(palletInfo.getPrintStatus());
        }
        this.listBox = listBox;
    }

    public List<WarehouseStampInfoDetailDTO> getListBox() {
        return listBox;
    }

    public void setListBox(List<WarehouseStampInfoDetailDTO> listBox) {
        this.listBox = listBox;
    }

    @Override
    public String toString() {
        return (
            "PalletWithBoxesDTO{" +
            "id=" +
            getId() +
            ", serialPallet='" +
            getSerialPallet() +
            "'" +
            ", listBox=" +
            listBox +
            "}"
        );
    }
}
