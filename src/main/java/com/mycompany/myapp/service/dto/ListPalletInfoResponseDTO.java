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

    public ListPalletInfoResponseDTO() {}

    public ListPalletInfoResponseDTO(List<PalletWithBoxesDTO> listPallet) {
        this.listPallet = listPallet;
    }

    public List<PalletWithBoxesDTO> getListPallet() {
        return listPallet;
    }

    public void setListPallet(List<PalletWithBoxesDTO> listPallet) {
        this.listPallet = listPallet;
    }

    @Override
    public String toString() {
        return "ListPalletInfoResponseDTO{" + "listPallet=" + listPallet + "}";
    }
}
