package com.mycompany.renderQr.domain;

import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
@Table(name = "list_product_of_request")
public class ListProductOfRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Request_create_tem_id", nullable = false)
    private Long requestCreateTemId;

    @Column(name = "product_name", length = 255)
    private String productName;

    @Column(name = "SAPCode", nullable = false, length = 8)
    private String sapCode;

    @Column(name = "Tem_quantity", nullable = false)
    private Integer temQuantity;

    @Column(name = "PartNumber", nullable = false, length = 100)
    private String partNumber;

    @Column(name = "LOT", nullable = false, length = 100)
    private String lot;

    @Column(name = "InitialQuantity", nullable = false)
    private Long initialQuantity;

    @Column(name = "Vendor", nullable = false, length = 100)
    private String vendor;

    @Column(name = "vendor_name", nullable = false, length = 255)
    private String vendorName;

    @Column(name = "UserData1", nullable = false, length = 100)
    private String userData1;

    @Column(name = "UserData2", nullable = false, length = 100)
    private String userData2;

    @Column(name = "UserData3", nullable = false, length = 100)
    private String userData3;

    @Column(name = "UserData4", nullable = false, length = 100)
    private String userData4;

    @Column(name = "UserData5", nullable = false, length = 100)
    private String userData5;

    @Column(name = "StorageUnit", length = 100)
    private String storageUnit;

    @Column(name = "ExpirationDate", nullable = false)
    private LocalDateTime expirationDate;

    @Column(name = "ManufacturingDate", nullable = false)
    private LocalDateTime manufacturingDate;

    @Column(name = "arrival_date", nullable = false)
    private LocalDateTime arrivalDate;

    @Column(name = "number_of_prints", nullable = false)
    private Integer numberOfPrints;

    @Column(name = "upload_panacim", nullable = false)
    private Boolean uploadPanacim;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequestCreateTemId() {
        return requestCreateTemId;
    }

    public void setRequestCreateTemId(Long requestCreateTemId) {
        this.requestCreateTemId = requestCreateTemId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSapCode() {
        return sapCode;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public Integer getTemQuantity() {
        return temQuantity;
    }

    public void setTemQuantity(Integer temQuantity) {
        this.temQuantity = temQuantity;
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

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalDateTime getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(LocalDateTime manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Integer getNumberOfPrints() {
        return numberOfPrints;
    }

    public void setNumberOfPrints(Integer numberOfPrints) {
        this.numberOfPrints = numberOfPrints;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public Boolean getUploadPanacim() {
        return uploadPanacim;
    }

    public void setUploadPanacim(Boolean uploadPanacim) {
        this.uploadPanacim = uploadPanacim;
    }
}
