package com.mycompany.myapp.service.dto;

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

    private String calculatedStatus;
    private String trackingType;
    private String updatedBy;
    private String manufacturingDate;
    private String materialType;
    private String checkinDate;
    private String receivedDate;
    private String rankAp;
    private String rankQuang;
    private String rankMau;
    /** Maps UI column itemName / Inventroy_MaterialName */
    private String materialName;

    /** contains (default) | equals */
    private String materialIdentifierMode;
    private String statusMode;
    private String partNumberMode;
    private String quantityMode;
    private String availableQuantityMode;
    private String lotNumberMode;
    private String userData4Mode;
    private String userData5Mode;
    private String locationNameMode;
    private String expirationDateMode;
    private String updatedDateMode;
    private String calculatedStatusMode;
    private String trackingTypeMode;
    private String updatedByMode;
    private String manufacturingDateMode;
    private String materialTypeMode;
    private String checkinDateMode;
    private String receivedDateMode;
    private String rankApMode;
    private String rankQuangMode;
    private String rankMauMode;
    private String materialNameMode;

    public InventoryRequestDTO() {}

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

    public String getCalculatedStatus() {
        return calculatedStatus;
    }

    public void setCalculatedStatus(String calculatedStatus) {
        this.calculatedStatus = calculatedStatus;
    }

    public String getTrackingType() {
        return trackingType;
    }

    public void setTrackingType(String trackingType) {
        this.trackingType = trackingType;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(String checkinDate) {
        this.checkinDate = checkinDate;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getRankAp() {
        return rankAp;
    }

    public void setRankAp(String rankAp) {
        this.rankAp = rankAp;
    }

    public String getRankQuang() {
        return rankQuang;
    }

    public void setRankQuang(String rankQuang) {
        this.rankQuang = rankQuang;
    }

    public String getRankMau() {
        return rankMau;
    }

    public void setRankMau(String rankMau) {
        this.rankMau = rankMau;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialIdentifierMode() {
        return materialIdentifierMode;
    }

    public void setMaterialIdentifierMode(String materialIdentifierMode) {
        this.materialIdentifierMode = materialIdentifierMode;
    }

    public String getStatusMode() {
        return statusMode;
    }

    public void setStatusMode(String statusMode) {
        this.statusMode = statusMode;
    }

    public String getPartNumberMode() {
        return partNumberMode;
    }

    public void setPartNumberMode(String partNumberMode) {
        this.partNumberMode = partNumberMode;
    }

    public String getQuantityMode() {
        return quantityMode;
    }

    public void setQuantityMode(String quantityMode) {
        this.quantityMode = quantityMode;
    }

    public String getAvailableQuantityMode() {
        return availableQuantityMode;
    }

    public void setAvailableQuantityMode(String availableQuantityMode) {
        this.availableQuantityMode = availableQuantityMode;
    }

    public String getLotNumberMode() {
        return lotNumberMode;
    }

    public void setLotNumberMode(String lotNumberMode) {
        this.lotNumberMode = lotNumberMode;
    }

    public String getUserData4Mode() {
        return userData4Mode;
    }

    public void setUserData4Mode(String userData4Mode) {
        this.userData4Mode = userData4Mode;
    }

    public String getUserData5Mode() {
        return userData5Mode;
    }

    public void setUserData5Mode(String userData5Mode) {
        this.userData5Mode = userData5Mode;
    }

    public String getLocationNameMode() {
        return locationNameMode;
    }

    public void setLocationNameMode(String locationNameMode) {
        this.locationNameMode = locationNameMode;
    }

    public String getExpirationDateMode() {
        return expirationDateMode;
    }

    public void setExpirationDateMode(String expirationDateMode) {
        this.expirationDateMode = expirationDateMode;
    }

    public String getUpdatedDateMode() {
        return updatedDateMode;
    }

    public void setUpdatedDateMode(String updatedDateMode) {
        this.updatedDateMode = updatedDateMode;
    }

    public String getCalculatedStatusMode() {
        return calculatedStatusMode;
    }

    public void setCalculatedStatusMode(String calculatedStatusMode) {
        this.calculatedStatusMode = calculatedStatusMode;
    }

    public String getTrackingTypeMode() {
        return trackingTypeMode;
    }

    public void setTrackingTypeMode(String trackingTypeMode) {
        this.trackingTypeMode = trackingTypeMode;
    }

    public String getUpdatedByMode() {
        return updatedByMode;
    }

    public void setUpdatedByMode(String updatedByMode) {
        this.updatedByMode = updatedByMode;
    }

    public String getManufacturingDateMode() {
        return manufacturingDateMode;
    }

    public void setManufacturingDateMode(String manufacturingDateMode) {
        this.manufacturingDateMode = manufacturingDateMode;
    }

    public String getMaterialTypeMode() {
        return materialTypeMode;
    }

    public void setMaterialTypeMode(String materialTypeMode) {
        this.materialTypeMode = materialTypeMode;
    }

    public String getCheckinDateMode() {
        return checkinDateMode;
    }

    public void setCheckinDateMode(String checkinDateMode) {
        this.checkinDateMode = checkinDateMode;
    }

    public String getReceivedDateMode() {
        return receivedDateMode;
    }

    public void setReceivedDateMode(String receivedDateMode) {
        this.receivedDateMode = receivedDateMode;
    }

    public String getRankApMode() {
        return rankApMode;
    }

    public void setRankApMode(String rankApMode) {
        this.rankApMode = rankApMode;
    }

    public String getRankQuangMode() {
        return rankQuangMode;
    }

    public void setRankQuangMode(String rankQuangMode) {
        this.rankQuangMode = rankQuangMode;
    }

    public String getRankMauMode() {
        return rankMauMode;
    }

    public void setRankMauMode(String rankMauMode) {
        this.rankMauMode = rankMauMode;
    }

    public String getMaterialNameMode() {
        return materialNameMode;
    }

    public void setMaterialNameMode(String materialNameMode) {
        this.materialNameMode = materialNameMode;
    }
}
