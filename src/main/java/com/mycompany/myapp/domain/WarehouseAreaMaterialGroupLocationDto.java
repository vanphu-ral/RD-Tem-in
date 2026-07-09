package com.mycompany.myapp.domain;

public class WarehouseAreaMaterialGroupLocationDto {

    private Long locationId;
    private String locationName;
    private Integer quantity;
    private Integer materialIdentifierCount;

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getMaterialIdentifierCount() {
        return materialIdentifierCount;
    }

    public void setMaterialIdentifierCount(Integer materialIdentifierCount) {
        this.materialIdentifierCount = materialIdentifierCount;
    }
}
