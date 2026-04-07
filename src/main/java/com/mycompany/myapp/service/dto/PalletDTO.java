package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PalletDTO {

    @JsonProperty("serial_pallet")
    private String serialPallet;

    @JsonProperty("quantity_per_box")
    private Integer quantityPerBox;

    @JsonProperty("num_box_per_pallet")
    private Integer numBoxPerPallet;

    @JsonProperty("total_quantity")
    private Integer totalQuantity;

    @JsonProperty("po_number")
    private String poNumber;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("production_decision_number")
    private String productionDecisionNumber;

    @JsonProperty("item_no_sku")
    private String itemNoSku;

    @JsonProperty("date_code")
    private String dateCode;

    private String note;

    @JsonProperty("production_date")
    private String productionDate;

    @JsonProperty("list_box")
    private List<BoxDTO> listBox;

    // getters and setters

    public String getSerialPallet() {
        return serialPallet;
    }

    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

    public Integer getQuantityPerBox() {
        return quantityPerBox;
    }

    public void setQuantityPerBox(Integer quantityPerBox) {
        this.quantityPerBox = quantityPerBox;
    }

    public Integer getNumBoxPerPallet() {
        return numBoxPerPallet;
    }

    public void setNumBoxPerPallet(Integer numBoxPerPallet) {
        this.numBoxPerPallet = numBoxPerPallet;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductionDecisionNumber() {
        return productionDecisionNumber;
    }

    public void setProductionDecisionNumber(String productionDecisionNumber) {
        this.productionDecisionNumber = productionDecisionNumber;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    public List<BoxDTO> getListBox() {
        return listBox;
    }

    public void setListBox(List<BoxDTO> listBox) {
        this.listBox = listBox;
    }
}
