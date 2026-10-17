package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class DeliveryDetailDTO implements Serializable {

    private UUID deliveryId;
    private String type;
    private String noticeNo;
    private LocalDate noticeDate;
    private String supplierCode;
    private String supplierName;
    private String factory;
    private ZonedDateTime dateArrivalRangDong;
    private String vehicleNo;
    private Integer numberOfPo;
    private Integer numberOfItem;
    private String source;
    private ZonedDateTime createdAt;
    private String createdBy;
    private ZonedDateTime deletedAt;
    private String deletedBy;
    private List<DetailDeliveryWithLabelsDTO> detailDelivery;

    public UUID getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(UUID deliveryId) {
        this.deliveryId = deliveryId;
    }

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

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public Integer getNumberOfPo() {
        return numberOfPo;
    }

    public void setNumberOfPo(Integer numberOfPo) {
        this.numberOfPo = numberOfPo;
    }

    public Integer getNumberOfItem() {
        return numberOfItem;
    }

    public void setNumberOfItem(Integer numberOfItem) {
        this.numberOfItem = numberOfItem;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(ZonedDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public List<DetailDeliveryWithLabelsDTO> getDetailDelivery() {
        return detailDelivery;
    }

    public void setDetailDelivery(
        List<DetailDeliveryWithLabelsDTO> detailDelivery
    ) {
        this.detailDelivery = detailDelivery;
    }

    public static class DetailDeliveryWithLabelsDTO implements Serializable {

        private UUID lineId;
        private UUID deliveryId;
        private String poSmart;
        private String poErp;
        private String prNumber;
        private String contractNo;
        private String lineItemStt;
        private String poLineItem;
        private String itemCode;
        private String itemName;
        private String whsCode;
        private String unit;
        private Integer quantity;
        private String invoiceSmartNo;
        private String invoiceNo;
        private LocalDate invoiceDate;
        private Integer deliveryTurn;
        private List<VendorLabelInfoDTO> vendorLabelInfoList;

        public UUID getLineId() {
            return lineId;
        }

        public void setLineId(UUID lineId) {
            this.lineId = lineId;
        }

        public UUID getDeliveryId() {
            return deliveryId;
        }

        public void setDeliveryId(UUID deliveryId) {
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

        public String getLineItemStt() {
            return lineItemStt;
        }

        public void setLineItemStt(String lineItemStt) {
            this.lineItemStt = lineItemStt;
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

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
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

        public LocalDate getInvoiceDate() {
            return invoiceDate;
        }

        public void setInvoiceDate(LocalDate invoiceDate) {
            this.invoiceDate = invoiceDate;
        }

        public Integer getDeliveryTurn() {
            return deliveryTurn;
        }

        public void setDeliveryTurn(Integer deliveryTurn) {
            this.deliveryTurn = deliveryTurn;
        }

        public List<VendorLabelInfoDTO> getVendorLabelInfoList() {
            return vendorLabelInfoList;
        }

        public void setVendorLabelInfoList(
            List<VendorLabelInfoDTO> vendorLabelInfoList
        ) {
            this.vendorLabelInfoList = vendorLabelInfoList;
        }
    }
}
