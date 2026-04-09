package com.mycompany.myapp.service.dto;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * DTO for updating wms_send_status for pallet information details.
 */
public class UpdateWmsSendStatusRequest {

    @NotEmpty
    private List<String> serialPallets;

    @NotNull
    private Boolean wmsSendStatus;

    private String updatedBy;

    public UpdateWmsSendStatusRequest() {}

    public UpdateWmsSendStatusRequest(
        List<String> serialPallets,
        Boolean wmsSendStatus,
        String updatedBy
    ) {
        this.serialPallets = serialPallets;
        this.wmsSendStatus = wmsSendStatus;
        this.updatedBy = updatedBy;
    }

    public List<String> getSerialPallets() {
        return serialPallets;
    }

    public void setSerialPallets(List<String> serialPallets) {
        this.serialPallets = serialPallets;
    }

    public Boolean getWmsSendStatus() {
        return wmsSendStatus;
    }

    public void setWmsSendStatus(Boolean wmsSendStatus) {
        this.wmsSendStatus = wmsSendStatus;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
