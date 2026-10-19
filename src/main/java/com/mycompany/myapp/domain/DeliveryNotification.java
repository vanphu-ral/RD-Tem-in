package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A DeliveryNotification.
 */
@Entity
@Table(name = "delivery_notification")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliveryNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 50)
    @Column(name = "delivery_notification_code", length = 50)
    private String deliveryNotificationCode;

    @Size(max = 50)
    @Column(name = "invoice_number", length = 50)
    private String invoiceNumber;

    @Size(max = 50)
    @Column(name = "contract_code", length = 50)
    private String contractCode;

    @Size(max = 255)
    @Column(name = "vendor_name", length = 255)
    private String vendorName;

    @Size(max = 50)
    @Column(name = "cont_no", length = 50)
    private String contNo;

    @Column(name = "entry_date")
    private Instant entryDate;

    @Column(name = "number_of_po")
    private Integer numberOfPo;

    @Column(name = "number_of_item")
    private Integer numberOfItem;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @Size(max = 50)
    @Column(name = "source", length = 50)
    private String source;

    @Column(name = "delivery_id", length = 255)
    private UUID deliveryId;

    @Size(max = 50)
    @Column(name = "type", length = 50)
    private String type;

    @Size(max = 255)
    @Column(name = "vendor_code", length = 255)
    private String vendorCode;

    @Column(name = "notice_date")
    private java.time.LocalDate noticeDate;

    @Size(max = 50)
    @Column(name = "factory", length = 50)
    private String factory;

    @Column(name = "created_at")
    private Instant createdAt;

    @Size(max = 50)
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Size(max = 50)
    @Column(name = "deleted_by", length = 50)
    private String deletedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DeliveryNotification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeliveryNotificationCode() {
        return this.deliveryNotificationCode;
    }

    public DeliveryNotification deliveryNotificationCode(
        String deliveryNotificationCode
    ) {
        this.setDeliveryNotificationCode(deliveryNotificationCode);
        return this;
    }

    public void setDeliveryNotificationCode(String deliveryNotificationCode) {
        this.deliveryNotificationCode = deliveryNotificationCode;
    }

    public String getInvoiceNumber() {
        return this.invoiceNumber;
    }

    public DeliveryNotification invoiceNumber(String invoiceNumber) {
        this.setInvoiceNumber(invoiceNumber);
        return this;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getContractCode() {
        return this.contractCode;
    }

    public DeliveryNotification contractCode(String contractCode) {
        this.setContractCode(contractCode);
        return this;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getVendorName() {
        return this.vendorName;
    }

    public DeliveryNotification vendorName(String vendorName) {
        this.setVendorName(vendorName);
        return this;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getContNo() {
        return this.contNo;
    }

    public DeliveryNotification contNo(String contNo) {
        this.setContNo(contNo);
        return this;
    }

    public void setContNo(String contNo) {
        this.contNo = contNo;
    }

    public Instant getEntryDate() {
        return this.entryDate;
    }

    public DeliveryNotification entryDate(Instant entryDate) {
        this.setEntryDate(entryDate);
        return this;
    }

    public void setEntryDate(Instant entryDate) {
        this.entryDate = entryDate;
    }

    public Integer getNumberOfPo() {
        return this.numberOfPo;
    }

    public DeliveryNotification numberOfPo(Integer numberOfPo) {
        this.setNumberOfPo(numberOfPo);
        return this;
    }

    public void setNumberOfPo(Integer numberOfPo) {
        this.numberOfPo = numberOfPo;
    }

    public Integer getNumberOfItem() {
        return this.numberOfItem;
    }

    public DeliveryNotification numberOfItem(Integer numberOfItem) {
        this.setNumberOfItem(numberOfItem);
        return this;
    }

    public void setNumberOfItem(Integer numberOfItem) {
        this.numberOfItem = numberOfItem;
    }

    public String getStatus() {
        return this.status;
    }

    public DeliveryNotification status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return this.source;
    }

    public DeliveryNotification source(String source) {
        this.setSource(source);
        return this;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public UUID getDeliveryId() {
        return this.deliveryId;
    }

    public DeliveryNotification deliveryId(UUID deliveryId) {
        this.setDeliveryId(deliveryId);
        return this;
    }

    public void setDeliveryId(UUID deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getType() {
        return this.type;
    }

    public DeliveryNotification type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVendorCode() {
        return this.vendorCode;
    }

    public DeliveryNotification vendorCode(String vendorCode) {
        this.setVendorCode(vendorCode);
        return this;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public java.time.LocalDate getNoticeDate() {
        return this.noticeDate;
    }

    public DeliveryNotification noticeDate(java.time.LocalDate noticeDate) {
        this.setNoticeDate(noticeDate);
        return this;
    }

    public void setNoticeDate(java.time.LocalDate noticeDate) {
        this.noticeDate = noticeDate;
    }

    public String getFactory() {
        return this.factory;
    }

    public DeliveryNotification factory(String factory) {
        this.setFactory(factory);
        return this;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public DeliveryNotification createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public DeliveryNotification createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public DeliveryNotification deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedBy() {
        return this.deletedBy;
    }

    public DeliveryNotification deletedBy(String deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeliveryNotification)) {
            return false;
        }
        return (
            getId() != null &&
            getId().equals(((DeliveryNotification) o).getId())
        );
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliveryNotification{" +
            "id=" + getId() +
            ", deliveryNotificationCode='" + getDeliveryNotificationCode() + "'" +
            ", invoiceNumber='" + getInvoiceNumber() + "'" +
            ", contractCode='" + getContractCode() + "'" +
            ", vendorName='" + getVendorName() + "'" +
            ", contNo='" + getContNo() + "'" +
            ", entryDate='" + getEntryDate() + "'" +
            ", numberOfPo=" + getNumberOfPo() +
            ", numberOfItem=" + getNumberOfItem() +
            ", status='" + getStatus() + "'" +
            ", source='" + getSource() + "'" +
            ", deliveryId='" + getDeliveryId() + "'" +
            ", type='" + getType() + "'" +
            ", vendorCode='" + getVendorCode() + "'" +
            ", noticeDate='" + getNoticeDate() + "'" +
            ", factory='" + getFactory() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            "}";
    }
}
