package com.mycompany.panacimmc.domain;

public interface InventoryResponse {
    Long getInventoryId();
    String getPartId();
    String getPartNumber();
    String getTrackingType();
    Long getMaterialTraceId();
    Integer getQuantity();
    Long getLocationId();
    Long getParentLocationId();
    Long getLastLocationId();
    String getMaterialControl();
    String getMaterialIdentifier();
    String getStatus();
    String getReservationReference();
    String getExpirationDate();
    String getReceivedDate();
    Long getUomId();
    String getUomName();
    String getUpdatedDate();
    String getUpdatedBy();
    String getLabelingStatus();
    String getPrinter();
    String getSplicedMaterialIdentifier();
    Long getSplicedInventoryId();
    Long getCarrierId();
    String getCarrierNumber();
    Integer getReservedQuantity();
    String getCalculatedStatus();
    Integer getInitialQuantity();
    Integer getAvailableQuantity();
    Integer getConsumedQuantity();
    Integer getScrappedQuantity();
    Long getParentInventoryId();
    String getParentMaterialIdentifier();
    String getMaterialName();
    String getPuLocation();
    Integer getLifetimeCount();
    String getBulkBarcode();
    Boolean getIsBulk();
    String getManufacturingDate();
    String getPartClass();
    String getMaterialType();
    String getCheckinDate();
    Integer getUsageCount();
    Long getPartAlternateNumbersId();
    String getReelNumber();
    Long getMainReelId();
    String getReasonCode();
    String getLastCarrierNumber();

    String getRankAp();
    String getRankQuang();
    String getRankMau();

    // Các field từ bảng liên kết
    String getLocationName();
    String getUserData4();
    String getUserData5();
    String getLotNumber();
    String getRecordCount();
}
