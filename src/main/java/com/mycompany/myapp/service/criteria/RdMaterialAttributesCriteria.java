package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
// import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the
 * {@link com.mycompany.myapp.domain.RdMaterialAttributes} entity. This class is
 * used
 * in {@link com.mycompany.myapp.web.rest.RdMaterialAttributesResource} to
 * receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /rd-material-attributes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific
 * {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
// @ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RdMaterialAttributesCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter attributes;

    private StringFilter description;

    private StringFilter attributesType;

    private StringFilter createdBy;

    private ZonedDateTimeFilter createdAt;

    private StringFilter updatedBy;

    private ZonedDateTimeFilter updatedAt;

    private Boolean distinct;

    public RdMaterialAttributesCriteria() {}

    public RdMaterialAttributesCriteria(RdMaterialAttributesCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.attributes = other
            .optionalAttributes()
            .map(StringFilter::copy)
            .orElse(null);
        this.description = other
            .optionalDescription()
            .map(StringFilter::copy)
            .orElse(null);
        this.attributesType = other
            .optionalAttributesType()
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
    public RdMaterialAttributesCriteria copy() {
        return new RdMaterialAttributesCriteria(this);
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

    public StringFilter getAttributes() {
        return attributes;
    }

    public Optional<StringFilter> optionalAttributes() {
        return Optional.ofNullable(attributes);
    }

    public StringFilter attributes() {
        if (attributes == null) {
            setAttributes(new StringFilter());
        }
        return attributes;
    }

    public void setAttributes(StringFilter attributes) {
        this.attributes = attributes;
    }

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getAttributesType() {
        return attributesType;
    }

    public Optional<StringFilter> optionalAttributesType() {
        return Optional.ofNullable(attributesType);
    }

    public StringFilter attributesType() {
        if (attributesType == null) {
            setAttributesType(new StringFilter());
        }
        return attributesType;
    }

    public void setAttributesType(StringFilter attributesType) {
        this.attributesType = attributesType;
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
        final RdMaterialAttributesCriteria that =
            (RdMaterialAttributesCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(attributes, that.attributes) &&
            Objects.equals(description, that.description) &&
            Objects.equals(attributesType, that.attributesType) &&
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
            attributes,
            description,
            attributesType,
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
        return "RdMaterialAttributesCriteria{" +
                optionalId().map(f -> "id=" + f + ", ").orElse("") +
                optionalAttributes().map(f -> "attributes=" + f + ", ").orElse("") +
                optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
                optionalAttributesType().map(f -> "attributesType=" + f + ", ").orElse("") +
                optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
                optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
                optionalUpdatedBy().map(f -> "updatedBy=" + f + ", ").orElse("") +
                optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
                optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
                "}";
    }
}
