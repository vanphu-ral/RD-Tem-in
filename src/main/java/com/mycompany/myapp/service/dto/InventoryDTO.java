package com.mycompany.myapp.service.dto;

public class InventoryDTO {
    private String materialIdentifier;
    private Long inventoryId;
    private String expirationDate;
    private Integer quantity;
    private Integer quantityChange;
    private String expiredDate;
    private Long locationId;
    private String status;
    private String updatedBy;
    private String updatedDate;
    private Boolean type;

    public InventoryDTO() {
    }

    public InventoryDTO(String materialIdentifier, Long inventoryId, String expirationDate, Integer quantity, Integer quantityChange, String expiredDate, Long locationId, String status, String updatedBy, String updatedDate, Boolean type) {
        this.materialIdentifier = materialIdentifier;
        this.inventoryId = inventoryId;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
        this.quantityChange = quantityChange;
        this.expiredDate = expiredDate;
        this.locationId = locationId;
        this.status = status;
        this.updatedBy = updatedBy;
        this.updatedDate = updatedDate;
        this.type = type;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getMaterialIdentifier() {
        return materialIdentifier;
    }

    public void setMaterialIdentifier(String materialIdentifier) {
        this.materialIdentifier = materialIdentifier;
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }



    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }
}
