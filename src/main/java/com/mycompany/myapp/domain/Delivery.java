package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Delivery.
 */
@Entity
@Table(name = "delivery")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Delivery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "delivery_id", nullable = false)
    private UUID deliveryId;

    @Size(max = 50)
    @Column(name = "type", length = 50)
    private String type;

    @Size(max = 50)
    @Column(name = "notice_no", length = 50)
    private String noticeNo;

    @Column(name = "notice_date")
    private LocalDate noticeDate;

    @Size(max = 50)
    @Column(name = "supplier_code", length = 50)
    private String supplierCode;

    @Size(max = 255)
    @Column(name = "supplier_name", length = 255)
    private String supplierName;

    @Size(max = 50)
    @Column(name = "factory", length = 50)
    private String factory;

    @Column(name = "date_arrival_rang_dong")
    private ZonedDateTime dateArrivalRangDong;

    @Size(max = 50)
    @Column(name = "vehicle_no", length = 50)
    private String vehicleNo;

    @Column(name = "number_of_po")
    private Integer numberOfPo;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Size(max = 50)
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;

    @Size(max = 50)
    @Column(name = "deleted_by", length = 50)
    private String deletedBy;

    @Size(max = 50)
    @Column(name = "source", length = 50)
    private String source;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "delivery")
    @JsonIgnoreProperties(value = { "delivery" }, allowSetters = true)
    private Set<DetailDelivery> detailDeliveries = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getDeliveryId() {
        return this.deliveryId;
    }

    public Delivery deliveryId(UUID deliveryId) {
        this.setDeliveryId(deliveryId);
        return this;
    }

    public void setDeliveryId(UUID deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getType() {
        return this.type;
    }

    public Delivery type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNoticeNo() {
        return this.noticeNo;
    }

    public Delivery noticeNo(String noticeNo) {
        this.setNoticeNo(noticeNo);
        return this;
    }

    public void setNoticeNo(String noticeNo) {
        this.noticeNo = noticeNo;
    }

    public LocalDate getNoticeDate() {
        return this.noticeDate;
    }

    public Delivery noticeDate(LocalDate noticeDate) {
        this.setNoticeDate(noticeDate);
        return this;
    }

    public void setNoticeDate(LocalDate noticeDate) {
        this.noticeDate = noticeDate;
    }

    public String getSupplierCode() {
        return this.supplierCode;
    }

    public Delivery supplierCode(String supplierCode) {
        this.setSupplierCode(supplierCode);
        return this;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return this.supplierName;
    }

    public Delivery supplierName(String supplierName) {
        this.setSupplierName(supplierName);
        return this;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getFactory() {
        return this.factory;
    }

    public Delivery factory(String factory) {
        this.setFactory(factory);
        return this;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public ZonedDateTime getDateArrivalRangDong() {
        return this.dateArrivalRangDong;
    }

    public Delivery dateArrivalRangDong(ZonedDateTime dateArrivalRangDong) {
        this.setDateArrivalRangDong(dateArrivalRangDong);
        return this;
    }

    public void setDateArrivalRangDong(ZonedDateTime dateArrivalRangDong) {
        this.dateArrivalRangDong = dateArrivalRangDong;
    }

    public String getVehicleNo() {
        return this.vehicleNo;
    }

    public Delivery vehicleNo(String vehicleNo) {
        this.setVehicleNo(vehicleNo);
        return this;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public Integer getNumberOfPo() {
        return this.numberOfPo;
    }

    public Delivery numberOfPo(Integer numberOfPo) {
        this.setNumberOfPo(numberOfPo);
        return this;
    }

    public void setNumberOfPo(Integer numberOfPo) {
        this.numberOfPo = numberOfPo;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Delivery createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Delivery createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getDeletedAt() {
        return this.deletedAt;
    }

    public Delivery deletedAt(ZonedDateTime deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(ZonedDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedBy() {
        return this.deletedBy;
    }

    public Delivery deletedBy(String deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public String getSource() {
        return this.source;
    }

    public Delivery source(String source) {
        this.setSource(source);
        return this;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Set<DetailDelivery> getDetailDeliveries() {
        return this.detailDeliveries;
    }

    public void setDetailDeliveries(Set<DetailDelivery> detailDeliveries) {
        if (this.detailDeliveries != null) {
            this.detailDeliveries.forEach(i -> i.setDelivery(null));
        }
        if (detailDeliveries != null) {
            detailDeliveries.forEach(i -> i.setDelivery(this));
        }
        this.detailDeliveries = detailDeliveries;
    }

    public Delivery detailDeliveries(Set<DetailDelivery> detailDeliveries) {
        this.setDetailDeliveries(detailDeliveries);
        return this;
    }

    public Delivery addDetailDeliveries(DetailDelivery detailDelivery) {
        this.detailDeliveries.add(detailDelivery);
        detailDelivery.setDelivery(this);
        return this;
    }

    public Delivery removeDetailDeliveries(DetailDelivery detailDelivery) {
        this.detailDeliveries.remove(detailDelivery);
        detailDelivery.setDelivery(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Delivery)) {
            return false;
        }
        Delivery delivery = (Delivery) o;
        return Objects.equals(this.deliveryId, delivery.deliveryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.deliveryId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Delivery{" +
            "deliveryId='" + getDeliveryId() + "'" +
            ", type='" + getType() + "'" +
            ", noticeNo='" + getNoticeNo() + "'" +
            ", noticeDate='" + getNoticeDate() + "'" +
            ", supplierCode='" + getSupplierCode() + "'" +
            ", supplierName='" + getSupplierName() + "'" +
            ", factory='" + getFactory() + "'" +
            ", dateArrivalRangDong='" + getDateArrivalRangDong() + "'" +
            ", vehicleNo='" + getVehicleNo() + "'" +
            ", numberOfPo=" + getNumberOfPo() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            "}";
    }
}
