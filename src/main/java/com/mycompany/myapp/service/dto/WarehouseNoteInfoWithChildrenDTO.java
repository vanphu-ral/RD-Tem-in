package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO for combined WarehouseNoteInfo with all child tables data.
 */
public class WarehouseNoteInfoWithChildrenDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("warehouse_note_info")
    private WarehouseStampInfoDTO warehouseNoteInfo;

    @JsonProperty("warehouse_note_info_details")
    private List<WarehouseStampInfoDetailDTO> warehouseNoteInfoDetails;

    @JsonProperty("serial_box_pallet_mappings")
    private List<SerialBoxPalletMappingDTO> serialBoxPalletMappings;

    @JsonProperty("pallet_infor_details")
    private List<PalletInforDetailDTO> palletInforDetails;

    // Constructors
    public WarehouseNoteInfoWithChildrenDTO() {}

    public WarehouseNoteInfoWithChildrenDTO(
        WarehouseStampInfoDTO warehouseNoteInfo,
        List<WarehouseStampInfoDetailDTO> warehouseNoteInfoDetails,
        List<SerialBoxPalletMappingDTO> serialBoxPalletMappings,
        List<GenTemConfigDTO> genNoteConfigs,
        List<PalletInforDetailDTO> palletInforDetails
    ) {
        this.warehouseNoteInfo = warehouseNoteInfo;
        this.warehouseNoteInfoDetails = warehouseNoteInfoDetails;
        this.serialBoxPalletMappings = serialBoxPalletMappings;
        this.palletInforDetails = palletInforDetails;
    }

    // Getters and Setters
    public WarehouseStampInfoDTO getWarehouseNoteInfo() {
        return warehouseNoteInfo;
    }

    public void setWarehouseNoteInfo(WarehouseStampInfoDTO warehouseNoteInfo) {
        this.warehouseNoteInfo = warehouseNoteInfo;
    }

    public List<WarehouseStampInfoDetailDTO> getWarehouseNoteInfoDetails() {
        return warehouseNoteInfoDetails;
    }

    public void setWarehouseNoteInfoDetails(
        List<WarehouseStampInfoDetailDTO> warehouseNoteInfoDetails
    ) {
        this.warehouseNoteInfoDetails = warehouseNoteInfoDetails;
    }

    public List<SerialBoxPalletMappingDTO> getSerialBoxPalletMappings() {
        return serialBoxPalletMappings;
    }

    public void setSerialBoxPalletMappings(
        List<SerialBoxPalletMappingDTO> serialBoxPalletMappings
    ) {
        this.serialBoxPalletMappings = serialBoxPalletMappings;
    }

    public List<PalletInforDetailDTO> getPalletInforDetails() {
        return palletInforDetails;
    }

    public void setPalletInforDetails(
        List<PalletInforDetailDTO> palletInforDetails
    ) {
        this.palletInforDetails = palletInforDetails;
    }

    @Override
    public String toString() {
        return (
            "WarehouseNoteInfoWithChildrenDTO{" +
            "warehouseNoteInfo=" +
            warehouseNoteInfo +
            ", warehouseNoteInfoDetailsCount=" +
            (warehouseNoteInfoDetails != null
                    ? warehouseNoteInfoDetails.size()
                    : 0) +
            ", serialBoxPalletMappingsCount=" +
            (serialBoxPalletMappings != null
                    ? serialBoxPalletMappings.size()
                    : 0) +
            ", palletInforDetailsCount=" +
            (palletInforDetails != null ? palletInforDetails.size() : 0) +
            "}"
        );
    }
}
