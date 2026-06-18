package com.mycompany.myapp.domain;

import com.mycompany.panacimmc.domain.WarehouseAreaSummaryResponse;

public class WarehouseAreaSummaryRowDto
    implements WarehouseAreaSummaryResponse {

    private String areaCode;
    private String areaName;
    private String materialType;
    private Integer quantity;
    private Integer materialIdentifierCount;

    public WarehouseAreaSummaryRowDto() {}

    public WarehouseAreaSummaryRowDto(
        String areaCode,
        String areaName,
        String materialType,
        Integer quantity,
        Integer materialIdentifierCount
    ) {
        this.areaCode = areaCode;
        this.areaName = areaName;
        this.materialType = materialType;
        this.quantity = quantity;
        this.materialIdentifierCount = materialIdentifierCount;
    }

    @Override
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Override
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @Override
    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    @Override
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public Integer getMaterialIdentifierCount() {
        return materialIdentifierCount;
    }

    public void setMaterialIdentifierCount(Integer materialIdentifierCount) {
        this.materialIdentifierCount = materialIdentifierCount;
    }
}
