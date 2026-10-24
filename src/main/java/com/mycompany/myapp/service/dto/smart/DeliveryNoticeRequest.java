package com.mycompany.myapp.service.dto.smart;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class DeliveryNoticeRequest {

    @JsonProperty("Type")
    private String type;

    @NotBlank
    @JsonProperty("noticeNo")
    private String noticeNo;

    @NotNull
    @JsonProperty("noticeDate")
    private java.time.LocalDate noticeDate;

    @NotBlank
    @JsonProperty("actionType")
    private String actionType; // CREATE, UPDATE, DELETE

    @NotBlank
    @JsonProperty("supplierCode")
    private String supplierCode;

    @NotBlank
    @JsonProperty("supplierName")
    private String supplierName;

    @NotBlank
    @JsonProperty("factory")
    private String factory;

    @NotNull
    @JsonProperty("dateArrivalRangDong")
    private java.time.Instant dateArrivalRangDong;

    @NotNull
    private List<VehicleDto> vehicles;

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNoticeNo() {
        return noticeNo;
    }

    public void setNoticeNo(String noticeNo) {
        this.noticeNo = noticeNo;
    }

    public java.time.LocalDate getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(java.time.LocalDate noticeDate) {
        this.noticeDate = noticeDate;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public java.time.Instant getDateArrivalRangDong() {
        return dateArrivalRangDong;
    }

    public void setDateArrivalRangDong(java.time.Instant dateArrivalRangDong) {
        this.dateArrivalRangDong = dateArrivalRangDong;
    }

    public List<VehicleDto> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<VehicleDto> vehicles) {
        this.vehicles = vehicles;
    }
}
