package com.mycompany.myapp.domain;

import java.util.ArrayList;
import java.util.List;

public class WarehouseAreaMaterialGroupDto {

    private String groupKey;
    private String itemCode;
    private String materialName;
    private String materialType;
    private Integer totalQuantity;
    private Integer materialIdentifierCount;
    private List<WarehouseAreaMaterialGroupLocationDto> locationSummaries =
        new ArrayList<>();

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getMaterialIdentifierCount() {
        return materialIdentifierCount;
    }

    public void setMaterialIdentifierCount(Integer materialIdentifierCount) {
        this.materialIdentifierCount = materialIdentifierCount;
    }

    public List<WarehouseAreaMaterialGroupLocationDto> getLocationSummaries() {
        return locationSummaries;
    }

    public void setLocationSummaries(
        List<WarehouseAreaMaterialGroupLocationDto> locationSummaries
    ) {
        this.locationSummaries = locationSummaries;
    }
}
