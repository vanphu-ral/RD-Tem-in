package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class DeliveryNoticeRequest {

    @JsonProperty("Type")
    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "noticeNo is required")
    private String noticeNo;

    @NotNull(message = "noticeDate is required")
    private LocalDate noticeDate;

    @NotBlank(message = "actionType is required")
    @JsonProperty("actionType")
    private String actionType;

    @NotBlank(message = "supplierCode is required")
    private String supplierCode;

    private String supplierName;

    private String factory;

    private ZonedDateTime dateArrivalRangDong;

    @NotNull(message = "vehicles list is required")
    @Valid
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

    public LocalDate getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(LocalDate noticeDate) {
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

    public ZonedDateTime getDateArrivalRangDong() {
        return dateArrivalRangDong;
    }

    public void setDateArrivalRangDong(ZonedDateTime dateArrivalRangDong) {
        this.dateArrivalRangDong = dateArrivalRangDong;
    }

    public List<VehicleDto> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<VehicleDto> vehicles) {
        this.vehicles = vehicles;
    }

    public static class VehicleDto {

        @NotBlank(message = "vehicleNo is required")
        private String vehicleNo;

        @NotNull(message = "lines list is required")
        @Valid
        private List<LineDto> lines;

        public String getVehicleNo() {
            return vehicleNo;
        }

        public void setVehicleNo(String vehicleNo) {
            this.vehicleNo = vehicleNo;
        }

        public List<LineDto> getLines() {
            return lines;
        }

        public void setLines(List<LineDto> lines) {
            this.lines = lines;
        }
    }

    public static class LineDto {

        @NotBlank(message = "lineId is required")
        private String lineId;

        private String deliveryId;
        private String poSmart;
        private String poErp;
        private String prNumber;
        private String contractNo;
        private String lineItemSTT;
        private String poLineItem;

        @NotBlank(message = "itemCode is required")
        private String itemCode;

        private String itemName;
        private String whsCode;
        private String unit;
        private Double quantity;
        private String invoiceSmartNo;
        private String invoiceNo;
        private String invoiceDate;
        private Integer deliveryTurn;

        // Getters and Setters
        public String getLineId() {
            return lineId;
        }

        public void setLineId(String lineId) {
            this.lineId = lineId;
        }

        public String getDeliveryId() {
            return deliveryId;
        }

        public void setDeliveryId(String deliveryId) {
            this.deliveryId = deliveryId;
        }

        public String getPoSmart() {
            return poSmart;
        }

        public void setPoSmart(String poSmart) {
            this.poSmart = poSmart;
        }

        public String getPoErp() {
            return poErp;
        }

        public void setPoErp(String poErp) {
            this.poErp = poErp;
        }

        public String getPrNumber() {
            return prNumber;
        }

        public void setPrNumber(String prNumber) {
            this.prNumber = prNumber;
        }

        public String getContractNo() {
            return contractNo;
        }

        public void setContractNo(String contractNo) {
            this.contractNo = contractNo;
        }

        public String getLineItemSTT() {
            return lineItemSTT;
        }

        public void setLineItemSTT(String lineItemSTT) {
            this.lineItemSTT = lineItemSTT;
        }

        public String getPoLineItem() {
            return poLineItem;
        }

        public void setPoLineItem(String poLineItem) {
            this.poLineItem = poLineItem;
        }

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getWhsCode() {
            return whsCode;
        }

        public void setWhsCode(String whsCode) {
            this.whsCode = whsCode;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public Double getQuantity() {
            return quantity;
        }

        public void setQuantity(Double quantity) {
            this.quantity = quantity;
        }

        public String getInvoiceSmartNo() {
            return invoiceSmartNo;
        }

        public void setInvoiceSmartNo(String invoiceSmartNo) {
            this.invoiceSmartNo = invoiceSmartNo;
        }

        public String getInvoiceNo() {
            return invoiceNo;
        }

        public void setInvoiceNo(String invoiceNo) {
            this.invoiceNo = invoiceNo;
        }

        public String getInvoiceDate() {
            return invoiceDate;
        }

        public void setInvoiceDate(String invoiceDate) {
            this.invoiceDate = invoiceDate;
        }

        public Integer getDeliveryTurn() {
            return deliveryTurn;
        }

        public void setDeliveryTurn(Integer deliveryTurn) {
            this.deliveryTurn = deliveryTurn;
        }
    }
}
