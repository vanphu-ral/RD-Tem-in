package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.Delivery;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A DTO for a Delivery with its DetailDelivery lines, each line holding
 * the VendorLabelInfo boxes (enriched with serialPallet from PalletBoxMapping).
 */
public class DeliveryNotificationDetailDTO implements Serializable {

    private UUID id;

    private String deliveryNotificationCode;

    private String noticeNo;

    private String supplierCode;

    private String supplierName;

    private String factory;

    private LocalDate noticeDate;

    private Integer numberOfPo;

    private Integer numberOfItem;

    private String source;

    private String createdBy;

    private Instant createdAt;

    private List<DetailDeliveryDetailDTO> detailDeliveryList;

    public DeliveryNotificationDetailDTO() {
        super();
    }

    public DeliveryNotificationDetailDTO(
        Delivery entity,
        List<DetailDeliveryDetailDTO> detailDeliveryList
    ) {
        super();
        if (entity != null) {
            this.id = entity.getDeliveryId();
            this.deliveryNotificationCode = entity.getNoticeNo();
            this.noticeNo = entity.getNoticeNo();
            this.supplierCode = entity.getSupplierCode();
            this.supplierName = entity.getSupplierName();
            this.factory = entity.getFactory();
            this.noticeDate = entity.getNoticeDate();
            this.numberOfPo = entity.getNumberOfPo();
            this.numberOfItem = entity.getNumberOfItem();
            this.source = entity.getSource();
            this.createdBy = entity.getCreatedBy();
            this.createdAt = entity.getCreatedAt() != null
                ? entity.getCreatedAt().toInstant()
                : null;
        }
        this.detailDeliveryList = detailDeliveryList == null
            ? new ArrayList<>()
            : detailDeliveryList;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDeliveryNotificationCode() {
        return deliveryNotificationCode;
    }

    public void setDeliveryNotificationCode(String deliveryNotificationCode) {
        this.deliveryNotificationCode = deliveryNotificationCode;
    }

    public String getNoticeNo() {
        return noticeNo;
    }

    public void setNoticeNo(String noticeNo) {
        this.noticeNo = noticeNo;
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

    public LocalDate getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(LocalDate noticeDate) {
        this.noticeDate = noticeDate;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<DetailDeliveryDetailDTO> getDetailDeliveryList() {
        return detailDeliveryList;
    }

    public void setDetailDeliveryList(
        List<DetailDeliveryDetailDTO> detailDeliveryList
    ) {
        this.detailDeliveryList = detailDeliveryList;
    }

    @Override
    public String toString() {
        return (
            "DeliveryNotificationDetailDTO{" +
            "id=" +
            id +
            ", deliveryNotificationCode='" +
            deliveryNotificationCode +
            "'" +
            ", status='" +
            source +
            "'" +
            ", detailDeliveryList=" +
            detailDeliveryList +
            "}"
        );
    }
}
