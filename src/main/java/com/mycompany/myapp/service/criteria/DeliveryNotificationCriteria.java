package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.DeliveryNotification} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.DeliveryNotificationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /delivery-notifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliveryNotificationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter deliveryNotificationCode;

    private StringFilter invoiceNumber;

    private StringFilter contractCode;

    private StringFilter vendorName;

    private StringFilter contNo;

    private InstantFilter entryDate;

    private IntegerFilter numberOfPo;

    private IntegerFilter numberOfItem;

    private StringFilter status;

    private StringFilter source;

    private InstantFilter createdAt;

    private StringFilter createdBy;

    private InstantFilter deletedAt;

    private StringFilter deletedBy;

    private Boolean distinct;

    public DeliveryNotificationCriteria() {}

    public DeliveryNotificationCriteria(DeliveryNotificationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.deliveryNotificationCode = other
            .optionalDeliveryNotificationCode()
            .map(StringFilter::copy)
            .orElse(null);
        this.invoiceNumber = other
            .optionalInvoiceNumber()
            .map(StringFilter::copy)
            .orElse(null);
        this.contractCode = other
            .optionalContractCode()
            .map(StringFilter::copy)
            .orElse(null);
        this.vendorName = other
            .optionalVendorName()
            .map(StringFilter::copy)
            .orElse(null);
        this.contNo = other
            .optionalContNo()
            .map(StringFilter::copy)
            .orElse(null);
        this.entryDate = other
            .optionalEntryDate()
            .map(InstantFilter::copy)
            .orElse(null);
        this.numberOfPo = other
            .optionalNumberOfPo()
            .map(IntegerFilter::copy)
            .orElse(null);
        this.numberOfItem = other
            .optionalNumberOfItem()
            .map(IntegerFilter::copy)
            .orElse(null);
        this.status = other
            .optionalStatus()
            .map(StringFilter::copy)
            .orElse(null);
        this.source = other
            .optionalSource()
            .map(StringFilter::copy)
            .orElse(null);
        this.createdAt = other
            .optionalCreatedAt()
            .map(InstantFilter::copy)
            .orElse(null);
        this.createdBy = other
            .optionalCreatedBy()
            .map(StringFilter::copy)
            .orElse(null);
        this.deletedAt = other
            .optionalDeletedAt()
            .map(InstantFilter::copy)
            .orElse(null);
        this.deletedBy = other
            .optionalDeletedBy()
            .map(StringFilter::copy)
            .orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DeliveryNotificationCriteria copy() {
        return new DeliveryNotificationCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getDeliveryNotificationCode() {
        return deliveryNotificationCode;
    }

    public Optional<StringFilter> optionalDeliveryNotificationCode() {
        return Optional.ofNullable(deliveryNotificationCode);
    }

    public StringFilter deliveryNotificationCode() {
        if (deliveryNotificationCode == null) {
            setDeliveryNotificationCode(new StringFilter());
        }
        return deliveryNotificationCode;
    }

    public void setDeliveryNotificationCode(
        StringFilter deliveryNotificationCode
    ) {
        this.deliveryNotificationCode = deliveryNotificationCode;
    }

    public StringFilter getInvoiceNumber() {
        return invoiceNumber;
    }

    public Optional<StringFilter> optionalInvoiceNumber() {
        return Optional.ofNullable(invoiceNumber);
    }

    public StringFilter invoiceNumber() {
        if (invoiceNumber == null) {
            setInvoiceNumber(new StringFilter());
        }
        return invoiceNumber;
    }

    public void setInvoiceNumber(StringFilter invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public StringFilter getContractCode() {
        return contractCode;
    }

    public Optional<StringFilter> optionalContractCode() {
        return Optional.ofNullable(contractCode);
    }

    public StringFilter contractCode() {
        if (contractCode == null) {
            setContractCode(new StringFilter());
        }
        return contractCode;
    }

    public void setContractCode(StringFilter contractCode) {
        this.contractCode = contractCode;
    }

    public StringFilter getVendorName() {
        return vendorName;
    }

    public Optional<StringFilter> optionalVendorName() {
        return Optional.ofNullable(vendorName);
    }

    public StringFilter vendorName() {
        if (vendorName == null) {
            setVendorName(new StringFilter());
        }
        return vendorName;
    }

    public void setVendorName(StringFilter vendorName) {
        this.vendorName = vendorName;
    }

    public StringFilter getContNo() {
        return contNo;
    }

    public Optional<StringFilter> optionalContNo() {
        return Optional.ofNullable(contNo);
    }

    public StringFilter contNo() {
        if (contNo == null) {
            setContNo(new StringFilter());
        }
        return contNo;
    }

    public void setContNo(StringFilter contNo) {
        this.contNo = contNo;
    }

    public InstantFilter getEntryDate() {
        return entryDate;
    }

    public Optional<InstantFilter> optionalEntryDate() {
        return Optional.ofNullable(entryDate);
    }

    public InstantFilter entryDate() {
        if (entryDate == null) {
            setEntryDate(new InstantFilter());
        }
        return entryDate;
    }

    public void setEntryDate(InstantFilter entryDate) {
        this.entryDate = entryDate;
    }

    public IntegerFilter getNumberOfPo() {
        return numberOfPo;
    }

    public Optional<IntegerFilter> optionalNumberOfPo() {
        return Optional.ofNullable(numberOfPo);
    }

    public IntegerFilter numberOfPo() {
        if (numberOfPo == null) {
            setNumberOfPo(new IntegerFilter());
        }
        return numberOfPo;
    }

    public void setNumberOfPo(IntegerFilter numberOfPo) {
        this.numberOfPo = numberOfPo;
    }

    public IntegerFilter getNumberOfItem() {
        return numberOfItem;
    }

    public Optional<IntegerFilter> optionalNumberOfItem() {
        return Optional.ofNullable(numberOfItem);
    }

    public IntegerFilter numberOfItem() {
        if (numberOfItem == null) {
            setNumberOfItem(new IntegerFilter());
        }
        return numberOfItem;
    }

    public void setNumberOfItem(IntegerFilter numberOfItem) {
        this.numberOfItem = numberOfItem;
    }

    public StringFilter getStatus() {
        return status;
    }

    public Optional<StringFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StringFilter status() {
        if (status == null) {
            setStatus(new StringFilter());
        }
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public StringFilter getSource() {
        return source;
    }

    public Optional<StringFilter> optionalSource() {
        return Optional.ofNullable(source);
    }

    public StringFilter source() {
        if (source == null) {
            setSource(new StringFilter());
        }
        return source;
    }

    public void setSource(StringFilter source) {
        this.source = source;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public Optional<StringFilter> optionalCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            setCreatedBy(new StringFilter());
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public InstantFilter getDeletedAt() {
        return deletedAt;
    }

    public Optional<InstantFilter> optionalDeletedAt() {
        return Optional.ofNullable(deletedAt);
    }

    public InstantFilter deletedAt() {
        if (deletedAt == null) {
            setDeletedAt(new InstantFilter());
        }
        return deletedAt;
    }

    public void setDeletedAt(InstantFilter deletedAt) {
        this.deletedAt = deletedAt;
    }

    public StringFilter getDeletedBy() {
        return deletedBy;
    }

    public Optional<StringFilter> optionalDeletedBy() {
        return Optional.ofNullable(deletedBy);
    }

    public StringFilter deletedBy() {
        if (deletedBy == null) {
            setDeletedBy(new StringFilter());
        }
        return deletedBy;
    }

    public void setDeletedBy(StringFilter deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DeliveryNotificationCriteria that =
            (DeliveryNotificationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(
                deliveryNotificationCode,
                that.deliveryNotificationCode
            ) &&
            Objects.equals(invoiceNumber, that.invoiceNumber) &&
            Objects.equals(contractCode, that.contractCode) &&
            Objects.equals(vendorName, that.vendorName) &&
            Objects.equals(contNo, that.contNo) &&
            Objects.equals(entryDate, that.entryDate) &&
            Objects.equals(numberOfPo, that.numberOfPo) &&
            Objects.equals(numberOfItem, that.numberOfItem) &&
            Objects.equals(status, that.status) &&
            Objects.equals(source, that.source) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            deliveryNotificationCode,
            invoiceNumber,
            contractCode,
            vendorName,
            contNo,
            entryDate,
            numberOfPo,
            numberOfItem,
            status,
            source,
            createdAt,
            createdBy,
            deletedAt,
            deletedBy,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliveryNotificationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalDeliveryNotificationCode().map(f -> "deliveryNotificationCode=" + f + ", ").orElse("") +
            optionalInvoiceNumber().map(f -> "invoiceNumber=" + f + ", ").orElse("") +
            optionalContractCode().map(f -> "contractCode=" + f + ", ").orElse("") +
            optionalVendorName().map(f -> "vendorName=" + f + ", ").orElse("") +
            optionalContNo().map(f -> "contNo=" + f + ", ").orElse("") +
            optionalEntryDate().map(f -> "entryDate=" + f + ", ").orElse("") +
            optionalNumberOfPo().map(f -> "numberOfPo=" + f + ", ").orElse("") +
            optionalNumberOfItem().map(f -> "numberOfItem=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalSource().map(f -> "source=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
