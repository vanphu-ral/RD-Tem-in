package com.mycompany.panacimmc.domain;

public interface WarehouseAreaInventoryItemRawResponse {
    Long getLocationId();
    String getLocationName();
    String getLocationFullName();
    String getMaterialIdentifier();
    String getItemCode();
    String getMaterialName();
    Integer getQuantity();
    String getStatus();
    String getLotNumber();
    String getPartNumber();
}
