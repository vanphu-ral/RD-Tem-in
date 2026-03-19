package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
// import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.VendorTemDetail}
 * entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.VendorTemDetailResource} to receive
 * all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /vendor-tem-details?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific
 * {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
// @ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VendorTemDetailCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reelId;

    private StringFilter partNumber;

    private StringFilter vendor;

    private StringFilter lot;

    private StringFilter userData1;

    private StringFilter userData2;

    private StringFilter userData3;

    private StringFilter userData4;

    private StringFilter userData5;

    private IntegerFilter initialQuantity;

    private StringFilter msdLevel;

    private StringFilter msdInitialFloorTime;

    private StringFilter msdBagSealDate;

    private StringFilter marketUsage;

    private IntegerFilter quantityOverride;

    private StringFilter shelfTime;

    private StringFilter spMaterialName;

    private StringFilter warningLimit;

    private StringFilter maximumLimit;

    private StringFilter comments;

    private StringFilter warmupTime;

    private StringFilter storageUnit;

    private StringFilter subStorageUnit;

    private StringFilter locationOverride;

    private StringFilter expirationDate;

    private StringFilter manufacturingDate;

    private StringFilter partClass;

    private StringFilter sapCode;

    private StringFilter vendorAdditionalData;

    private StringFilter vendorQrCode;

    private StringFilter status;

    private StringFilter createdBy;

    private ZonedDateTimeFilter createdAt;

    private StringFilter updatedBy;

    private ZonedDateTimeFilter updatedAt;

    private LongFilter poDetailId;

    private Boolean distinct;

    public VendorTemDetailCriteria() {}

    public VendorTemDetailCriteria(VendorTemDetailCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reelId = other
            .optionalReelId()
            .map(StringFilter::copy)
            .orElse(null);
        this.partNumber = other
            .optionalPartNumber()
            .map(StringFilter::copy)
            .orElse(null);
        this.vendor = other
            .optionalVendor()
            .map(StringFilter::copy)
            .orElse(null);
        this.lot = other.optionalLot().map(StringFilter::copy).orElse(null);
        this.userData1 = other
            .optionalUserData1()
            .map(StringFilter::copy)
            .orElse(null);
        this.userData2 = other
            .optionalUserData2()
            .map(StringFilter::copy)
            .orElse(null);
        this.userData3 = other
            .optionalUserData3()
            .map(StringFilter::copy)
            .orElse(null);
        this.userData4 = other
            .optionalUserData4()
            .map(StringFilter::copy)
            .orElse(null);
        this.userData5 = other
            .optionalUserData5()
            .map(StringFilter::copy)
            .orElse(null);
        this.initialQuantity = other
            .optionalInitialQuantity()
            .map(IntegerFilter::copy)
            .orElse(null);
        this.msdLevel = other
            .optionalMsdLevel()
            .map(StringFilter::copy)
            .orElse(null);
        this.msdInitialFloorTime = other
            .optionalMsdInitialFloorTime()
            .map(StringFilter::copy)
            .orElse(null);
        this.msdBagSealDate = other
            .optionalMsdBagSealDate()
            .map(StringFilter::copy)
            .orElse(null);
        this.marketUsage = other
            .optionalMarketUsage()
            .map(StringFilter::copy)
            .orElse(null);
        this.quantityOverride = other
            .optionalQuantityOverride()
            .map(IntegerFilter::copy)
            .orElse(null);
        this.shelfTime = other
            .optionalShelfTime()
            .map(StringFilter::copy)
            .orElse(null);
        this.spMaterialName = other
            .optionalSpMaterialName()
            .map(StringFilter::copy)
            .orElse(null);
        this.warningLimit = other
            .optionalWarningLimit()
            .map(StringFilter::copy)
            .orElse(null);
        this.maximumLimit = other
            .optionalMaximumLimit()
            .map(StringFilter::copy)
            .orElse(null);
        this.comments = other
            .optionalComments()
            .map(StringFilter::copy)
            .orElse(null);
        this.warmupTime = other
            .optionalWarmupTime()
            .map(StringFilter::copy)
            .orElse(null);
        this.storageUnit = other
            .optionalStorageUnit()
            .map(StringFilter::copy)
            .orElse(null);
        this.subStorageUnit = other
            .optionalSubStorageUnit()
            .map(StringFilter::copy)
            .orElse(null);
        this.locationOverride = other
            .optionalLocationOverride()
            .map(StringFilter::copy)
            .orElse(null);
        this.expirationDate = other
            .optionalExpirationDate()
            .map(StringFilter::copy)
            .orElse(null);
        this.manufacturingDate = other
            .optionalManufacturingDate()
            .map(StringFilter::copy)
            .orElse(null);
        this.partClass = other
            .optionalPartClass()
            .map(StringFilter::copy)
            .orElse(null);
        this.sapCode = other
            .optionalSapCode()
            .map(StringFilter::copy)
            .orElse(null);
        this.vendorAdditionalData = other
            .optionalVendorAdditionalData()
            .map(StringFilter::copy)
            .orElse(null);
        this.vendorQrCode = other
            .optionalVendorQrCode()
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
        this.poDetailId = other
            .optionalPoDetailId()
            .map(LongFilter::copy)
            .orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public VendorTemDetailCriteria copy() {
        return new VendorTemDetailCriteria(this);
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

    public StringFilter getReelId() {
        return reelId;
    }

    public Optional<StringFilter> optionalReelId() {
        return Optional.ofNullable(reelId);
    }

    public StringFilter reelId() {
        if (reelId == null) {
            setReelId(new StringFilter());
        }
        return reelId;
    }

    public void setReelId(StringFilter reelId) {
        this.reelId = reelId;
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

    public StringFilter getVendor() {
        return vendor;
    }

    public Optional<StringFilter> optionalVendor() {
        return Optional.ofNullable(vendor);
    }

    public StringFilter vendor() {
        if (vendor == null) {
            setVendor(new StringFilter());
        }
        return vendor;
    }

    public void setVendor(StringFilter vendor) {
        this.vendor = vendor;
    }

    public StringFilter getLot() {
        return lot;
    }

    public Optional<StringFilter> optionalLot() {
        return Optional.ofNullable(lot);
    }

    public StringFilter lot() {
        if (lot == null) {
            setLot(new StringFilter());
        }
        return lot;
    }

    public void setLot(StringFilter lot) {
        this.lot = lot;
    }

    public StringFilter getUserData1() {
        return userData1;
    }

    public Optional<StringFilter> optionalUserData1() {
        return Optional.ofNullable(userData1);
    }

    public StringFilter userData1() {
        if (userData1 == null) {
            setUserData1(new StringFilter());
        }
        return userData1;
    }

    public void setUserData1(StringFilter userData1) {
        this.userData1 = userData1;
    }

    public StringFilter getUserData2() {
        return userData2;
    }

    public Optional<StringFilter> optionalUserData2() {
        return Optional.ofNullable(userData2);
    }

    public StringFilter userData2() {
        if (userData2 == null) {
            setUserData2(new StringFilter());
        }
        return userData2;
    }

    public void setUserData2(StringFilter userData2) {
        this.userData2 = userData2;
    }

    public StringFilter getUserData3() {
        return userData3;
    }

    public Optional<StringFilter> optionalUserData3() {
        return Optional.ofNullable(userData3);
    }

    public StringFilter userData3() {
        if (userData3 == null) {
            setUserData3(new StringFilter());
        }
        return userData3;
    }

    public void setUserData3(StringFilter userData3) {
        this.userData3 = userData3;
    }

    public StringFilter getUserData4() {
        return userData4;
    }

    public Optional<StringFilter> optionalUserData4() {
        return Optional.ofNullable(userData4);
    }

    public StringFilter userData4() {
        if (userData4 == null) {
            setUserData4(new StringFilter());
        }
        return userData4;
    }

    public void setUserData4(StringFilter userData4) {
        this.userData4 = userData4;
    }

    public StringFilter getUserData5() {
        return userData5;
    }

    public Optional<StringFilter> optionalUserData5() {
        return Optional.ofNullable(userData5);
    }

    public StringFilter userData5() {
        if (userData5 == null) {
            setUserData5(new StringFilter());
        }
        return userData5;
    }

    public void setUserData5(StringFilter userData5) {
        this.userData5 = userData5;
    }

    public IntegerFilter getInitialQuantity() {
        return initialQuantity;
    }

    public Optional<IntegerFilter> optionalInitialQuantity() {
        return Optional.ofNullable(initialQuantity);
    }

    public IntegerFilter initialQuantity() {
        if (initialQuantity == null) {
            setInitialQuantity(new IntegerFilter());
        }
        return initialQuantity;
    }

    public void setInitialQuantity(IntegerFilter initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public StringFilter getMsdLevel() {
        return msdLevel;
    }

    public Optional<StringFilter> optionalMsdLevel() {
        return Optional.ofNullable(msdLevel);
    }

    public StringFilter msdLevel() {
        if (msdLevel == null) {
            setMsdLevel(new StringFilter());
        }
        return msdLevel;
    }

    public void setMsdLevel(StringFilter msdLevel) {
        this.msdLevel = msdLevel;
    }

    public StringFilter getMsdInitialFloorTime() {
        return msdInitialFloorTime;
    }

    public Optional<StringFilter> optionalMsdInitialFloorTime() {
        return Optional.ofNullable(msdInitialFloorTime);
    }

    public StringFilter msdInitialFloorTime() {
        if (msdInitialFloorTime == null) {
            setMsdInitialFloorTime(new StringFilter());
        }
        return msdInitialFloorTime;
    }

    public void setMsdInitialFloorTime(StringFilter msdInitialFloorTime) {
        this.msdInitialFloorTime = msdInitialFloorTime;
    }

    public StringFilter getMsdBagSealDate() {
        return msdBagSealDate;
    }

    public Optional<StringFilter> optionalMsdBagSealDate() {
        return Optional.ofNullable(msdBagSealDate);
    }

    public StringFilter msdBagSealDate() {
        if (msdBagSealDate == null) {
            setMsdBagSealDate(new StringFilter());
        }
        return msdBagSealDate;
    }

    public void setMsdBagSealDate(StringFilter msdBagSealDate) {
        this.msdBagSealDate = msdBagSealDate;
    }

    public StringFilter getMarketUsage() {
        return marketUsage;
    }

    public Optional<StringFilter> optionalMarketUsage() {
        return Optional.ofNullable(marketUsage);
    }

    public StringFilter marketUsage() {
        if (marketUsage == null) {
            setMarketUsage(new StringFilter());
        }
        return marketUsage;
    }

    public void setMarketUsage(StringFilter marketUsage) {
        this.marketUsage = marketUsage;
    }

    public IntegerFilter getQuantityOverride() {
        return quantityOverride;
    }

    public Optional<IntegerFilter> optionalQuantityOverride() {
        return Optional.ofNullable(quantityOverride);
    }

    public IntegerFilter quantityOverride() {
        if (quantityOverride == null) {
            setQuantityOverride(new IntegerFilter());
        }
        return quantityOverride;
    }

    public void setQuantityOverride(IntegerFilter quantityOverride) {
        this.quantityOverride = quantityOverride;
    }

    public StringFilter getShelfTime() {
        return shelfTime;
    }

    public Optional<StringFilter> optionalShelfTime() {
        return Optional.ofNullable(shelfTime);
    }

    public StringFilter shelfTime() {
        if (shelfTime == null) {
            setShelfTime(new StringFilter());
        }
        return shelfTime;
    }

    public void setShelfTime(StringFilter shelfTime) {
        this.shelfTime = shelfTime;
    }

    public StringFilter getSpMaterialName() {
        return spMaterialName;
    }

    public Optional<StringFilter> optionalSpMaterialName() {
        return Optional.ofNullable(spMaterialName);
    }

    public StringFilter spMaterialName() {
        if (spMaterialName == null) {
            setSpMaterialName(new StringFilter());
        }
        return spMaterialName;
    }

    public void setSpMaterialName(StringFilter spMaterialName) {
        this.spMaterialName = spMaterialName;
    }

    public StringFilter getWarningLimit() {
        return warningLimit;
    }

    public Optional<StringFilter> optionalWarningLimit() {
        return Optional.ofNullable(warningLimit);
    }

    public StringFilter warningLimit() {
        if (warningLimit == null) {
            setWarningLimit(new StringFilter());
        }
        return warningLimit;
    }

    public void setWarningLimit(StringFilter warningLimit) {
        this.warningLimit = warningLimit;
    }

    public StringFilter getMaximumLimit() {
        return maximumLimit;
    }

    public Optional<StringFilter> optionalMaximumLimit() {
        return Optional.ofNullable(maximumLimit);
    }

    public StringFilter maximumLimit() {
        if (maximumLimit == null) {
            setMaximumLimit(new StringFilter());
        }
        return maximumLimit;
    }

    public void setMaximumLimit(StringFilter maximumLimit) {
        this.maximumLimit = maximumLimit;
    }

    public StringFilter getComments() {
        return comments;
    }

    public Optional<StringFilter> optionalComments() {
        return Optional.ofNullable(comments);
    }

    public StringFilter comments() {
        if (comments == null) {
            setComments(new StringFilter());
        }
        return comments;
    }

    public void setComments(StringFilter comments) {
        this.comments = comments;
    }

    public StringFilter getWarmupTime() {
        return warmupTime;
    }

    public Optional<StringFilter> optionalWarmupTime() {
        return Optional.ofNullable(warmupTime);
    }

    public StringFilter warmupTime() {
        if (warmupTime == null) {
            setWarmupTime(new StringFilter());
        }
        return warmupTime;
    }

    public void setWarmupTime(StringFilter warmupTime) {
        this.warmupTime = warmupTime;
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

    public StringFilter getSubStorageUnit() {
        return subStorageUnit;
    }

    public Optional<StringFilter> optionalSubStorageUnit() {
        return Optional.ofNullable(subStorageUnit);
    }

    public StringFilter subStorageUnit() {
        if (subStorageUnit == null) {
            setSubStorageUnit(new StringFilter());
        }
        return subStorageUnit;
    }

    public void setSubStorageUnit(StringFilter subStorageUnit) {
        this.subStorageUnit = subStorageUnit;
    }

    public StringFilter getLocationOverride() {
        return locationOverride;
    }

    public Optional<StringFilter> optionalLocationOverride() {
        return Optional.ofNullable(locationOverride);
    }

    public StringFilter locationOverride() {
        if (locationOverride == null) {
            setLocationOverride(new StringFilter());
        }
        return locationOverride;
    }

    public void setLocationOverride(StringFilter locationOverride) {
        this.locationOverride = locationOverride;
    }

    public StringFilter getExpirationDate() {
        return expirationDate;
    }

    public Optional<StringFilter> optionalExpirationDate() {
        return Optional.ofNullable(expirationDate);
    }

    public StringFilter expirationDate() {
        if (expirationDate == null) {
            setExpirationDate(new StringFilter());
        }
        return expirationDate;
    }

    public void setExpirationDate(StringFilter expirationDate) {
        this.expirationDate = expirationDate;
    }

    public StringFilter getManufacturingDate() {
        return manufacturingDate;
    }

    public Optional<StringFilter> optionalManufacturingDate() {
        return Optional.ofNullable(manufacturingDate);
    }

    public StringFilter manufacturingDate() {
        if (manufacturingDate == null) {
            setManufacturingDate(new StringFilter());
        }
        return manufacturingDate;
    }

    public void setManufacturingDate(StringFilter manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public StringFilter getPartClass() {
        return partClass;
    }

    public Optional<StringFilter> optionalPartClass() {
        return Optional.ofNullable(partClass);
    }

    public StringFilter partClass() {
        if (partClass == null) {
            setPartClass(new StringFilter());
        }
        return partClass;
    }

    public void setPartClass(StringFilter partClass) {
        this.partClass = partClass;
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

    public StringFilter getVendorAdditionalData() {
        return vendorAdditionalData;
    }

    public Optional<StringFilter> optionalVendorAdditionalData() {
        return Optional.ofNullable(vendorAdditionalData);
    }

    public StringFilter vendorAdditionalData() {
        if (vendorAdditionalData == null) {
            setVendorAdditionalData(new StringFilter());
        }
        return vendorAdditionalData;
    }

    public void setVendorAdditionalData(StringFilter vendorAdditionalData) {
        this.vendorAdditionalData = vendorAdditionalData;
    }

    public StringFilter getVendorQrCode() {
        return vendorQrCode;
    }

    public Optional<StringFilter> optionalVendorQrCode() {
        return Optional.ofNullable(vendorQrCode);
    }

    public StringFilter vendorQrCode() {
        if (vendorQrCode == null) {
            setVendorQrCode(new StringFilter());
        }
        return vendorQrCode;
    }

    public void setVendorQrCode(StringFilter vendorQrCode) {
        this.vendorQrCode = vendorQrCode;
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
        final VendorTemDetailCriteria that = (VendorTemDetailCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reelId, that.reelId) &&
            Objects.equals(partNumber, that.partNumber) &&
            Objects.equals(vendor, that.vendor) &&
            Objects.equals(lot, that.lot) &&
            Objects.equals(userData1, that.userData1) &&
            Objects.equals(userData2, that.userData2) &&
            Objects.equals(userData3, that.userData3) &&
            Objects.equals(userData4, that.userData4) &&
            Objects.equals(userData5, that.userData5) &&
            Objects.equals(initialQuantity, that.initialQuantity) &&
            Objects.equals(msdLevel, that.msdLevel) &&
            Objects.equals(msdInitialFloorTime, that.msdInitialFloorTime) &&
            Objects.equals(msdBagSealDate, that.msdBagSealDate) &&
            Objects.equals(marketUsage, that.marketUsage) &&
            Objects.equals(quantityOverride, that.quantityOverride) &&
            Objects.equals(shelfTime, that.shelfTime) &&
            Objects.equals(spMaterialName, that.spMaterialName) &&
            Objects.equals(warningLimit, that.warningLimit) &&
            Objects.equals(maximumLimit, that.maximumLimit) &&
            Objects.equals(comments, that.comments) &&
            Objects.equals(warmupTime, that.warmupTime) &&
            Objects.equals(storageUnit, that.storageUnit) &&
            Objects.equals(subStorageUnit, that.subStorageUnit) &&
            Objects.equals(locationOverride, that.locationOverride) &&
            Objects.equals(expirationDate, that.expirationDate) &&
            Objects.equals(manufacturingDate, that.manufacturingDate) &&
            Objects.equals(partClass, that.partClass) &&
            Objects.equals(sapCode, that.sapCode) &&
            Objects.equals(vendorAdditionalData, that.vendorAdditionalData) &&
            Objects.equals(vendorQrCode, that.vendorQrCode) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedBy, that.updatedBy) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(poDetailId, that.poDetailId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            reelId,
            partNumber,
            vendor,
            lot,
            userData1,
            userData2,
            userData3,
            userData4,
            userData5,
            initialQuantity,
            msdLevel,
            msdInitialFloorTime,
            msdBagSealDate,
            marketUsage,
            quantityOverride,
            shelfTime,
            spMaterialName,
            warningLimit,
            maximumLimit,
            comments,
            warmupTime,
            storageUnit,
            subStorageUnit,
            locationOverride,
            expirationDate,
            manufacturingDate,
            partClass,
            sapCode,
            vendorQrCode,
            status,
            createdBy,
            createdAt,
            updatedBy,
            updatedAt,
            poDetailId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VendorTemDetailCriteria{" +
                optionalId().map(f -> "id=" + f + ", ").orElse("") +
                optionalReelId().map(f -> "reelId=" + f + ", ").orElse("") +
                optionalPartNumber().map(f -> "partNumber=" + f + ", ").orElse("") +
                optionalVendor().map(f -> "vendor=" + f + ", ").orElse("") +
                optionalLot().map(f -> "lot=" + f + ", ").orElse("") +
                optionalUserData1().map(f -> "userData1=" + f + ", ").orElse("") +
                optionalUserData2().map(f -> "userData2=" + f + ", ").orElse("") +
                optionalUserData3().map(f -> "userData3=" + f + ", ").orElse("") +
                optionalUserData4().map(f -> "userData4=" + f + ", ").orElse("") +
                optionalUserData5().map(f -> "userData5=" + f + ", ").orElse("") +
                optionalInitialQuantity().map(f -> "initialQuantity=" + f + ", ").orElse("") +
                optionalMsdLevel().map(f -> "msdLevel=" + f + ", ").orElse("") +
                optionalMsdInitialFloorTime().map(f -> "msdInitialFloorTime=" + f + ", ").orElse("") +
                optionalMsdBagSealDate().map(f -> "msdBagSealDate=" + f + ", ").orElse("") +
                optionalMarketUsage().map(f -> "marketUsage=" + f + ", ").orElse("") +
                optionalQuantityOverride().map(f -> "quantityOverride=" + f + ", ").orElse("") +
                optionalShelfTime().map(f -> "shelfTime=" + f + ", ").orElse("") +
                optionalSpMaterialName().map(f -> "spMaterialName=" + f + ", ").orElse("") +
                optionalWarningLimit().map(f -> "warningLimit=" + f + ", ").orElse("") +
                optionalMaximumLimit().map(f -> "maximumLimit=" + f + ", ").orElse("") +
                optionalComments().map(f -> "comments=" + f + ", ").orElse("") +
                optionalWarmupTime().map(f -> "warmupTime=" + f + ", ").orElse("") +
                optionalStorageUnit().map(f -> "storageUnit=" + f + ", ").orElse("") +
                optionalSubStorageUnit().map(f -> "subStorageUnit=" + f + ", ").orElse("") +
                optionalLocationOverride().map(f -> "locationOverride=" + f + ", ").orElse("") +
                optionalExpirationDate().map(f -> "expirationDate=" + f + ", ").orElse("") +
                optionalManufacturingDate().map(f -> "manufacturingDate=" + f + ", ").orElse("") +
                optionalPartClass().map(f -> "partClass=" + f + ", ").orElse("") +
                optionalSapCode().map(f -> "sapCode=" + f + ", ").orElse("") +
                optionalVendorAdditionalData().map(f -> "vendorAdditionalData=" + f + ", ").orElse("") +
                optionalVendorQrCode().map(f -> "vendorQrCode=" + f + ", ").orElse("") +
                optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
                optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
                optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
                optionalUpdatedBy().map(f -> "updatedBy=" + f + ", ").orElse("") +
                optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
                optionalPoDetailId().map(f -> "poDetailId=" + f + ", ").orElse("") +
                optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
                "}";
    }
}
