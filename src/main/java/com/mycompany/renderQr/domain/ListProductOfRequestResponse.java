package com.mycompany.renderQr.domain;

import java.time.LocalDateTime;

public interface ListProductOfRequestResponse {
    Long getId();
    Long getRequestCreateTemId();
    String getSapCode();
    Integer getTemQuantity();
    String getPartNumber();
    String getLot();
    Long getInitialQuantity();
    String getVendor();
    String getUserData1();
    String getUserData2();
    String getUserData3();
    String getUserData4();
    String getUserData5();
    String getStorageUnit();
    LocalDateTime getExpirationDate();
    LocalDateTime getManufacturingDate();
    LocalDateTime getArrivalDate();
    Integer getNumberOfPrints();
}
