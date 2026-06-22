package com.mycompany.panacimmc.domain;

public interface WarehouseLocationInventoryRawResponse {
    String getMaterialIdentifier();
    String getItemCode();
    String getPartNumber();
    Integer getQuantity();
    String getStatus();
    String getLotNumber();
}
