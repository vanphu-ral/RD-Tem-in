package com.mycompany.panacimmc.domain;

public interface WarehouseAreaLocationRawResponse {
    Long getLocationId();
    String getLocationName();
    String getLocationFullName();
    Double getXPos();
    Double getYPos();
    Integer getProductLimit();
    String getItemCode();
    Integer getQuantity();
    Integer getMaterialIdentifierCount();
}
