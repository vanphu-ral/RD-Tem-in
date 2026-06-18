package com.mycompany.myapp.domain;

import com.mycompany.panacimmc.domain.WarehouseSummaryStatsResponse;

public class WarehouseSummaryStatsDto implements WarehouseSummaryStatsResponse {

    private Integer warehouseCount;
    private Integer locationCount;
    private Long availableQuantity;
    private Integer materialTypeCount;
    private Long unavailableQuantity;

    public WarehouseSummaryStatsDto() {}

    public WarehouseSummaryStatsDto(
        Integer warehouseCount,
        Integer locationCount,
        Long availableQuantity,
        Integer materialTypeCount,
        Long unavailableQuantity
    ) {
        this.warehouseCount = warehouseCount;
        this.locationCount = locationCount;
        this.availableQuantity = availableQuantity;
        this.materialTypeCount = materialTypeCount;
        this.unavailableQuantity = unavailableQuantity;
    }

    @Override
    public Integer getWarehouseCount() {
        return warehouseCount;
    }

    public void setWarehouseCount(Integer warehouseCount) {
        this.warehouseCount = warehouseCount;
    }

    @Override
    public Integer getLocationCount() {
        return locationCount;
    }

    public void setLocationCount(Integer locationCount) {
        this.locationCount = locationCount;
    }

    @Override
    public Long getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Long availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    @Override
    public Integer getMaterialTypeCount() {
        return materialTypeCount;
    }

    public void setMaterialTypeCount(Integer materialTypeCount) {
        this.materialTypeCount = materialTypeCount;
    }

    @Override
    public Long getUnavailableQuantity() {
        return unavailableQuantity;
    }

    public void setUnavailableQuantity(Long unavailableQuantity) {
        this.unavailableQuantity = unavailableQuantity;
    }
}
