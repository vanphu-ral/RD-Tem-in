package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A DetailDelivery.
 */
@Entity
@Table(name = "detail_delivery")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DetailDelivery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "line_id", nullable = false)
    private UUID lineId;

    @Size(max = 50)
    @Column(name = "po_smart", length = 50)
    private String poSmart;

    @Size(max = 50)
    @Column(name = "po_erp", length = 50)
    private String poErp;

    @Size(max = 50)
    @Column(name = "pr_number", length = 50)
    private String prNumber;

    @Size(max = 50)
    @Column(name = "contract_no", length = 50)
    private String contractNo;

    @Size(max = 50)
    @Column(name = "line_item_stt", length = 50)
    private String lineItemStt;

    @Size(max = 50)
    @Column(name = "po_line_item", length = 50)
    private String poLineItem;

    @Size(max = 50)
    @Column(name = "item_code", length = 50)
    private String itemCode;

    @Size(max = 255)
    @Column(name = "item_name", length = 255)
    private String itemName;

    @Size(max = 50)
    @Column(name = "whs_code", length = 50)
    private String whsCode;

    @Size(max = 50)
    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "quantity")
    private Integer quantity;

    @Size(max = 50)
    @Column(name = "invoice_smart_no", length = 50)
    private String invoiceSmartNo;

    @Size(max = 50)
    @Column(name = "invoice_no", length = 50)
    private String invoiceNo;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "delivery_turn")
    private Integer deliveryTurn;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "detailDeliveries" }, allowSetters = true)
    @JoinColumn(name = "delivery_id", referencedColumnName = "delivery_id")
    private Delivery delivery;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getLineId() {
        return this.lineId;
    }

    public DetailDelivery lineId(UUID lineId) {
        this.setLineId(lineId);
        return this;
    }

    public void setLineId(UUID lineId) {
        this.lineId = lineId;
    }

    public String getPoSmart() {
        return this.poSmart;
    }

    public DetailDelivery poSmart(String poSmart) {
        this.setPoSmart(poSmart);
        return this;
    }

    public void setPoSmart(String poSmart) {
        this.poSmart = poSmart;
    }

    public String getPoErp() {
        return this.poErp;
    }

    public DetailDelivery poErp(String poErp) {
        this.setPoErp(poErp);
        return this;
    }

    public void setPoErp(String poErp) {
        this.poErp = poErp;
    }

    public String getPrNumber() {
        return this.prNumber;
    }

    public DetailDelivery prNumber(String prNumber) {
        this.setPrNumber(prNumber);
        return this;
    }

    public void setPrNumber(String prNumber) {
        this.prNumber = prNumber;
    }

    public String getContractNo() {
        return this.contractNo;
    }

    public DetailDelivery contractNo(String contractNo) {
        this.setContractNo(contractNo);
        return this;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getLineItemStt() {
        return this.lineItemStt;
    }

    public DetailDelivery lineItemStt(String lineItemStt) {
        this.setLineItemStt(lineItemStt);
        return this;
    }

    public void setLineItemStt(String lineItemStt) {
        this.lineItemStt = lineItemStt;
    }

    public String getPoLineItem() {
        return this.poLineItem;
    }

    public DetailDelivery poLineItem(String poLineItem) {
        this.setPoLineItem(poLineItem);
        return this;
    }

    public void setPoLineItem(String poLineItem) {
        this.poLineItem = poLineItem;
    }

    public String getItemCode() {
        return this.itemCode;
    }

    public DetailDelivery itemCode(String itemCode) {
        this.setItemCode(itemCode);
        return this;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return this.itemName;
    }

    public DetailDelivery itemName(String itemName) {
        this.setItemName(itemName);
        return this;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getWhsCode() {
        return this.whsCode;
    }

    public DetailDelivery whsCode(String whsCode) {
        this.setWhsCode(whsCode);
        return this;
    }

    public void setWhsCode(String whsCode) {
        this.whsCode = whsCode;
    }

    public String getUnit() {
        return this.unit;
    }

    public DetailDelivery unit(String unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public DetailDelivery quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getInvoiceSmartNo() {
        return this.invoiceSmartNo;
    }

    public DetailDelivery invoiceSmartNo(String invoiceSmartNo) {
        this.setInvoiceSmartNo(invoiceSmartNo);
        return this;
    }

    public void setInvoiceSmartNo(String invoiceSmartNo) {
        this.invoiceSmartNo = invoiceSmartNo;
    }

    public String getInvoiceNo() {
        return this.invoiceNo;
    }

    public DetailDelivery invoiceNo(String invoiceNo) {
        this.setInvoiceNo(invoiceNo);
        return this;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public LocalDate getInvoiceDate() {
        return this.invoiceDate;
    }

    public DetailDelivery invoiceDate(LocalDate invoiceDate) {
        this.setInvoiceDate(invoiceDate);
        return this;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Integer getDeliveryTurn() {
        return this.deliveryTurn;
    }

    public DetailDelivery deliveryTurn(Integer deliveryTurn) {
        this.setDeliveryTurn(deliveryTurn);
        return this;
    }

    public void setDeliveryTurn(Integer deliveryTurn) {
        this.deliveryTurn = deliveryTurn;
    }

    public Delivery getDelivery() {
        return this.delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public DetailDelivery delivery(Delivery delivery) {
        this.setDelivery(delivery);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetailDelivery)) {
            return false;
        }
        DetailDelivery detailDelivery = (DetailDelivery) o;
        return Objects.equals(this.lineId, detailDelivery.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.lineId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DetailDelivery{" +
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
            "}";
    }
}
