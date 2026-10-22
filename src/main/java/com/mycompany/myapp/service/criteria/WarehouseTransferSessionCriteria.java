package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.WarehouseTransferSession} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.WarehouseTransferSessionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /warehouse-transfer-sessions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseTransferSessionCriteria
    implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter status;

    private StringFilter note;

    private InstantFilter createdAt;

    private StringFilter createdBy;

    private InstantFilter updatedAt;

    private StringFilter updatedBy;

    private InstantFilter deletedAt;

    private StringFilter deletedBy;

    private LongFilter detailsId;

    private Boolean distinct;

    public WarehouseTransferSessionCriteria() {}

    public WarehouseTransferSessionCriteria(
        WarehouseTransferSessionCriteria other
    ) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.status = other
            .optionalStatus()
            .map(StringFilter::copy)
            .orElse(null);
        this.note = other.optionalNote().map(StringFilter::copy).orElse(null);
        this.createdAt = other
            .optionalCreatedAt()
            .map(InstantFilter::copy)
            .orElse(null);
        this.createdBy = other
            .optionalCreatedBy()
            .map(StringFilter::copy)
            .orElse(null);
        this.updatedAt = other
            .optionalUpdatedAt()
            .map(InstantFilter::copy)
            .orElse(null);
        this.updatedBy = other
            .optionalUpdatedBy()
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
        this.detailsId = other
            .optionalDetailsId()
            .map(LongFilter::copy)
            .orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public WarehouseTransferSessionCriteria copy() {
        return new WarehouseTransferSessionCriteria(this);
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

    public StringFilter getNote() {
        return note;
    }

    public Optional<StringFilter> optionalNote() {
        return Optional.ofNullable(note);
    }

    public StringFilter note() {
        if (note == null) {
            setNote(new StringFilter());
        }
        return note;
    }

    public void setNote(StringFilter note) {
        this.note = note;
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

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
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

    public LongFilter getDetailsId() {
        return detailsId;
    }

    public Optional<LongFilter> optionalDetailsId() {
        return Optional.ofNullable(detailsId);
    }

    public LongFilter detailsId() {
        if (detailsId == null) {
            setDetailsId(new LongFilter());
        }
        return detailsId;
    }

    public void setDetailsId(LongFilter detailsId) {
        this.detailsId = detailsId;
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
        final WarehouseTransferSessionCriteria that =
            (WarehouseTransferSessionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(note, that.note) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(updatedBy, that.updatedBy) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(detailsId, that.detailsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            status,
            note,
            createdAt,
            createdBy,
            updatedAt,
            updatedBy,
            deletedAt,
            deletedBy,
            detailsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WarehouseTransferSessionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalNote().map(f -> "note=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalUpdatedBy().map(f -> "updatedBy=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalDetailsId().map(f -> "detailsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
