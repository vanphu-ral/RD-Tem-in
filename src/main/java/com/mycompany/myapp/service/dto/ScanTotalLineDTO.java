package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 1 dòng hàng gửi gateway: tổng SL thực nhận (quantityReceived) của 1 dòng PO trong đơn giao
 * hàng. Tên trường theo tài liệu gateway (mục 4.5.2) — trường không có dữ liệu để null.
 */
public class ScanTotalLineDTO implements Serializable {

    /** Mã dòng do Smart sinh (GUID) — sap_por1_r1.line_id */
    private String lineId;

    /** Phiếu lịch giao hàng (GUID) — sap_por1_r1.delivery_id */
    private String deliveryId;

    private String poSmart;

    /** Số PO trên ERP/SAP — sap_por1_r1.doc_entry */
    private String poErp;

    private String prNumber;

    private String contractNo;

    /** Dòng vật tư trong PO ERP — sap_por1_r1.line_num (tên trường theo tài liệu: poLineIem) */
    @JsonProperty("poLineIem")
    private String poLineItem;

    private String itemCode;

    /** sap_por1_r1.dscription */
    private String itemName;

    /** sap_por1_r1.uom_code */
    private String unit;

    /** SL dự kiến giao — sap_por1_r1.quantity */
    private BigDecimal quantity;

    /** SL thực nhận = tổng initial_quantity các thùng của dòng (cộng dồn mọi lần scan) */
    private Long quantityReceived;

    /** Mã giao dịch nhập kho (GRPO) — chưa có → null (tên theo tài liệu: grpoErrp) */
    @JsonProperty("grpoErrp")
    private String grpoErp;

    private String grpoItemLine;

    /** Thời gian duyệt nhập kho — chưa có → null */
    private String timeApproved;

    private String whsCode;

    private String invoiceSmartNo;

    private String invoiceNo;

    /** yyyy-MM-dd */
    private String invoiceDate;

    private Integer deliveryTurn;

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

    public Long getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(Long quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public String getGrpoErp() {
        return grpoErp;
    }

    public void setGrpoErp(String grpoErp) {
        this.grpoErp = grpoErp;
    }

    public String getGrpoItemLine() {
        return grpoItemLine;
    }

    public void setGrpoItemLine(String grpoItemLine) {
        this.grpoItemLine = grpoItemLine;
    }

    public String getTimeApproved() {
        return timeApproved;
    }

    public void setTimeApproved(String timeApproved) {
        this.timeApproved = timeApproved;
    }

    public String getWhsCode() {
        return whsCode;
    }

    public void setWhsCode(String whsCode) {
        this.whsCode = whsCode;
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
