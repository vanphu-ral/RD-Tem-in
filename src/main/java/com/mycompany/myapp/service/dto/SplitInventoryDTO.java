package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.SplitInventory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SplitInventoryDTO implements Serializable {

    private Long id;

    private Long businessSourceId;

    @Size(max = 50)
    private String businessType;

    @Size(max = 50)
    private String reelId;

    @Size(max = 50)
    private String parentMaterialIdentifier;

    @Size(max = 50)
    private String partNumber;

    @Size(max = 20)
    private String vendor;

    @Size(max = 30)
    private String lot;

    @Size(max = 20)
    private String userData1;

    @Size(max = 255)
    private String userData2;

    @Size(max = 100)
    private String userData3;

    @Size(max = 20)
    private String userData4;

    @Size(max = 20)
    private String userData5;

    private Integer initialQuantity;

    @Size(max = 20)
    private String msdLevel;

    @Size(max = 20)
    private String msdInitialFloorTime;

    @Size(max = 20)
    private String msdBagSealDate;

    @Size(max = 20)
    private String marketUsage;

    private Integer quantityOverride;

    @Size(max = 20)
    private String shelfTime;

    @Size(max = 255)
    private String spMaterialName;

    @Size(max = 20)
    private String warningLimit;

    @Size(max = 20)
    private String maximumLimit;

    @Size(max = 50)
    private String comments;

    @Size(max = 20)
    private String warmupTime;

    @Size(max = 20)
    private String storageUnit;

    @Size(max = 20)
    private String subStorageUnit;

    @Size(max = 20)
    private String locationOverride;

    @Size(max = 20)
    private String expirationDate;

    @Size(max = 20)
    private String manufacturingDate;

    @Size(max = 20)
    private String partClass;

    @Size(max = 20)
    private String sapCode;

    @NotNull
    @Size(max = 510)
    private String vendorQrCode;

    @Size(max = 50)
    private String status;

    @Size(max = 20)
    private String createdBy;

    private Instant createdAt;

    @Size(max = 20)
    private String updatedBy;

    private Instant updatedAt;

    private Boolean printStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBusinessSourceId() {
        return businessSourceId;
    }

    public void setBusinessSourceId(Long businessSourceId) {
        this.businessSourceId = businessSourceId;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getReelId() {
        return reelId;
    }

    public void setReelId(String reelId) {
        this.reelId = reelId;
    }

    public String getParentMaterialIdentifier() {
        return parentMaterialIdentifier;
    }

    public void setParentMaterialIdentifier(String parentMaterialIdentifier) {
        this.parentMaterialIdentifier = parentMaterialIdentifier;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getUserData1() {
        return userData1;
    }

    public void setUserData1(String userData1) {
        this.userData1 = userData1;
    }

    public String getUserData2() {
        return userData2;
    }

    public void setUserData2(String userData2) {
        this.userData2 = userData2;
    }

    public String getUserData3() {
        return userData3;
    }

    public void setUserData3(String userData3) {
        this.userData3 = userData3;
    }

    public String getUserData4() {
        return userData4;
    }

    public void setUserData4(String userData4) {
        this.userData4 = userData4;
    }

    public String getUserData5() {
        return userData5;
    }

    public void setUserData5(String userData5) {
        this.userData5 = userData5;
    }

    public Integer getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public String getMsdLevel() {
        return msdLevel;
    }

    public void setMsdLevel(String msdLevel) {
        this.msdLevel = msdLevel;
    }

    public String getMsdInitialFloorTime() {
        return msdInitialFloorTime;
    }

    public void setMsdInitialFloorTime(String msdInitialFloorTime) {
        this.msdInitialFloorTime = msdInitialFloorTime;
    }

    public String getMsdBagSealDate() {
        return msdBagSealDate;
    }

    public void setMsdBagSealDate(String msdBagSealDate) {
        this.msdBagSealDate = msdBagSealDate;
    }

    public String getMarketUsage() {
        return marketUsage;
    }

    public void setMarketUsage(String marketUsage) {
        this.marketUsage = marketUsage;
    }

    public Integer getQuantityOverride() {
        return quantityOverride;
    }

    public void setQuantityOverride(Integer quantityOverride) {
        this.quantityOverride = quantityOverride;
    }

    public String getShelfTime() {
        return shelfTime;
    }

    public void setShelfTime(String shelfTime) {
        this.shelfTime = shelfTime;
    }

    public String getSpMaterialName() {
        return spMaterialName;
    }

    public void setSpMaterialName(String spMaterialName) {
        this.spMaterialName = spMaterialName;
    }

    public String getWarningLimit() {
        return warningLimit;
    }

    public void setWarningLimit(String warningLimit) {
        this.warningLimit = warningLimit;
    }

    public String getMaximumLimit() {
        return maximumLimit;
    }

    public void setMaximumLimit(String maximumLimit) {
        this.maximumLimit = maximumLimit;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getWarmupTime() {
        return warmupTime;
    }

    public void setWarmupTime(String warmupTime) {
        this.warmupTime = warmupTime;
    }

    public String getStorageUnit() {
        return storageUnit;
    }

    public void setStorageUnit(String storageUnit) {
        this.storageUnit = storageUnit;
    }

    public String getSubStorageUnit() {
        return subStorageUnit;
    }

    public void setSubStorageUnit(String subStorageUnit) {
        this.subStorageUnit = subStorageUnit;
    }

    public String getLocationOverride() {
        return locationOverride;
    }

    public void setLocationOverride(String locationOverride) {
        this.locationOverride = locationOverride;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public String getPartClass() {
        return partClass;
    }

    public void setPartClass(String partClass) {
        this.partClass = partClass;
    }

    public String getSapCode() {
        return sapCode;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public String getVendorQrCode() {
        return vendorQrCode;
    }

    public void setVendorQrCode(String vendorQrCode) {
        this.vendorQrCode = vendorQrCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(Boolean printStatus) {
        this.printStatus = printStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SplitInventoryDTO)) {
            return false;
        }

        SplitInventoryDTO splitInventoryDTO = (SplitInventoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, splitInventoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SplitInventoryDTO{" +
            "id=" + getId() +
            ", businessSourceId=" + getBusinessSourceId() +
            ", businessType='" + getBusinessType() + "'" +
            ", reelId='" + getReelId() + "'" +
            ", parentMaterialIdentifier='" + getParentMaterialIdentifier() + "'" +
            ", partNumber='" + getPartNumber() + "'" +
            ", vendor='" + getVendor() + "'" +
            ", lot='" + getLot() + "'" +
            ", userData1='" + getUserData1() + "'" +
            ", userData2='" + getUserData2() + "'" +
            ", userData3='" + getUserData3() + "'" +
            ", userData4='" + getUserData4() + "'" +
            ", userData5='" + getUserData5() + "'" +
            ", initialQuantity=" + getInitialQuantity() +
            ", msdLevel='" + getMsdLevel() + "'" +
            ", msdInitialFloorTime='" + getMsdInitialFloorTime() + "'" +
            ", msdBagSealDate='" + getMsdBagSealDate() + "'" +
            ", marketUsage='" + getMarketUsage() + "'" +
            ", quantityOverride=" + getQuantityOverride() +
            ", shelfTime='" + getShelfTime() + "'" +
            ", spMaterialName='" + getSpMaterialName() + "'" +
            ", warningLimit='" + getWarningLimit() + "'" +
            ", maximumLimit='" + getMaximumLimit() + "'" +
            ", comments='" + getComments() + "'" +
            ", warmupTime='" + getWarmupTime() + "'" +
            ", storageUnit='" + getStorageUnit() + "'" +
            ", subStorageUnit='" + getSubStorageUnit() + "'" +
            ", locationOverride='" + getLocationOverride() + "'" +
            ", expirationDate='" + getExpirationDate() + "'" +
            ", manufacturingDate='" + getManufacturingDate() + "'" +
            ", partClass='" + getPartClass() + "'" +
            ", sapCode='" + getSapCode() + "'" +
            ", vendorQrCode='" + getVendorQrCode() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", printStatus='" + getPrintStatus() + "'" +
            "}";
    }
}
