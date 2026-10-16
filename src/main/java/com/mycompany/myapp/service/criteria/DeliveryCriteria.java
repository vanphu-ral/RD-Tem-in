package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Delivery} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.DeliveryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /deliveries?deliveryId.contains=something&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliveryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private UUIDFilter deliveryId;

    private StringFilter type;

    private StringFilter noticeNo;

    private LocalDateFilter noticeDate;

    private StringFilter supplierCode;

    private StringFilter supplierName;

    private StringFilter factory;

    private ZonedDateTimeFilter dateArrivalRangDong;

    private StringFilter vehicleNo;

    private IntegerFilter numberOfPo;

    private IntegerFilter numberOfItem;

    private StringFilter source;

    private ZonedDateTimeFilter createdAt;

    private StringFilter createdBy;

    private ZonedDateTimeFilter deletedAt;

    private StringFilter deletedBy;

    private UUIDFilter detailDeliveriesId;

    private Boolean distinct;

    public DeliveryCriteria() {}

    public DeliveryCriteria(DeliveryCriteria other) {
        this.deliveryId = other
            .optionalDeliveryId()
            .map(UUIDFilter::copy)
            .orElse(null);
        this.type = other.optionalType().map(StringFilter::copy).orElse(null);
        this.noticeNo = other
            .optionalNoticeNo()
            .map(StringFilter::copy)
            .orElse(null);
        this.noticeDate = other
            .optionalNoticeDate()
            .map(LocalDateFilter::copy)
            .orElse(null);
        this.supplierCode = other
            .optionalSupplierCode()
            .map(StringFilter::copy)
            .orElse(null);
        this.supplierName = other
            .optionalSupplierName()
            .map(StringFilter::copy)
            .orElse(null);
        this.factory = other
            .optionalFactory()
            .map(StringFilter::copy)
            .orElse(null);
        this.dateArrivalRangDong = other
            .optionalDateArrivalRangDong()
            .map(ZonedDateTimeFilter::copy)
            .orElse(null);
        this.vehicleNo = other
            .optionalVehicleNo()
            .map(StringFilter::copy)
            .orElse(null);
        this.numberOfPo = other
            .optionalNumberOfPo()
            .map(IntegerFilter::copy)
            .orElse(null);
        this.numberOfItem = other
            .optionalNumberOfItem()
            .map(IntegerFilter::copy)
            .orElse(null);
        this.source = other
            .optionalSource()
            .map(StringFilter::copy)
            .orElse(null);
        this.createdAt = other
            .optionalCreatedAt()
            .map(ZonedDateTimeFilter::copy)
            .orElse(null);
        this.createdBy = other
            .optionalCreatedBy()
            .map(StringFilter::copy)
            .orElse(null);
        this.deletedAt = other
            .optionalDeletedAt()
            .map(ZonedDateTimeFilter::copy)
            .orElse(null);
        this.deletedBy = other
            .optionalDeletedBy()
            .map(StringFilter::copy)
            .orElse(null);
        this.detailDeliveriesId = other
            .optionalDetailDeliveriesId()
            .map(UUIDFilter::copy)
            .orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DeliveryCriteria copy() {
        return new DeliveryCriteria(this);
    }

    public UUIDFilter getDeliveryId() {
        return deliveryId;
    }

    public Optional<UUIDFilter> optionalDeliveryId() {
        return Optional.ofNullable(deliveryId);
    }

    public UUIDFilter deliveryId() {
        if (deliveryId == null) {
            setDeliveryId(new UUIDFilter());
        }
        return deliveryId;
    }

    public void setDeliveryId(UUIDFilter deliveryId) {
        this.deliveryId = deliveryId;
    }

    public StringFilter getType() {
        return type;
    }

    public Optional<StringFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public StringFilter type() {
        if (type == null) {
            setType(new StringFilter());
        }
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
    }

    public StringFilter getNoticeNo() {
        return noticeNo;
    }

    public Optional<StringFilter> optionalNoticeNo() {
        return Optional.ofNullable(noticeNo);
    }

    public StringFilter noticeNo() {
        if (noticeNo == null) {
            setNoticeNo(new StringFilter());
        }
        return noticeNo;
    }

    public void setNoticeNo(StringFilter noticeNo) {
        this.noticeNo = noticeNo;
    }

    public LocalDateFilter getNoticeDate() {
        return noticeDate;
    }

    public Optional<LocalDateFilter> optionalNoticeDate() {
        return Optional.ofNullable(noticeDate);
    }

    public LocalDateFilter noticeDate() {
        if (noticeDate == null) {
            setNoticeDate(new LocalDateFilter());
        }
        return noticeDate;
    }

    public void setNoticeDate(LocalDateFilter noticeDate) {
        this.noticeDate = noticeDate;
    }

    public StringFilter getSupplierCode() {
        return supplierCode;
    }

    public Optional<StringFilter> optionalSupplierCode() {
        return Optional.ofNullable(supplierCode);
    }

    public StringFilter supplierCode() {
        if (supplierCode == null) {
            setSupplierCode(new StringFilter());
        }
        return supplierCode;
    }

    public void setSupplierCode(StringFilter supplierCode) {
        this.supplierCode = supplierCode;
    }

    public StringFilter getSupplierName() {
        return supplierName;
    }

    public Optional<StringFilter> optionalSupplierName() {
        return Optional.ofNullable(supplierName);
    }

    public StringFilter supplierName() {
        if (supplierName == null) {
            setSupplierName(new StringFilter());
        }
        return supplierName;
    }

    public void setSupplierName(StringFilter supplierName) {
        this.supplierName = supplierName;
    }

    public StringFilter getFactory() {
        return factory;
    }

    public Optional<StringFilter> optionalFactory() {
        return Optional.ofNullable(factory);
    }

    public StringFilter factory() {
        if (factory == null) {
            setFactory(new StringFilter());
        }
        return factory;
    }

    public void setFactory(StringFilter factory) {
        this.factory = factory;
    }

    public ZonedDateTimeFilter getDateArrivalRangDong() {
        return dateArrivalRangDong;
    }

    public Optional<ZonedDateTimeFilter> optionalDateArrivalRangDong() {
        return Optional.ofNullable(dateArrivalRangDong);
    }

    public ZonedDateTimeFilter dateArrivalRangDong() {
        if (dateArrivalRangDong == null) {
            setDateArrivalRangDong(new ZonedDateTimeFilter());
        }
        return dateArrivalRangDong;
    }

    public void setDateArrivalRangDong(
        ZonedDateTimeFilter dateArrivalRangDong
    ) {
        this.dateArrivalRangDong = dateArrivalRangDong;
    }

    public StringFilter getVehicleNo() {
        return vehicleNo;
    }

    public Optional<StringFilter> optionalVehicleNo() {
        return Optional.ofNullable(vehicleNo);
    }

    public StringFilter vehicleNo() {
        if (vehicleNo == null) {
            setVehicleNo(new StringFilter());
        }
        return vehicleNo;
    }

    public void setVehicleNo(StringFilter vehicleNo) {
        this.vehicleNo = vehicleNo;
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

    public UUIDFilter getDetailDeliveriesId() {
        return detailDeliveriesId;
    }

    public Optional<UUIDFilter> optionalDetailDeliveriesId() {
        return Optional.ofNullable(detailDeliveriesId);
    }

    public UUIDFilter detailDeliveriesId() {
        if (detailDeliveriesId == null) {
            setDetailDeliveriesId(new UUIDFilter());
        }
        return detailDeliveriesId;
    }

    public void setDetailDeliveriesId(UUIDFilter detailDeliveriesId) {
        this.detailDeliveriesId = detailDeliveriesId;
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
        final DeliveryCriteria that = (DeliveryCriteria) o;
        return (
            Objects.equals(deliveryId, that.deliveryId) &&
            Objects.equals(type, that.type) &&
            Objects.equals(noticeNo, that.noticeNo) &&
            Objects.equals(noticeDate, that.noticeDate) &&
            Objects.equals(supplierCode, that.supplierCode) &&
            Objects.equals(supplierName, that.supplierName) &&
            Objects.equals(factory, that.factory) &&
            Objects.equals(dateArrivalRangDong, that.dateArrivalRangDong) &&
            Objects.equals(vehicleNo, that.vehicleNo) &&
            Objects.equals(numberOfPo, that.numberOfPo) &&
            Objects.equals(numberOfItem, that.numberOfItem) &&
            Objects.equals(source, that.source) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(detailDeliveriesId, that.detailDeliveriesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            deliveryId,
            type,
            noticeNo,
            noticeDate,
            supplierCode,
            supplierName,
            factory,
            dateArrivalRangDong,
            vehicleNo,
            numberOfPo,
            numberOfItem,
            source,
            createdAt,
            createdBy,
            deletedAt,
            deletedBy,
            detailDeliveriesId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliveryCriteria{" +

            optionalDeliveryId().map(f -> "deliveryId=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalNoticeNo().map(f -> "noticeNo=" + f + ", ").orElse("") +
            optionalNoticeDate().map(f -> "noticeDate=" + f + ", ").orElse("") +
            optionalSupplierCode().map(f -> "supplierCode=" + f + ", ").orElse("") +
            optionalSupplierName().map(f -> "supplierName=" + f + ", ").orElse("") +
            optionalFactory().map(f -> "factory=" + f + ", ").orElse("") +
            optionalDateArrivalRangDong().map(f -> "dateArrivalRangDong=" + f + ", ").orElse("") +
            optionalVehicleNo().map(f -> "vehicleNo=" + f + ", ").orElse("") +
            optionalNumberOfPo().map(f -> "numberOfPo=" + f + ", ").orElse("") +
            optionalNumberOfItem().map(f -> "numberOfItem=" + f + ", ").orElse("") +
            optionalSource().map(f -> "source=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalDetailDeliveriesId().map(f -> "detailDeliveriesId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
