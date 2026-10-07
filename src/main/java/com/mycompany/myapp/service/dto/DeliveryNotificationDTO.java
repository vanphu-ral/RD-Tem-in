package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.DeliveryNotification} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliveryNotificationDTO implements Serializable {

    private Long id;

    @Size(max = 50)
    private String deliveryNotificationCode;

    @Size(max = 50)
    private String invoiceNumber;

    @Size(max = 50)
    private String contractCode;

    @Size(max = 255)
    private String vendorName;

    @Size(max = 50)
    private String contNo;

    private Instant entryDate;

    private Integer numberOfPo;

    private Integer numberOfItem;

    @Size(max = 50)
    private String status;

    @Size(max = 50)
    private String source;

    private Instant createAt;

    @Size(max = 50)
    private String createBy;

    private Instant deletedAt;

    @Size(max = 50)
    private String deletedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeliveryNotificationCode() {
        return deliveryNotificationCode;
    }

    public void setDeliveryNotificationCode(String deliveryNotificationCode) {
        this.deliveryNotificationCode = deliveryNotificationCode;
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

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getContNo() {
        return contNo;
    }

    public void setContNo(String contNo) {
        this.contNo = contNo;
    }

    public Instant getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Instant entryDate) {
        this.entryDate = entryDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Instant getCreatedAt() {
        return createAt;
    }

    public void setCreatedAt(Instant createAt) {
        this.createAt = createAt;
    }

    public String getCreatedBy() {
        return createBy;
    }

    public void setCreatedBy(String createBy) {
        this.createBy = createBy;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
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
        if (!(o instanceof DeliveryNotificationDTO)) {
            return false;
        }

        DeliveryNotificationDTO deliveryNotificationDTO =
            (DeliveryNotificationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, deliveryNotificationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliveryNotificationDTO{" +
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
            ", createdAt='" + getCreatedAt() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            "}";
    }
}
