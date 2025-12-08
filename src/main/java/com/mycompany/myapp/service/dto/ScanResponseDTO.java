package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO for scan response containing pallet and warehouse information.
 */
public class ScanResponseDTO implements Serializable {

    @JsonProperty("pallet_infor_detail")
    private List<PalletInforDetailDTO> palletInforDetail;

    @JsonProperty("warehouse_note_info_detail")
    private List<WarehouseStampInfoDetailDTO> warehouseNoteInfoDetail;

    public ScanResponseDTO() {}

    public ScanResponseDTO(
        List<PalletInforDetailDTO> palletInforDetail,
        List<WarehouseStampInfoDetailDTO> warehouseNoteInfoDetail
    ) {
        this.palletInforDetail = palletInforDetail;
        this.warehouseNoteInfoDetail = warehouseNoteInfoDetail;
    }

    public List<PalletInforDetailDTO> getPalletInforDetail() {
        return palletInforDetail;
    }

    public void setPalletInforDetail(
        List<PalletInforDetailDTO> palletInforDetail
    ) {
        this.palletInforDetail = palletInforDetail;
    }

    public List<WarehouseStampInfoDetailDTO> getWarehouseNoteInfoDetail() {
        return warehouseNoteInfoDetail;
    }

    public void setWarehouseNoteInfoDetail(
        List<WarehouseStampInfoDetailDTO> warehouseNoteInfoDetail
    ) {
        this.warehouseNoteInfoDetail = warehouseNoteInfoDetail;
    }

    @Override
    public String toString() {
        return (
            "ScanResponseDTO{" +
            "palletInforDetail=" +
            palletInforDetail +
            ", warehouseNoteInfoDetail=" +
            warehouseNoteInfoDetail +
            '}'
        );
    }
}
