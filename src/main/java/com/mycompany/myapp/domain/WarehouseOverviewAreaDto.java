package com.mycompany.myapp.domain;

import java.util.ArrayList;
import java.util.List;

public class WarehouseOverviewAreaDto {

    private String areaCode;
    private String areaName;
    private Integer quantity;
    private Integer materialIdentifierCount;
    private Integer materialTypeCount;
    private List<String> materialTypes = new ArrayList<>();

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getMaterialIdentifierCount() {
        return materialIdentifierCount;
    }

    public void setMaterialIdentifierCount(Integer materialIdentifierCount) {
        this.materialIdentifierCount = materialIdentifierCount;
    }

    public Integer getMaterialTypeCount() {
        return materialTypeCount;
    }

    public void setMaterialTypeCount(Integer materialTypeCount) {
        this.materialTypeCount = materialTypeCount;
    }

    public List<String> getMaterialTypes() {
        return materialTypes;
    }

    public void setMaterialTypes(List<String> materialTypes) {
        this.materialTypes = materialTypes;
    }
}
