package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class WhsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String whsCode;

    private String whsName;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String address2;

    private String uWhskeeper;

    public String getWhsCode() {
        return whsCode;
    }

    public void setWhsCode(String whsCode) {
        this.whsCode = whsCode;
    }

    public String getWhsName() {
        return whsName;
    }

    public void setWhsName(String whsName) {
        this.whsName = whsName;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getUWhskeeper() {
        return uWhskeeper;
    }

    public void setUWhskeeper(String uWhskeeper) {
        this.uWhskeeper = uWhskeeper;
    }
}
