package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO for combined WarehouseNoteInfoApproval with all child tables data.
 */
public class WarehouseNoteInfoApprovalWithChildrenDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("warehouse_note_info")
    private WarehouseNoteInfoApprovalDTO warehouseNoteInfo;

    @JsonProperty("warehouse_note_info_details")
    private List<WarehouseStampInfoDetailDTO> warehouseNoteInfoDetails;

    @JsonProperty("serial_box_pallet_mappings")
    private List<SerialBoxPalletMappingDTO> serialBoxPalletMappings;

    @JsonProperty("pallet_infor_details")
    private List<PalletInforDetailDTO> palletInforDetails;

    // Constructors
    public WarehouseNoteInfoApprovalWithChildrenDTO() {}

    public WarehouseNoteInfoApprovalWithChildrenDTO(
        WarehouseNoteInfoApprovalDTO warehouseNoteInfo,
        List<WarehouseStampInfoDetailDTO> warehouseNoteInfoDetails,
        List<SerialBoxPalletMappingDTO> serialBoxPalletMappings,
        List<PalletInforDetailDTO> palletInforDetails
    ) {
        this.warehouseNoteInfo = warehouseNoteInfo;
        this.warehouseNoteInfoDetails = warehouseNoteInfoDetails;
        this.serialBoxPalletMappings = serialBoxPalletMappings;
        this.palletInforDetails = palletInforDetails;
    }

    // Getters and Setters
    public WarehouseNoteInfoApprovalDTO getWarehouseNoteInfo() {
        return warehouseNoteInfo;
    }

    public void setWarehouseNoteInfo(
        WarehouseNoteInfoApprovalDTO warehouseNoteInfo
    ) {
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
            "WarehouseNoteInfoApprovalWithChildrenDTO{" +
            "warehouse_note_info=" +
            warehouseNoteInfo +
            ", warehouse_note_info_details_count=" +
            (warehouseNoteInfoDetails != null
                    ? warehouseNoteInfoDetails.size()
                    : 0) +
            ", serial_box_pallet_mappings_count=" +
            (serialBoxPalletMappings != null
                    ? serialBoxPalletMappings.size()
                    : 0) +
            ", pallet_infor_details_count=" +
            (palletInforDetails != null ? palletInforDetails.size() : 0) +
            "}"
        );
    }
}
