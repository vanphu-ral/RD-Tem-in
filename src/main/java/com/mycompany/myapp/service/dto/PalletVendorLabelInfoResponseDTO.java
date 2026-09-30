package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.List;

/**
 * Response returned after saving the vendor label information of one pallet.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PalletVendorLabelInfoResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serialPallet;

    private List<PalletVendorLabelInfoItemDTO> vendorLabelInfoList;

    public String getSerialPallet() {
        return serialPallet;
    }

    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

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
            "PalletVendorLabelInfoResponseDTO{" +
            "serialPallet='" +
            serialPallet +
            '\'' +
            ", vendorLabelInfoCount=" +
            (vendorLabelInfoList == null ? 0 : vendorLabelInfoList.size()) +
            "}"
        );
    }
}
