package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Request body used to register a list of {@link VendorLabelInfoDTO} for one pallet.
 *
 * Every item of {@code vendorLabelInfoList} is saved into the table
 * {@code vendor_label_info} and linked to the pallet through the table
 * {@code pallet_box_mapping} (pallet_box_mapping.serial_pallet +
 * pallet_box_mapping.reel_id_box = vendor_label_info.reel_id).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PalletVendorLabelInfoRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "serialPallet is required")
    @Size(max = 30, message = "serialPallet must not exceed 30 characters")
    private String serialPallet;

    @NotEmpty(message = "vendorLabelInfoList is required")
    @Valid
    private List<VendorLabelInfoDTO> vendorLabelInfoList = new ArrayList<>();

    public String getSerialPallet() {
        return serialPallet;
    }

    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

    public List<VendorLabelInfoDTO> getVendorLabelInfoList() {
        return vendorLabelInfoList;
    }

    public void setVendorLabelInfoList(
        List<VendorLabelInfoDTO> vendorLabelInfoList
    ) {
        this.vendorLabelInfoList = vendorLabelInfoList;
    }

    @Override
    public String toString() {
        return (
            "PalletVendorLabelInfoRequestDTO{" +
            "serialPallet='" +
            serialPallet +
            '\'' +
            ", vendorLabelInfoCount=" +
            (vendorLabelInfoList == null ? 0 : vendorLabelInfoList.size()) +
            "}"
        );
    }
}
