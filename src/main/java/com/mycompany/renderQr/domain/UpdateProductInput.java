package com.mycompany.renderQr.domain;

public class UpdateProductInput {

    private Long id;
    private String sapCode;
    private String partNumber;
    private Long requestCreateTemId;
    private String lot;
    private Integer temQuantity;
    private Long initialQuantity;
    private String vendor;
    private String userData1;
    private String userData2;
    private String userData3;
    private String userData4;
    private String userData5;
    private String storageUnit;
    private String expirationDate;
    private String manufacturingDate;
    private String arrivalDate;
    private Integer numberOfPrints;

    // ✅ Getters và setters đầy đủ
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSapCode() {
        return sapCode;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public Long getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(Long initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getUserData1() {
        return userData1;
    }

    public void setUserData1(String userData1) {
        this.userData1 = userData1;
    }

    public String getUserData2() {
        return userData2;
    }

    public void setUserData2(String userData2) {
        this.userData2 = userData2;
    }

    public String getUserData3() {
        return userData3;
    }

    public void setUserData3(String userData3) {
        this.userData3 = userData3;
    }

    public String getUserData4() {
        return userData4;
    }

    public void setUserData4(String userData4) {
        this.userData4 = userData4;
    }

    public String getUserData5() {
        return userData5;
    }

    public void setUserData5(String userData5) {
        this.userData5 = userData5;
    }

    public String getStorageUnit() {
        return storageUnit;
    }

    public void setStorageUnit(String storageUnit) {
        this.storageUnit = storageUnit;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Integer getNumberOfPrints() {
        return numberOfPrints;
    }

    public void setNumberOfPrints(Integer numberOfPrints) {
        this.numberOfPrints = numberOfPrints;
    }

    public Long getRequestCreateTemId() {
        return requestCreateTemId;
    }

    public void setRequestCreateTemId(Long requestCreateTemId) {
        this.requestCreateTemId = requestCreateTemId;
    }

    public Integer getTemQuantity() {
        return temQuantity;
    }

    public void setTemQuantity(Integer temQuantity) {
        this.temQuantity = temQuantity;
    }
}
