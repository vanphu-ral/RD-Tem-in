package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.DetailDelivery} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DetailDeliveryDTO implements Serializable {

    @NotNull
    private UUID lineId;

    @Size(max = 50)
    private String poSmart;

    @Size(max = 50)
    private String poErp;

    @Size(max = 50)
    private String prNumber;

    @Size(max = 50)
    private String contractNo;

    @Size(max = 50)
    private String lineItemStt;

    @Size(max = 50)
    private String poLineItem;

    @Size(max = 50)
    private String itemCode;

    @Size(max = 255)
    private String itemName;

    @Size(max = 50)
    private String whsCode;

    @Size(max = 50)
    private String unit;

    private Integer quantity;

    @Size(max = 50)
    private String invoiceSmartNo;

    @Size(max = 50)
    private String invoiceNo;

    private LocalDate invoiceDate;

    private Integer deliveryTurn;

    @NotNull
    private DeliveryDTO delivery;

    public UUID getLineId() {
        return lineId;
    }

    public void setLineId(UUID lineId) {
        this.lineId = lineId;
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

    public DeliveryDTO getDelivery() {
        return delivery;
    }

    public void setDelivery(DeliveryDTO delivery) {
        this.delivery = delivery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetailDeliveryDTO)) {
            return false;
        }

        DetailDeliveryDTO detailDeliveryDTO = (DetailDeliveryDTO) o;
        if (this.lineId == null) {
            return false;
        }
        return Objects.equals(this.lineId, detailDeliveryDTO.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.lineId);
    }

    // prettier-ignore
    @Override
     public String toString() {
         return "DetailDeliveryDTO{" +
             "lineId='" + getLineId() + "'" +
             ", poSmart='" + getPoSmart() + "'" +
             ", poErp='" + getPoErp() + "'" +
             ", prNumber='" + getPrNumber() + "'" +
             ", contractNo='" + getContractNo() + "'" +
             ", lineItemStt='" + getLineItemStt() + "'" +
             ", poLineItem='" + getPoLineItem() + "'" +
             ", itemCode='" + getItemCode() + "'" +
             ", itemName='" + getItemName() + "'" +
             ", whsCode='" + getWhsCode() + "'" +
             ", unit='" + getUnit() + "'" +
             ", quantity=" + getQuantity() +
             ", invoiceSmartNo='" + getInvoiceSmartNo() + "'" +
             ", invoiceNo='" + getInvoiceNo() + "'" +
             ", invoiceDate='" + getInvoiceDate() + "'" +
             ", deliveryTurn=" + getDeliveryTurn() +
             ", delivery=" + getDelivery() +
             "}";
     }
}
