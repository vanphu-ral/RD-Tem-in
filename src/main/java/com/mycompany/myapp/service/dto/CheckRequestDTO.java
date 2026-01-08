package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * A DTO for the check request.
 */
public class CheckRequestDTO implements Serializable {

    @Size(max = 50)
    @NotNull
    @JsonProperty("work_order_code")
    private String workOrderCode;

    @Size(max = 50)
    @NotNull
    @JsonProperty("pallet_code")
    private String palletCode;

    @Size(max = 50)
    @NotNull
    @JsonProperty("box_code")
    private String boxCode;

    @JsonProperty("updated_by")
    private String updatedBy;

    public String getWorkOrderCode() {
        return workOrderCode;
    }

    public void setWorkOrderCode(String workOrderCode) {
        this.workOrderCode = workOrderCode;
    }

    public String getPalletCode() {
        return palletCode;
    }

    public void setPalletCode(String palletCode) {
        this.palletCode = palletCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return (
            "CheckRequestDTO{" +
            "workOrderCode='" +
            workOrderCode +
            '\'' +
            ", palletCode='" +
            palletCode +
            '\'' +
            ", boxCode='" +
            boxCode +
            '\'' +
            ", updatedBy='" +
            updatedBy +
            '\'' +
            '}'
        );
    }
}
