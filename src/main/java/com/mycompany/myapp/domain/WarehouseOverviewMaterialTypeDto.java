package com.mycompany.myapp.domain;

public class WarehouseOverviewMaterialTypeDto {

    private String materialType;
    private Integer warehouseCount;
    private Integer quantity;
    private Integer materialIdentifierCount;

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public Integer getWarehouseCount() {
        return warehouseCount;
    }

    public void setWarehouseCount(Integer warehouseCount) {
        this.warehouseCount = warehouseCount;
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
}
