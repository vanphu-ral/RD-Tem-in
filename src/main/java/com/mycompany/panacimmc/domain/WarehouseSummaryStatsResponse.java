package com.mycompany.panacimmc.domain;

public interface WarehouseSummaryStatsResponse {
    Integer getWarehouseCount();
    Integer getLocationCount();
    Integer getEmptyLocationCount();
    Long getAvailableQuantity();
    Integer getMaterialTypeCount();
    Long getUnavailableQuantity();
}
