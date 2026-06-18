package com.mycompany.panacimmc.domain;

public interface WarehouseSummaryStatsCombined {
    Integer getWarehouseCount();
    Integer getLocationCount();
    Long getAvailableQuantity();
    Long getUnavailableQuantity();
}
