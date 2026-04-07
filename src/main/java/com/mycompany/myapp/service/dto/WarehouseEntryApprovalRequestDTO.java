package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class WarehouseEntryApprovalRequestDTO {

    @JsonProperty("general_info")
    private GeneralInfoDTO generalInfo;

    @JsonProperty("list_pallet")
    private List<PalletDTO> listPallet;

    // getters and setters

    public GeneralInfoDTO getGeneralInfo() {
        return generalInfo;
    }

    public void setGeneralInfo(GeneralInfoDTO generalInfo) {
        this.generalInfo = generalInfo;
    }

    public List<PalletDTO> getListPallet() {
        return listPallet;
    }

    public void setListPallet(List<PalletDTO> listPallet) {
        this.listPallet = listPallet;
    }
}
