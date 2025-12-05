package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.WarehouseNoteInfoDetail}
 * entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseStampInfoDetailDTO implements Serializable {

    private Long id;

    @Size(max = 30)
    private String reelId;

    @Size(max = 50)
    private String partNumber;

    @Size(max = 20)
    private String vendor;

    @Size(max = 20)
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

    @Size(max = 20)
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

    private Long maLenhSanXuatId;

    private Long lenhSanXuatId;

    @Size(max = 50)
    private String trangThai;

    private Integer checked;

    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReelId() {
        return reelId;
    }

    public void setReelId(String reelId) {
        this.reelId = reelId;
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

    public Long getMaLenhSanXuatId() {
        return maLenhSanXuatId;
    }

    public void setMaLenhSanXuatId(Long maLenhSanXuatId) {
        this.maLenhSanXuatId = maLenhSanXuatId;
    }

    public Long getLenhSanXuatId() {
        return lenhSanXuatId;
    }

    public void setLenhSanXuatId(Long lenhSanXuatId) {
        this.lenhSanXuatId = lenhSanXuatId;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Integer getChecked() {
        return checked;
    }

    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WarehouseStampInfoDetailDTO)) {
            return false;
        }

        WarehouseStampInfoDetailDTO warehouseStampInfoDetailDTO =
            (WarehouseStampInfoDetailDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, warehouseStampInfoDetailDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WarehouseStampInfoDetailDTO{" +
                "id=" + getId() +
                ", reelId='" + getReelId() + "'" +
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
                ", maLenhSanXuatId=" + getMaLenhSanXuatId() +
                ", lenhSanXuatId=" + getLenhSanXuatId() +
                ", trangThai='" + getTrangThai() + "'" +
                ", checked=" + getChecked() +
                ", createdAt='" + getCreatedAt() + "'" +
                "}";
    }
}
