package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
// import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.PoDetail} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.PoDetailResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /po-details?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
// @ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PoDetailCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter sapCode;

    private StringFilter sapName;

    private IntegerFilter quantityContainer;

    private IntegerFilter totalQuantity;

    private StringFilter partNumber;

    private LongFilter vendorTemDetailId;

    private LongFilter importVendorTemTransactionsId;

    private Boolean distinct;

    public PoDetailCriteria() {}

    public PoDetailCriteria(PoDetailCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.sapCode = other
            .optionalSapCode()
            .map(StringFilter::copy)
            .orElse(null);
        this.sapName = other
            .optionalSapName()
            .map(StringFilter::copy)
            .orElse(null);
        this.quantityContainer = other
            .optionalQuantityContainer()
            .map(IntegerFilter::copy)
            .orElse(null);
        this.totalQuantity = other
            .optionalTotalQuantity()
            .map(IntegerFilter::copy)
            .orElse(null);
        this.partNumber = other
            .optionalPartNumber()
            .map(StringFilter::copy)
            .orElse(null);
        this.vendorTemDetailId = other
            .optionalVendorTemDetailId()
            .map(LongFilter::copy)
            .orElse(null);
        this.importVendorTemTransactionsId = other
            .optionalImportVendorTemTransactionsId()
            .map(LongFilter::copy)
            .orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PoDetailCriteria copy() {
        return new PoDetailCriteria(this);
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

    public StringFilter getSapCode() {
        return sapCode;
    }

    public Optional<StringFilter> optionalSapCode() {
        return Optional.ofNullable(sapCode);
    }

    public StringFilter sapCode() {
        if (sapCode == null) {
            setSapCode(new StringFilter());
        }
        return sapCode;
    }

    public void setSapCode(StringFilter sapCode) {
        this.sapCode = sapCode;
    }

    public StringFilter getSapName() {
        return sapName;
    }

    public Optional<StringFilter> optionalSapName() {
        return Optional.ofNullable(sapName);
    }

    public StringFilter sapName() {
        if (sapName == null) {
            setSapName(new StringFilter());
        }
        return sapName;
    }

    public void setSapName(StringFilter sapName) {
        this.sapName = sapName;
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

    public StringFilter getPartNumber() {
        return partNumber;
    }

    public Optional<StringFilter> optionalPartNumber() {
        return Optional.ofNullable(partNumber);
    }

    public StringFilter partNumber() {
        if (partNumber == null) {
            setPartNumber(new StringFilter());
        }
        return partNumber;
    }

    public void setPartNumber(StringFilter partNumber) {
        this.partNumber = partNumber;
    }

    public LongFilter getVendorTemDetailId() {
        return vendorTemDetailId;
    }

    public Optional<LongFilter> optionalVendorTemDetailId() {
        return Optional.ofNullable(vendorTemDetailId);
    }

    public LongFilter vendorTemDetailId() {
        if (vendorTemDetailId == null) {
            setVendorTemDetailId(new LongFilter());
        }
        return vendorTemDetailId;
    }

    public void setVendorTemDetailId(LongFilter vendorTemDetailId) {
        this.vendorTemDetailId = vendorTemDetailId;
    }

    public LongFilter getImportVendorTemTransactionsId() {
        return importVendorTemTransactionsId;
    }

    public Optional<LongFilter> optionalImportVendorTemTransactionsId() {
        return Optional.ofNullable(importVendorTemTransactionsId);
    }

    public LongFilter importVendorTemTransactionsId() {
        if (importVendorTemTransactionsId == null) {
            setImportVendorTemTransactionsId(new LongFilter());
        }
        return importVendorTemTransactionsId;
    }

    public void setImportVendorTemTransactionsId(
        LongFilter importVendorTemTransactionsId
    ) {
        this.importVendorTemTransactionsId = importVendorTemTransactionsId;
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
        final PoDetailCriteria that = (PoDetailCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(sapCode, that.sapCode) &&
            Objects.equals(sapName, that.sapName) &&
            Objects.equals(quantityContainer, that.quantityContainer) &&
            Objects.equals(totalQuantity, that.totalQuantity) &&
            Objects.equals(partNumber, that.partNumber) &&
            Objects.equals(vendorTemDetailId, that.vendorTemDetailId) &&
            Objects.equals(
                importVendorTemTransactionsId,
                that.importVendorTemTransactionsId
            ) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            sapCode,
            sapName,
            quantityContainer,
            totalQuantity,
            partNumber,
            vendorTemDetailId,
            importVendorTemTransactionsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PoDetailCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSapCode().map(f -> "sapCode=" + f + ", ").orElse("") +
            optionalSapName().map(f -> "sapName=" + f + ", ").orElse("") +
            optionalQuantityContainer().map(f -> "quantityContainer=" + f + ", ").orElse("") +
            optionalTotalQuantity().map(f -> "totalQuantity=" + f + ", ").orElse("") +
            optionalPartNumber().map(f -> "partNumber=" + f + ", ").orElse("") +
            optionalVendorTemDetailId().map(f -> "vendorTemDetailId=" + f + ", ").orElse("") +
            optionalImportVendorTemTransactionsId().map(f -> "importVendorTemTransactionsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
