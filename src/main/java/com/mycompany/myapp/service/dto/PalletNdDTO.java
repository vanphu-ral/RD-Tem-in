package com.mycompany.myapp.service.dto;

import java.io.Serializable;

/**
 * DTO for pallet ND data sent from frontend to backend for PDF generation.
 * Matches the fields required by pallet_A4_ND.jasper template.
 */
public class PalletNdDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String customerName;
    private String serialPallet;
    private String sapName;
    private String sapCode;
    private String poNumber;
    private String itemNoSku;
    private String dateCode;
    private String soQdsx;
    private String productionDate;
    private String inspectorName;
    private String inspectionResult;
    private String branch;
    private String groupName;
    private Integer quantityPerBox;
    private Integer numBoxActual;
    private Integer totalQuantity;
    private Integer thuTuGiaPallet;
    private String note;
    private String serialBox;
    private Integer qty;
    private String date;
    private Boolean printStatus;
    private String productType;
    private String version;
    private String workOrderCode;
    private String lotNumber;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSerialPallet() {
        return serialPallet;
    }

    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

    public String getSapName() {
        return sapName;
    }

    public void setSapName(String sapName) {
        this.sapName = sapName;
    }

    public String getSapCode() {
        return sapCode;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getItemNoSku() {
        return itemNoSku;
    }

    public void setItemNoSku(String itemNoSku) {
        this.itemNoSku = itemNoSku;
    }

    public String getDateCode() {
        return dateCode;
    }

    public void setDateCode(String dateCode) {
        this.dateCode = dateCode;
    }

    public String getSoQdsx() {
        return soQdsx;
    }

    public void setSoQdsx(String soQdsx) {
        this.soQdsx = soQdsx;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    public String getInspectorName() {
        return inspectorName;
    }

    public void setInspectorName(String inspectorName) {
        this.inspectorName = inspectorName;
    }

    public String getInspectionResult() {
        return inspectionResult;
    }

    public void setInspectionResult(String inspectionResult) {
        this.inspectionResult = inspectionResult;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getQuantityPerBox() {
        return quantityPerBox;
    }

    public void setQuantityPerBox(Integer quantityPerBox) {
        this.quantityPerBox = quantityPerBox;
    }

    public Integer getNumBoxActual() {
        return numBoxActual;
    }

    public void setNumBoxActual(Integer numBoxActual) {
        this.numBoxActual = numBoxActual;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getThuTuGiaPallet() {
        return thuTuGiaPallet;
    }

    public void setThuTuGiaPallet(Integer thuTuGiaPallet) {
        this.thuTuGiaPallet = thuTuGiaPallet;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSerialBox() {
        return serialBox;
    }

    public void setSerialBox(String serialBox) {
        this.serialBox = serialBox;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(Boolean printStatus) {
        this.printStatus = printStatus;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWorkOrderCode() {
        return workOrderCode;
    }

    public void setWorkOrderCode(String workOrderCode) {
        this.workOrderCode = workOrderCode;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }
}
