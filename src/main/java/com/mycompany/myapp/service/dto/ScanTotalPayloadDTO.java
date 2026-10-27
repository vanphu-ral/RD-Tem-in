package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Tổng SL thực nhận của 1 đơn thông báo giao hàng (noticeNo) — trả cho gateway gọi lấy:
 * mọi dòng PO của đơn, mỗi dòng kèm quantityReceived tính tại thời điểm gọi.
 */
public class ScanTotalPayloadDTO implements Serializable {

    /** Mã thông báo giao hàng — delivery_notification.delivery_notification_code */
    private String noticeNo;

    /** Trạng thái đơn: New (chưa scan) / SCANNING (đang scan) / COMPLETED (hoàn thành) — delivery_notification.status */
    private String status;

    /** Phiếu lịch giao hàng (GUID) — delivery_notification.delivery_id */
    private String deliveryId;

    /** Số hóa đơn — delivery_notification.invoice_number */
    private String invoiceNumber;

    /** Mã hợp đồng — delivery_notification.contract_code */
    private String contractCode;

    /** Loại vận chuyển (vd Transport) — delivery_notification.type */
    private String type;

    /** Mã nhà cung cấp — delivery_notification.vendor_code */
    private String vendorCode;

    /** Tên nhà cung cấp — delivery_notification.vendor_name */
    private String vendorName;

    /** Ngày thông báo giao hàng (yyyy-MM-dd) — delivery_notification.notice_date */
    private String noticeDate;

    /** Nhà máy — delivery_notification.factory */
    private String factory;

    /** Nguồn tạo đơn (vd system = tạo từ web RD) — delivery_notification.source */
    private String source;

    /** Thời điểm tính số liệu (ISO-8601, UTC) */
    private String generatedAt;

    private List<ScanTotalLineDTO> lines = new ArrayList<>();

    public String getNoticeNo() {
        return noticeNo;
    }

    public void setNoticeNo(String noticeNo) {
        this.noticeNo = noticeNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(String noticeDate) {
        this.noticeDate = noticeDate;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public List<ScanTotalLineDTO> getLines() {
        return lines;
    }

    public void setLines(List<ScanTotalLineDTO> lines) {
        this.lines = lines;
    }
}
