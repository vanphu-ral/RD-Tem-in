package com.mycompany.myapp.domain;

public interface ProfileCheckResponse {
    Integer getProfileId();
    Integer getProductId();
    String getCheckName();
    String getCheckValue();
    String getCheckStatus();
    Integer getPosition();
    Integer getVersionId();
    Integer getMachineId();
    String getMachineName();
    String getVersion();
}
