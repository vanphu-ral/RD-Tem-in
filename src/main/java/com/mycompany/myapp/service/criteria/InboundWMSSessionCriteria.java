package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InboundWMSSessionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter status;
    private StringFilter note;
    private StringFilter createdBy;
    private ZonedDateTimeFilter createdAt;
    private ZonedDateTimeFilter wmsSentAt;

    public InboundWMSSessionCriteria() {}

    public InboundWMSSessionCriteria(InboundWMSSessionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.status = other
            .optionalStatus()
            .map(StringFilter::copy)
            .orElse(null);
        this.note = other.optionalNote().map(StringFilter::copy).orElse(null);
        this.createdBy = other
            .optionalCreatedBy()
            .map(StringFilter::copy)
            .orElse(null);
        this.createdAt = other
            .optionalCreatedAt()
            .map(ZonedDateTimeFilter::copy)
            .orElse(null);
        this.wmsSentAt = other
            .optionalWmsSentAt()
            .map(ZonedDateTimeFilter::copy)
            .orElse(null);
    }

    @Override
    public InboundWMSSessionCriteria copy() {
        return new InboundWMSSessionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) setId(new LongFilter());
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
        if (status == null) setStatus(new StringFilter());
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
        if (note == null) setNote(new StringFilter());
        return note;
    }

    public void setNote(StringFilter note) {
        this.note = note;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public Optional<StringFilter> optionalCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    public StringFilter createdBy() {
        if (createdBy == null) setCreatedBy(new StringFilter());
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
        if (createdAt == null) setCreatedAt(new ZonedDateTimeFilter());
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTimeFilter createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTimeFilter getWmsSentAt() {
        return wmsSentAt;
    }

    public Optional<ZonedDateTimeFilter> optionalWmsSentAt() {
        return Optional.ofNullable(wmsSentAt);
    }

    public ZonedDateTimeFilter wmsSentAt() {
        if (wmsSentAt == null) setWmsSentAt(new ZonedDateTimeFilter());
        return wmsSentAt;
    }

    public void setWmsSentAt(ZonedDateTimeFilter wmsSentAt) {
        this.wmsSentAt = wmsSentAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InboundWMSSessionCriteria)) return false;
        InboundWMSSessionCriteria that = (InboundWMSSessionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(note, that.note) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(wmsSentAt, that.wmsSentAt)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, createdBy, createdAt, wmsSentAt);
    }

    @Override
    public String toString() {
        return (
            "InboundWMSSessionCriteria{" +
            optionalId()
                .map(f -> "id=" + f + ", ")
                .orElse("") +
            optionalStatus()
                .map(f -> "status=" + f + ", ")
                .orElse("") +
            optionalNote()
                .map(f -> "note=" + f + ", ")
                .orElse("") +
            optionalCreatedBy()
                .map(f -> "createdBy=" + f + ", ")
                .orElse("") +
            optionalCreatedAt()
                .map(f -> "createdAt=" + f + ", ")
                .orElse("") +
            optionalWmsSentAt()
                .map(f -> "wmsSentAt=" + f + ", ")
                .orElse("")
        );
    }
}
