package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.PoImportTem} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.PoImportTemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /po-import-tems?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PoImportTemCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter poNumber;

    private StringFilter vendorCode;

    private StringFilter vendorName;

    private LocalDateFilter entryDate;

    private IntegerFilter quantityContainer;

    private IntegerFilter totalQuantity;

    private StringFilter status;

    private StringFilter createdBy;

    private ZonedDateTimeFilter createdAt;

    private StringFilter updatedBy;

    private ZonedDateTimeFilter updatedAt;

    private StringFilter deletedBy;

    private ZonedDateTimeFilter deletedAt;

    private Boolean distinct;

    public PoImportTemCriteria() {}

    public PoImportTemCriteria(PoImportTemCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.poNumber = other
            .optionalPoNumber()
            .map(StringFilter::copy)
            .orElse(null);
        this.vendorCode = other
            .optionalVendorCode()
            .map(StringFilter::copy)
            .orElse(null);
        this.vendorName = other
            .optionalVendorName()
            .map(StringFilter::copy)
            .orElse(null);
        this.entryDate = other
            .optionalEntryDate()
            .map(LocalDateFilter::copy)
            .orElse(null);
        this.quantityContainer = other
            .optionalQuantityContainer()
            .map(IntegerFilter::copy)
            .orElse(null);
        this.totalQuantity = other
            .optionalTotalQuantity()
            .map(IntegerFilter::copy)
            .orElse(null);
        this.status = other
            .optionalStatus()
            .map(StringFilter::copy)
            .orElse(null);
        this.createdBy = other
            .optionalCreatedBy()
            .map(StringFilter::copy)
            .orElse(null);
        this.createdAt = other
            .optionalCreatedAt()
            .map(ZonedDateTimeFilter::copy)
            .orElse(null);
        this.updatedBy = other
            .optionalUpdatedBy()
            .map(StringFilter::copy)
            .orElse(null);
        this.updatedAt = other
            .optionalUpdatedAt()
            .map(ZonedDateTimeFilter::copy)
            .orElse(null);
        this.deletedBy = other
            .optionalDeletedBy()
            .map(StringFilter::copy)
            .orElse(null);
        this.deletedAt = other
            .optionalDeletedAt()
            .map(ZonedDateTimeFilter::copy)
            .orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PoImportTemCriteria copy() {
        return new PoImportTemCriteria(this);
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

    public StringFilter getPoNumber() {
        return poNumber;
    }

    public Optional<StringFilter> optionalPoNumber() {
        return Optional.ofNullable(poNumber);
    }

    public StringFilter poNumber() {
        if (poNumber == null) {
            setPoNumber(new StringFilter());
        }
        return poNumber;
    }

    public void setPoNumber(StringFilter poNumber) {
        this.poNumber = poNumber;
    }

    public StringFilter getVendorCode() {
        return vendorCode;
    }

    public Optional<StringFilter> optionalVendorCode() {
        return Optional.ofNullable(vendorCode);
    }

    public StringFilter vendorCode() {
        if (vendorCode == null) {
            setVendorCode(new StringFilter());
        }
        return vendorCode;
    }

    public void setVendorCode(StringFilter vendorCode) {
        this.vendorCode = vendorCode;
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

    public LocalDateFilter getEntryDate() {
        return entryDate;
    }

    public Optional<LocalDateFilter> optionalEntryDate() {
        return Optional.ofNullable(entryDate);
    }

    public LocalDateFilter entryDate() {
        if (entryDate == null) {
            setEntryDate(new LocalDateFilter());
        }
        return entryDate;
    }

    public void setEntryDate(LocalDateFilter entryDate) {
        this.entryDate = entryDate;
    }

    public IntegerFilter getQuantityContainer() {
        return quantityContainer;
    }

    public Optional<IntegerFilter> optionalQuantityContainer() {
        return Optional.ofNullable(quantityContainer);
    }

    public IntegerFilter quantityContainer() {
        if (quantityContainer == null) {
            setQuantityContainer(new IntegerFilter());
        }
        return quantityContainer;
    }

    public void setQuantityContainer(IntegerFilter quantityContainer) {
        this.quantityContainer = quantityContainer;
    }

    public IntegerFilter getTotalQuantity() {
        return totalQuantity;
    }

    public Optional<IntegerFilter> optionalTotalQuantity() {
        return Optional.ofNullable(totalQuantity);
    }

    public IntegerFilter totalQuantity() {
        if (totalQuantity == null) {
            setTotalQuantity(new IntegerFilter());
        }
        return totalQuantity;
    }

    public void setTotalQuantity(IntegerFilter totalQuantity) {
        this.totalQuantity = totalQuantity;
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

    public ZonedDateTimeFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<ZonedDateTimeFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public ZonedDateTimeFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new ZonedDateTimeFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTimeFilter createdAt) {
        this.createdAt = createdAt;
    }

    public StringFilter getUpdatedBy() {
        return updatedBy;
    }

    public Optional<StringFilter> optionalUpdatedBy() {
        return Optional.ofNullable(updatedBy);
    }

    public StringFilter updatedBy() {
        if (updatedBy == null) {
            setUpdatedBy(new StringFilter());
        }
        return updatedBy;
    }

    public void setUpdatedBy(StringFilter updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTimeFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<ZonedDateTimeFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public ZonedDateTimeFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new ZonedDateTimeFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTimeFilter updatedAt) {
        this.updatedAt = updatedAt;
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

    public ZonedDateTimeFilter getDeletedAt() {
        return deletedAt;
    }

    public Optional<ZonedDateTimeFilter> optionalDeletedAt() {
        return Optional.ofNullable(deletedAt);
    }

    public ZonedDateTimeFilter deletedAt() {
        if (deletedAt == null) {
            setDeletedAt(new ZonedDateTimeFilter());
        }
        return deletedAt;
    }

    public void setDeletedAt(ZonedDateTimeFilter deletedAt) {
        this.deletedAt = deletedAt;
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
        final PoImportTemCriteria that = (PoImportTemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(poNumber, that.poNumber) &&
            Objects.equals(vendorCode, that.vendorCode) &&
            Objects.equals(vendorName, that.vendorName) &&
            Objects.equals(entryDate, that.entryDate) &&
            Objects.equals(quantityContainer, that.quantityContainer) &&
            Objects.equals(totalQuantity, that.totalQuantity) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedBy, that.updatedBy) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            poNumber,
            vendorCode,
            vendorName,
            entryDate,
            quantityContainer,
            totalQuantity,
            status,
            createdBy,
            createdAt,
            updatedBy,
            updatedAt,
            deletedBy,
            deletedAt,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PoImportTemCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPoNumber().map(f -> "poNumber=" + f + ", ").orElse("") +
            optionalVendorCode().map(f -> "vendorCode=" + f + ", ").orElse("") +
            optionalVendorName().map(f -> "vendorName=" + f + ", ").orElse("") +
            optionalEntryDate().map(f -> "entryDate=" + f + ", ").orElse("") +
            optionalQuantityContainer().map(f -> "quantityContainer=" + f + ", ").orElse("") +
            optionalTotalQuantity().map(f -> "totalQuantity=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedBy().map(f -> "updatedBy=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
