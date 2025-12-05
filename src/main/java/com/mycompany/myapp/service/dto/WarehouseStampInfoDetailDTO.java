package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("reel_id")
    public String getReelId() {
        return reelId;
    }

    @JsonProperty("reel_id")
    public void setReelId(String reelId) {
        this.reelId = reelId;
    }

    @JsonProperty("part_number")
    public String getPartNumber() {
        return partNumber;
    }

    @JsonProperty("part_number")
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    @JsonProperty("vendor")
    public String getVendor() {
        return vendor;
    }

    @JsonProperty("vendor")
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @JsonProperty("lot")
    public String getLot() {
        return lot;
    }

    @JsonProperty("lot")
    public void setLot(String lot) {
        this.lot = lot;
    }

    @JsonProperty("user_data_1")
    public String getUserData1() {
        return userData1;
    }

    @JsonProperty("user_data_1")
    public void setUserData1(String userData1) {
        this.userData1 = userData1;
    }

    @JsonProperty("user_data_2")
    public String getUserData2() {
        return userData2;
    }

    @JsonProperty("user_data_2")
    public void setUserData2(String userData2) {
        this.userData2 = userData2;
    }

    @JsonProperty("user_data_3")
    public String getUserData3() {
        return userData3;
    }

    @JsonProperty("user_data_3")
    public void setUserData3(String userData3) {
        this.userData3 = userData3;
    }

    @JsonProperty("user_data_4")
    public String getUserData4() {
        return userData4;
    }

    @JsonProperty("user_data_4")
    public void setUserData4(String userData4) {
        this.userData4 = userData4;
    }

    @JsonProperty("user_data_5")
    public String getUserData5() {
        return userData5;
    }

    @JsonProperty("user_data_5")
    public void setUserData5(String userData5) {
        this.userData5 = userData5;
    }

    @JsonProperty("initial_quantity")
    public Integer getInitialQuantity() {
        return initialQuantity;
    }

    @JsonProperty("initial_quantity")
    public void setInitialQuantity(Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    @JsonProperty("msd_level")
    public String getMsdLevel() {
        return msdLevel;
    }

    @JsonProperty("msd_level")
    public void setMsdLevel(String msdLevel) {
        this.msdLevel = msdLevel;
    }

    @JsonProperty("msd_initial_floor_time")
    public String getMsdInitialFloorTime() {
        return msdInitialFloorTime;
    }

    @JsonProperty("msd_initial_floor_time")
    public void setMsdInitialFloorTime(String msdInitialFloorTime) {
        this.msdInitialFloorTime = msdInitialFloorTime;
    }

    @JsonProperty("msd_bag_seal_date")
    public String getMsdBagSealDate() {
        return msdBagSealDate;
    }

    @JsonProperty("msd_bag_seal_date")
    public void setMsdBagSealDate(String msdBagSealDate) {
        this.msdBagSealDate = msdBagSealDate;
    }

    @JsonProperty("market_usage")
    public String getMarketUsage() {
        return marketUsage;
    }

    @JsonProperty("market_usage")
    public void setMarketUsage(String marketUsage) {
        this.marketUsage = marketUsage;
    }

    @JsonProperty("quantity_override")
    public Integer getQuantityOverride() {
        return quantityOverride;
    }

    @JsonProperty("quantity_override")
    public void setQuantityOverride(Integer quantityOverride) {
        this.quantityOverride = quantityOverride;
    }

    @JsonProperty("shelf_time")
    public String getShelfTime() {
        return shelfTime;
    }

    @JsonProperty("shelf_time")
    public void setShelfTime(String shelfTime) {
        this.shelfTime = shelfTime;
    }

    @JsonProperty("sp_material_name")
    public String getSpMaterialName() {
        return spMaterialName;
    }

    @JsonProperty("sp_material_name")
    public void setSpMaterialName(String spMaterialName) {
        this.spMaterialName = spMaterialName;
    }

    @JsonProperty("warning_limit")
    public String getWarningLimit() {
        return warningLimit;
    }

    @JsonProperty("warning_limit")
    public void setWarningLimit(String warningLimit) {
        this.warningLimit = warningLimit;
    }

    @JsonProperty("maximum_limit")
    public String getMaximumLimit() {
        return maximumLimit;
    }

    @JsonProperty("maximum_limit")
    public void setMaximumLimit(String maximumLimit) {
        this.maximumLimit = maximumLimit;
    }

    @JsonProperty("comments")
    public String getComments() {
        return comments;
    }

    @JsonProperty("comments")
    public void setComments(String comments) {
        this.comments = comments;
    }

    @JsonProperty("warmup_time")
    public String getWarmupTime() {
        return warmupTime;
    }

    @JsonProperty("warmup_time")
    public void setWarmupTime(String warmupTime) {
        this.warmupTime = warmupTime;
    }

    @JsonProperty("storage_unit")
    public String getStorageUnit() {
        return storageUnit;
    }

    @JsonProperty("storage_unit")
    public void setStorageUnit(String storageUnit) {
        this.storageUnit = storageUnit;
    }

    @JsonProperty("sub_storage_unit")
    public String getSubStorageUnit() {
        return subStorageUnit;
    }

    @JsonProperty("sub_storage_unit")
    public void setSubStorageUnit(String subStorageUnit) {
        this.subStorageUnit = subStorageUnit;
    }

    @JsonProperty("location_override")
    public String getLocationOverride() {
        return locationOverride;
    }

    @JsonProperty("location_override")
    public void setLocationOverride(String locationOverride) {
        this.locationOverride = locationOverride;
    }

    @JsonProperty("expiration_date")
    public String getExpirationDate() {
        return expirationDate;
    }

    @JsonProperty("expiration_date")
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    @JsonProperty("manufacturing_date")
    public String getManufacturingDate() {
        return manufacturingDate;
    }

    @JsonProperty("manufacturing_date")
    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    @JsonProperty("part_class")
    public String getPartClass() {
        return partClass;
    }

    @JsonProperty("part_class")
    public void setPartClass(String partClass) {
        this.partClass = partClass;
    }

    @JsonProperty("sap_code")
    public String getSapCode() {
        return sapCode;
    }

    @JsonProperty("sap_code")
    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    @JsonProperty("ma_lenh_san_xuat_id")
    public Long getMaLenhSanXuatId() {
        return maLenhSanXuatId;
    }

    @JsonProperty("ma_lenh_san_xuat_id")
    public void setMaLenhSanXuatId(Long maLenhSanXuatId) {
        this.maLenhSanXuatId = maLenhSanXuatId;
    }

    @JsonProperty("lenh_san_xuat_id")
    public Long getLenhSanXuatId() {
        return lenhSanXuatId;
    }

    @JsonProperty("lenh_san_xuat_id")
    public void setLenhSanXuatId(Long lenhSanXuatId) {
        this.lenhSanXuatId = lenhSanXuatId;
    }

    @JsonProperty("trang_thai")
    public String getTrangThai() {
        return trangThai;
    }

    @JsonProperty("trang_thai")
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @JsonProperty("checked")
    public Integer getChecked() {
        return checked;
    }

    @JsonProperty("checked")
    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    @JsonProperty("created_at")
    public Instant getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
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
