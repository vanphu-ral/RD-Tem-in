package com.mycompany.renderQr.domain;

public interface InfoTemDetailResponse {
    Long getId();
    Long getProductOfRequestId();
    String getReelId();
    String getSapCode();
    String getProductName();
    String getPartNumber();
    String getLot();
    Integer getInitialQuantity();
    String getVendor();
    String getUserData1();
    String getUserData2();
    String getUserData3();
    String getUserData4();
    String getUserData5();
    String getStorageUnit();
    java.time.LocalDate getExpirationDate();
    java.time.LocalDate getManufacturingDate();
    java.time.LocalDate getArrivalDate();
    String getQrCode();
}
