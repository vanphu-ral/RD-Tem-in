package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * A {@link PalletMngtDTO} enriched with the boxes (vendor label information)
 * linked to the pallet through {@code pallet_box_mapping}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PalletMngtDetailDTO extends PalletMngtDTO {

    private static final long serialVersionUID = 1L;

    private List<PalletVendorLabelInfoItemDTO> vendorLabelInfoList;

    public List<PalletVendorLabelInfoItemDTO> getVendorLabelInfoList() {
        return vendorLabelInfoList;
    }

    public void setVendorLabelInfoList(
        List<PalletVendorLabelInfoItemDTO> vendorLabelInfoList
    ) {
        this.vendorLabelInfoList = vendorLabelInfoList;
    }

    @Override
    public String toString() {
        return (
            "PalletMngtDetailDTO{" +
            "id=" +
            getId() +
            ", serialPallet='" +
            getSerialPallet() +
            '\'' +
            ", vendorLabelInfoCount=" +
            (vendorLabelInfoList == null ? 0 : vendorLabelInfoList.size()) +
            "}"
        );
    }
}
