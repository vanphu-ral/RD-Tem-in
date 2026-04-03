package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
// import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the
 * {@link com.mycompany.myapp.domain.ImportVendorTemTransactions} entity. This
 * class is used
 * in {@link com.mycompany.myapp.web.rest.ImportVendorTemTransactionsResource}
 * to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /import-vendor-tem-transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific
 * {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
// @ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImportVendorTemTransactionsCriteria
    implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter poNumber;

    private StringFilter vendorCode;

    private StringFilter vendorName;

    private LocalDateFilter entryDate;

    private StringFilter storageUnit;

    private IntegerFilter temIdentificationScenarioId;

    private StringFilter mappingConfig;

    private StringFilter status;

    private BooleanFilter panaSendStatus;

    private StringFilter createdBy;

    private ZonedDateTimeFilter createdAt;

    private StringFilter updatedBy;

    private ZonedDateTimeFilter updatedAt;

    private StringFilter deletedBy;

    private ZonedDateTimeFilter deletedAt;

    private LongFilter poDetailId;

    private LongFilter poImportTemId;

    private Boolean distinct;

    public ImportVendorTemTransactionsCriteria() {}

    public ImportVendorTemTransactionsCriteria(
        ImportVendorTemTransactionsCriteria other
    ) {
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
        this.storageUnit = other
            .optionalStorageUnit()
            .map(StringFilter::copy)
            .orElse(null);
        this.temIdentificationScenarioId = other
            .optionalTemIdentificationScenarioId()
            .map(IntegerFilter::copy)
            .orElse(null);
        this.mappingConfig = other
            .optionalMappingConfig()
            .map(StringFilter::copy)
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
        this.panaSendStatus = other
            .optionalPanaSendStatus()
            .map(BooleanFilter::copy)
            .orElse(null);
        this.poDetailId = other
            .optionalPoDetailId()
            .map(LongFilter::copy)
            .orElse(null);
        this.poImportTemId = other
            .optionalPoImportTemId()
            .map(LongFilter::copy)
            .orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ImportVendorTemTransactionsCriteria copy() {
        return new ImportVendorTemTransactionsCriteria(this);
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

    public StringFilter getStorageUnit() {
        return storageUnit;
    }

    public Optional<StringFilter> optionalStorageUnit() {
        return Optional.ofNullable(storageUnit);
    }

    public StringFilter storageUnit() {
        if (storageUnit == null) {
            setStorageUnit(new StringFilter());
        }
        return storageUnit;
    }

    public void setStorageUnit(StringFilter storageUnit) {
        this.storageUnit = storageUnit;
    }

    public IntegerFilter getTemIdentificationScenarioId() {
        return temIdentificationScenarioId;
    }

    public Optional<IntegerFilter> optionalTemIdentificationScenarioId() {
        return Optional.ofNullable(temIdentificationScenarioId);
    }

    public IntegerFilter temIdentificationScenarioId() {
        if (temIdentificationScenarioId == null) {
            setTemIdentificationScenarioId(new IntegerFilter());
        }
        return temIdentificationScenarioId;
    }

    public void setTemIdentificationScenarioId(
        IntegerFilter temIdentificationScenarioId
    ) {
        this.temIdentificationScenarioId = temIdentificationScenarioId;
    }

    public StringFilter getMappingConfig() {
        return mappingConfig;
    }

    public Optional<StringFilter> optionalMappingConfig() {
        return Optional.ofNullable(mappingConfig);
    }

    public StringFilter mappingConfig() {
        if (mappingConfig == null) {
            setMappingConfig(new StringFilter());
        }
        return mappingConfig;
    }

    public void setMappingConfig(StringFilter mappingConfig) {
        this.mappingConfig = mappingConfig;
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

    public BooleanFilter getPanaSendStatus() {
        return panaSendStatus;
    }

    public Optional<BooleanFilter> optionalPanaSendStatus() {
        return Optional.ofNullable(panaSendStatus);
    }

    public BooleanFilter panaSendStatus() {
        if (panaSendStatus == null) {
            setPanaSendStatus(new BooleanFilter());
        }
        return panaSendStatus;
    }

    public void setPanaSendStatus(BooleanFilter panaSendStatus) {
        this.panaSendStatus = panaSendStatus;
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

    public LongFilter getPoDetailId() {
        return poDetailId;
    }

    public Optional<LongFilter> optionalPoDetailId() {
        return Optional.ofNullable(poDetailId);
    }

    public LongFilter poDetailId() {
        if (poDetailId == null) {
            setPoDetailId(new LongFilter());
        }
        return poDetailId;
    }

    public void setPoDetailId(LongFilter poDetailId) {
        this.poDetailId = poDetailId;
    }

    public LongFilter getPoImportTemId() {
        return poImportTemId;
    }

    public Optional<LongFilter> optionalPoImportTemId() {
        return Optional.ofNullable(poImportTemId);
    }

    public LongFilter poImportTemId() {
        if (poImportTemId == null) {
            setPoImportTemId(new LongFilter());
        }
        return poImportTemId;
    }

    public void setPoImportTemId(LongFilter poImportTemId) {
        this.poImportTemId = poImportTemId;
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
        final ImportVendorTemTransactionsCriteria that =
            (ImportVendorTemTransactionsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(poNumber, that.poNumber) &&
            Objects.equals(vendorCode, that.vendorCode) &&
            Objects.equals(vendorName, that.vendorName) &&
            Objects.equals(entryDate, that.entryDate) &&
            Objects.equals(storageUnit, that.storageUnit) &&
            Objects.equals(
                temIdentificationScenarioId,
                that.temIdentificationScenarioId
            ) &&
            Objects.equals(mappingConfig, that.mappingConfig) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedBy, that.updatedBy) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(poDetailId, that.poDetailId) &&
            Objects.equals(poImportTemId, that.poImportTemId) &&
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
            storageUnit,
            temIdentificationScenarioId,
            mappingConfig,
            status,
            panaSendStatus,
            createdBy,
            createdAt,
            updatedBy,
            updatedAt,
            deletedBy,
            deletedAt,
            poDetailId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportVendorTemTransactionsCriteria{" +
                optionalId().map(f -> "id=" + f + ", ").orElse("") +
                optionalPoNumber().map(f -> "poNumber=" + f + ", ").orElse("") +
                optionalVendorCode().map(f -> "vendorCode=" + f + ", ").orElse("") +
                optionalVendorName().map(f -> "vendorName=" + f + ", ").orElse("") +
                optionalEntryDate().map(f -> "entryDate=" + f + ", ").orElse("") +
                optionalStorageUnit().map(f -> "storageUnit=" + f + ", ").orElse("") +
                optionalTemIdentificationScenarioId().map(f -> "temIdentificationScenarioId=" + f + ", ").orElse("") +
                optionalMappingConfig().map(f -> "mappingConfig=" + f + ", ").orElse("") +
                optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
                optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
                optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
                optionalUpdatedBy().map(f -> "updatedBy=" + f + ", ").orElse("") +
                optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
                optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
                optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
                optionalPoDetailId().map(f -> "poDetailId=" + f + ", ").orElse("") +
                optionalPoImportTemId().map(f -> "poImportTemId=" + f + ", ").orElse("") +
                optionalPanaSendStatus().map(f -> "panaSendStatus=" + f + ", ").orElse("") +
                optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
                "}";
    }
}
