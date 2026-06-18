package com.mycompany.panacimmc.domain;

public interface WarehouseSummaryStatsCombined {
    Integer getWarehouseCount();
    Integer getLocationCount();
    Integer getEmptyLocationCount();
    Long getAvailableQuantity();
    Long getUnavailableQuantity();
}
