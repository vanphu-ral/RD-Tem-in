package com.mycompany.panacimmc.domain;

public interface WarehouseLocationInventoryRawResponse {
    String getMaterialIdentifier();
    String getItemCode();
    String getPartNumber();
    String getLocationFullName();
    Integer getQuantity();
    String getStatus();
    String getLotNumber();
}
