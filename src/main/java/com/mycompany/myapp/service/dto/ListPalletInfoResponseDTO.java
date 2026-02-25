package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the response of list pallet info detail endpoint.
 */
public class ListPalletInfoResponseDTO implements Serializable {

    @JsonProperty("list_pallet")
    private List<PalletWithBoxesDTO> listPallet;

    @JsonProperty("list_box")
    private List<WarehouseStampInfoDetailDTO> listBox;

    public ListPalletInfoResponseDTO() {}

    public ListPalletInfoResponseDTO(List<PalletWithBoxesDTO> listPallet) {
        this.listPallet = listPallet;
    }

    public ListPalletInfoResponseDTO(
        List<PalletWithBoxesDTO> listPallet,
        List<WarehouseStampInfoDetailDTO> listBox
    ) {
        this.listPallet = listPallet;
        this.listBox = listBox;
    }

    public List<PalletWithBoxesDTO> getListPallet() {
        return listPallet;
    }

    public List<WarehouseStampInfoDetailDTO> getListBox() {
        return listBox;
    }

    public void setListPallet(List<PalletWithBoxesDTO> listPallet) {
        this.listPallet = listPallet;
    }

    public void setListBox(List<WarehouseStampInfoDetailDTO> listBox) {
        this.listBox = listBox;
    }

    @Override
    public String toString() {
        return (
            "ListPalletInfoResponseDTO{" +
            "listPallet=" +
            listPallet +
            ", listBox=" +
            listBox +
            "}"
        );
    }
}
