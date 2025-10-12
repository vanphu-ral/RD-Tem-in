package com.mycompany.renderQr.domain;

import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table(name = "info_tem_detail")
public class InfoTemDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Product_of_request_id")
    private Long productOfRequestId;

    @Column(name = "ReelID")
    private String reelID;

    @Column(name = "SAPCode")
    private String sapCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "PartNumber")
    private String partNumber;

    @Column(name = "LOT")
    private String lot;

    @Column(name = "InitialQuantity")
    private Integer initialQuantity;

    @Column(name = "Vendor")
    private String vendor;

    @Column(name = "UserData1")
    private String userData1;

    @Column(name = "UserData2")
    private String userData2;

    @Column(name = "UserData3")
    private String userData3;

    @Column(name = "UserData4")
    private String userData4;

    @Column(name = "UserData5")
    private String userData5;

    @Column(name = "StorageUnit")
    private String storageUnit;

    @Column(name = "ExpirationDate")
    private LocalDate expirationDate;

    @Column(name = "ManufacturingDate")
    private LocalDate manufacturingDate;

    @Column(name = "Arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "QR_code")
    private String qrCode;

    @Column(name = "sl_tem_quantity")
    private Integer slTemQuantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductOfRequestId() {
        return productOfRequestId;
    }

    public void setProductOfRequestId(Long productOfRequestId) {
        this.productOfRequestId = productOfRequestId;
    }

    public String getReelID() {
        return reelID;
    }

    public void setReelID(String reelID) {
        this.reelID = reelID;
    }

    public String getSapCode() {
        return sapCode;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public Integer getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(Integer initialQuantity) {
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

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(LocalDate manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Integer getSlTemQuantity() {
        return slTemQuantity;
    }

    public void setSlTemQuantity(Integer slTemQuantity) {
        this.slTemQuantity = slTemQuantity;
    }
}
