package com.mycompany.myapp.service.dto.smart;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class VehicleDto {

    @NotBlank
    @JsonProperty("vehicleNo")
    private String vehicleNo;

    @NotNull
    private List<LineDto> lines;

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public List<LineDto> getLines() {
        return lines;
    }

    public void setLines(List<LineDto> lines) {
        this.lines = lines;
    }
}
