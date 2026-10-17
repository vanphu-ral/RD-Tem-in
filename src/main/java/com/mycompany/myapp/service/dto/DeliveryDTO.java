package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Delivery} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliveryDTO implements Serializable {

    private UUID deliveryId;

    @Size(max = 50)
    private String type;

    @Size(max = 50)
    private String noticeNo;

    private LocalDate noticeDate;

    @Size(max = 50)
    private String supplierCode;

    @Size(max = 255)
    private String supplierName;

    @Size(max = 50)
    private String factory;

    @Size(max = 50)
    private String deliveryNotificationCode;

    private ZonedDateTime dateArrivalRangDong;

    @Size(max = 50)
    private String vehicleNo;

    private Integer numberOfPo;

    private Integer numberOfItem;

    @Size(max = 50)
    private String source;

    private ZonedDateTime createdAt;

    @Size(max = 50)
    private String createdBy;

    private ZonedDateTime deletedAt;

    @Size(max = 50)
    private String deletedBy;

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

    public String getDeliveryNotificationCode() {
        return deliveryNotificationCode;
    }

    public void setDeliveryNotificationCode(String deliveryNotificationCode) {
        this.deliveryNotificationCode = deliveryNotificationCode;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeliveryDTO)) {
            return false;
        }

        DeliveryDTO deliveryDTO = (DeliveryDTO) o;
        if (this.deliveryId == null) {
            return false;
        }
        return Objects.equals(this.deliveryId, deliveryDTO.deliveryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.deliveryId);
    }

    // prettier-ignore
    @Override
    public String toString() {
         return "DeliveryDTO{" +
             "deliveryId='" + getDeliveryId() + "'" +
             ", type='" + getType() + "'" +
             ", noticeNo='" + getNoticeNo() + "'" +
             ", noticeDate='" + getNoticeDate() + "'" +
             ", supplierCode='" + getSupplierCode() + "'" +
             ", supplierName='" + getSupplierName() + "'" +
             ", factory='" + getFactory() + "'" +
             ", deliveryNotificationCode='" + getDeliveryNotificationCode() + "'" +
             ", dateArrivalRangDong='" + getDateArrivalRangDong() + "'" +
             ", vehicleNo='" + getVehicleNo() + "'" +
             ", numberOfPo=" + getNumberOfPo() +
             ", numberOfItem=" + getNumberOfItem() +
             ", source='" + getSource() + "'" +
             ", createdAt='" + getCreatedAt() + "'" +
             ", createdBy='" + getCreatedBy() + "'" +
             ", deletedAt='" + getDeletedAt() + "'" +
             ", deletedBy='" + getDeletedBy() + "'" +
             "}";
    }
}
