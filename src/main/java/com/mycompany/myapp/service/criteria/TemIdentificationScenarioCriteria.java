package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.TemIdentificationScenario} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.TemIdentificationScenarioResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tem-identification-scenarios?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TemIdentificationScenarioCriteria
    implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter vendorCode;

    private StringFilter vendorName;

    private StringFilter mappingConfig;

    private StringFilter createdBy;

    private ZonedDateTimeFilter createdAt;

    private StringFilter updatedBy;

    private ZonedDateTimeFilter updatedAt;

    private Boolean distinct;

    public TemIdentificationScenarioCriteria() {}

    public TemIdentificationScenarioCriteria(
        TemIdentificationScenarioCriteria other
    ) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.vendorCode = other
            .optionalVendorCode()
            .map(StringFilter::copy)
            .orElse(null);
        this.vendorName = other
            .optionalVendorName()
            .map(StringFilter::copy)
            .orElse(null);
        this.mappingConfig = other
            .optionalMappingConfig()
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
        this.distinct = other.distinct;
    }

    @Override
    public TemIdentificationScenarioCriteria copy() {
        return new TemIdentificationScenarioCriteria(this);
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
        final TemIdentificationScenarioCriteria that =
            (TemIdentificationScenarioCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(vendorCode, that.vendorCode) &&
            Objects.equals(vendorName, that.vendorName) &&
            Objects.equals(mappingConfig, that.mappingConfig) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedBy, that.updatedBy) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            vendorCode,
            vendorName,
            mappingConfig,
            createdBy,
            createdAt,
            updatedBy,
            updatedAt,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TemIdentificationScenarioCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalVendorCode().map(f -> "vendorCode=" + f + ", ").orElse("") +
            optionalVendorName().map(f -> "vendorName=" + f + ", ").orElse("") +
            optionalMappingConfig().map(f -> "mappingConfig=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedBy().map(f -> "updatedBy=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
