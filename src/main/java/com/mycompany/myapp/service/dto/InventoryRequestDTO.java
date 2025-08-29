package com.mycompany.myapp.service.dto;

import javax.persistence.Column;

public class InventoryRequestDTO {

    private String materialIdentifier;
    private String status;
    private String partNumber;
    private Integer quantity;
    private Integer availableQuantity;
    private String lotNumber;
    private String userData4;
    private String userData5;
    private String locationName;
    private String expirationDate;
    private String updatedDate;
    private Integer itemPerPage;
    private Integer pageNumber;

    public InventoryRequestDTO(
        String materialIdentifier,
        String status,
        String partNumber,
        Integer quantity,
        Integer availableQuantity,
        String lotNumber,
        String userData4,
        String userData5,
        String locationName,
        String expirationDate,
        String updatedDate,
        Integer itemPerPage,
        Integer pageNumber
    ) {
        this.materialIdentifier = materialIdentifier;
        this.status = status;
        this.partNumber = partNumber;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
        this.lotNumber = lotNumber;
        this.userData4 = userData4;
        this.userData5 = userData5;
        this.locationName = locationName;
        this.expirationDate = expirationDate;
        this.updatedDate = updatedDate;
        this.itemPerPage = itemPerPage;
        this.pageNumber = pageNumber;
    }

    public String getUserData5() {
        return userData5;
    }

    public void setUserData5(String userData5) {
        this.userData5 = userData5;
    }

    public Integer getItemPerPage() {
        return itemPerPage;
    }

    public void setItemPerPage(Integer itemPerPage) {
        this.itemPerPage = itemPerPage;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public InventoryRequestDTO() {}

    public String getMaterialIdentifier() {
        return materialIdentifier;
    }

    public void setMaterialIdentifier(String materialIdentifier) {
        this.materialIdentifier = materialIdentifier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getUserData4() {
        return userData4;
    }

    public void setUserData4(String userData4) {
        this.userData4 = userData4;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }
}
