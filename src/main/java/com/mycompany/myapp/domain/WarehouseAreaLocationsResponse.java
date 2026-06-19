package com.mycompany.myapp.domain;

import java.util.ArrayList;
import java.util.List;

public class WarehouseAreaLocationsResponse {

    private String areaCode;
    private String areaName;
    private String selectedMaterialType;
    private List<String> legendMaterialTypes = new ArrayList<>();
    private List<WarehouseAreaLocationDto> locations = new ArrayList<>();
    private int totalCount;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getSelectedMaterialType() {
        return selectedMaterialType;
    }

    public void setSelectedMaterialType(String selectedMaterialType) {
        this.selectedMaterialType = selectedMaterialType;
    }

    public List<String> getLegendMaterialTypes() {
        return legendMaterialTypes;
    }

    public void setLegendMaterialTypes(List<String> legendMaterialTypes) {
        this.legendMaterialTypes = legendMaterialTypes;
    }

    public List<WarehouseAreaLocationDto> getLocations() {
        return locations;
    }

    public void setLocations(List<WarehouseAreaLocationDto> locations) {
        this.locations = locations;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
