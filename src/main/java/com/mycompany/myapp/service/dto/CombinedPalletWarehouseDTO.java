package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO for combined PalletInforDetail and WarehouseNoteInfoDetail data.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CombinedPalletWarehouseDTO implements Serializable {

    private List<PalletInforDetailDTO> palletInforDetail;

    private List<WarehouseStampInfoDetailDTO> warehouseNoteInfoDetail;

    @JsonProperty("pallet_infor_detail")
    public List<PalletInforDetailDTO> getPalletInforDetail() {
        return palletInforDetail;
    }

    @JsonProperty("pallet_infor_detail")
    public void setPalletInforDetail(
        List<PalletInforDetailDTO> palletInforDetail
    ) {
        this.palletInforDetail = palletInforDetail;
    }

    @JsonProperty("warehouse_note_info_detail")
    public List<WarehouseStampInfoDetailDTO> getWarehouseNoteInfoDetail() {
        return warehouseNoteInfoDetail;
    }

    @JsonProperty("warehouse_note_info_detail")
    public void setWarehouseNoteInfoDetail(
        List<WarehouseStampInfoDetailDTO> warehouseNoteInfoDetail
    ) {
        this.warehouseNoteInfoDetail = warehouseNoteInfoDetail;
    }

    @Override
    public String toString() {
        return (
            "CombinedPalletWarehouseDTO{" +
            "palletInforDetail=" +
            getPalletInforDetail() +
            ", warehouseNoteInfoDetail=" +
            getWarehouseNoteInfoDetail() +
            "}"
        );
    }
}
