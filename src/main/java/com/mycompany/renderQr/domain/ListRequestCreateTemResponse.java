package com.mycompany.renderQr.domain;

import java.time.LocalDateTime;

public interface ListRequestCreateTemResponse {
    Long getId();
    String getVendor();
    String getUserData5();
    String getCreatedBy();
    Short getNumberProduction();
    Long getTotalQuantity();
    String getStatus();
    LocalDateTime getCreatedDate();
}
