package com.mycompany.panacimmc.domain;

public interface WarehouseSummaryStatsResponse {
    Integer getWarehouseCount();
    Integer getLocationCount();
    Long getAvailableQuantity();
    Integer getMaterialTypeCount();
    Long getUnavailableQuantity();
}
