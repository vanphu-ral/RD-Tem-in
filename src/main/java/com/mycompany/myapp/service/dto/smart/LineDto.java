package com.mycompany.myapp.service.dto.smart;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LineDto {

    @NotNull
    @JsonProperty("lineId")
    private UUID lineId;

    @NotBlank
    @JsonProperty("deliveryId")
    private String deliveryId;

    @NotBlank
    @JsonProperty("poSmart")
    private String poSmart;

    @NotBlank
    @JsonProperty("poErp")
    private String poErp;

    @NotBlank
    @JsonProperty("prNumber")
    private String prNumber;

    @JsonProperty("contractNo")
    private String contractNo;

    @JsonProperty("lineItemSTT")
    private String lineItemSTT;

    @JsonProperty("poLineItem")
    private String poLineItem;

    @NotBlank
    @JsonProperty("itemCode")
    private String itemCode;

    @JsonProperty("itemName")
    private String itemName;

    @JsonProperty("whsCode")
    private String whsCode;

    @JsonProperty("unit")
    private String unit;

    @NotNull
    @JsonProperty("quantity")
    private BigDecimal quantity;

    @JsonProperty("invoiceSmartNo")
    private String invoiceSmartNo;

    @JsonProperty("invoiceNo")
    private String invoiceNo;

    @JsonProperty("invoiceDate")
    private LocalDate invoiceDate;

    @JsonProperty("deliveryTurn")
    private Integer deliveryTurn;

    // Getters and Setters
    public UUID getLineId() {
        return lineId;
    }

    public void setLineId(UUID lineId) {
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
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
}
