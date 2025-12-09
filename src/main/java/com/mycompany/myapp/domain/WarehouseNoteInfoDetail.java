package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A WarehouseNoteInfoDetail.
 */
@Entity
@Table(name = "warehouse_note_info_detail")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseNoteInfoDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 50)
    @Column(name = "reel_id", length = 50)
    private String reelId;

    @Size(max = 50)
    @Column(name = "part_number", length = 50)
    private String partNumber;

    @Size(max = 20)
    @Column(name = "vendor", length = 20)
    private String vendor;

    @Size(max = 20)
    @Column(name = "lot", length = 20)
    private String lot;

    @Size(max = 20)
    @Column(name = "user_data_1", length = 20)
    private String userData1;

    @Size(max = 255)
    @Column(name = "user_data_2", length = 255)
    private String userData2;

    @Size(max = 100)
    @Column(name = "user_data_3", length = 100)
    private String userData3;

    @Size(max = 20)
    @Column(name = "user_data_4", length = 20)
    private String userData4;

    @Size(max = 20)
    @Column(name = "user_data_5", length = 20)
    private String userData5;

    @Column(name = "initial_quantity")
    private Integer initialQuantity;

    @Size(max = 20)
    @Column(name = "msd_level", length = 20)
    private String msdLevel;

    @Size(max = 20)
    @Column(name = "msd_initial_floor_time", length = 20)
    private String msdInitialFloorTime;

    @Size(max = 20)
    @Column(name = "msd_bag_seal_date", length = 20)
    private String msdBagSealDate;

    @Size(max = 20)
    @Column(name = "market_usage", length = 20)
    private String marketUsage;

    @Column(name = "quantity_override")
    private Integer quantityOverride;

    @Size(max = 20)
    @Column(name = "shelf_time", length = 20)
    private String shelfTime;

    @Size(max = 255)
    @Column(name = "sp_material_name", length = 255)
    private String spMaterialName;

    @Size(max = 20)
    @Column(name = "warning_limit", length = 20)
    private String warningLimit;

    @Size(max = 20)
    @Column(name = "maximum_limit", length = 20)
    private String maximumLimit;

    @Size(max = 50)
    @Column(name = "comments", length = 50)
    private String comments;

    @Size(max = 20)
    @Column(name = "warmup_time", length = 20)
    private String warmupTime;

    @Size(max = 20)
    @Column(name = "storage_unit", length = 20)
    private String storageUnit;

    @Size(max = 20)
    @Column(name = "sub_storage_unit", length = 20)
    private String subStorageUnit;

    @Size(max = 20)
    @Column(name = "location_override", length = 20)
    private String locationOverride;

    @Size(max = 20)
    @Column(name = "expiration_date", length = 20)
    private String expirationDate;

    @Size(max = 20)
    @Column(name = "manufacturing_date", length = 20)
    private String manufacturingDate;

    @Size(max = 20)
    @Column(name = "part_class", length = 20)
    private String partClass;

    @Size(max = 20)
    @Column(name = "sap_code", length = 20)
    private String sapCode;

    @Column(name = "ma_lenh_san_xuat_id")
    private Long maLenhSanXuatId;

    @Column(name = "lenh_san_xuat_id")
    private Long lenhSanXuatId;

    @Size(max = 50)
    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Column(name = "checked")
    private Integer checked;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "wms_send_status")
    private Boolean wmsSendStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "ma_lenh_san_xuat_id",
        insertable = false,
        updatable = false
    )
    @JsonIgnoreProperties(
        value = { "genTemConfigs", "serialMappings", "details" },
        allowSetters = true
    )
    private WarehouseNoteInfo maLenhSanXuat;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WarehouseNoteInfoDetail id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReelId() {
        return this.reelId;
    }

    public WarehouseNoteInfoDetail reelId(String reelId) {
        this.setReelId(reelId);
        return this;
    }

    public void setReelId(String reelId) {
        this.reelId = reelId;
    }

    public String getPartNumber() {
        return this.partNumber;
    }

    public WarehouseNoteInfoDetail partNumber(String partNumber) {
        this.setPartNumber(partNumber);
        return this;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getVendor() {
        return this.vendor;
    }

    public WarehouseNoteInfoDetail vendor(String vendor) {
        this.setVendor(vendor);
        return this;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getLot() {
        return this.lot;
    }

    public WarehouseNoteInfoDetail lot(String lot) {
        this.setLot(lot);
        return this;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getUserData1() {
        return this.userData1;
    }

    public WarehouseNoteInfoDetail userData1(String userData1) {
        this.setUserData1(userData1);
        return this;
    }

    public void setUserData1(String userData1) {
        this.userData1 = userData1;
    }

    public String getUserData2() {
        return this.userData2;
    }

    public WarehouseNoteInfoDetail userData2(String userData2) {
        this.setUserData2(userData2);
        return this;
    }

    public void setUserData2(String userData2) {
        this.userData2 = userData2;
    }

    public String getUserData3() {
        return this.userData3;
    }

    public WarehouseNoteInfoDetail userData3(String userData3) {
        this.setUserData3(userData3);
        return this;
    }

    public void setUserData3(String userData3) {
        this.userData3 = userData3;
    }

    public String getUserData4() {
        return this.userData4;
    }

    public WarehouseNoteInfoDetail userData4(String userData4) {
        this.setUserData4(userData4);
        return this;
    }

    public void setUserData4(String userData4) {
        this.userData4 = userData4;
    }

    public String getUserData5() {
        return this.userData5;
    }

    public WarehouseNoteInfoDetail userData5(String userData5) {
        this.setUserData5(userData5);
        return this;
    }

    public void setUserData5(String userData5) {
        this.userData5 = userData5;
    }

    public Integer getInitialQuantity() {
        return this.initialQuantity;
    }

    public WarehouseNoteInfoDetail initialQuantity(Integer initialQuantity) {
        this.setInitialQuantity(initialQuantity);
        return this;
    }

    public void setInitialQuantity(Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public String getMsdLevel() {
        return this.msdLevel;
    }

    public WarehouseNoteInfoDetail msdLevel(String msdLevel) {
        this.setMsdLevel(msdLevel);
        return this;
    }

    public void setMsdLevel(String msdLevel) {
        this.msdLevel = msdLevel;
    }

    public String getMsdInitialFloorTime() {
        return this.msdInitialFloorTime;
    }

    public WarehouseNoteInfoDetail msdInitialFloorTime(
        String msdInitialFloorTime
    ) {
        this.setMsdInitialFloorTime(msdInitialFloorTime);
        return this;
    }

    public void setMsdInitialFloorTime(String msdInitialFloorTime) {
        this.msdInitialFloorTime = msdInitialFloorTime;
    }

    public String getMsdBagSealDate() {
        return this.msdBagSealDate;
    }

    public WarehouseNoteInfoDetail msdBagSealDate(String msdBagSealDate) {
        this.setMsdBagSealDate(msdBagSealDate);
        return this;
    }

    public void setMsdBagSealDate(String msdBagSealDate) {
        this.msdBagSealDate = msdBagSealDate;
    }

    public String getMarketUsage() {
        return this.marketUsage;
    }

    public WarehouseNoteInfoDetail marketUsage(String marketUsage) {
        this.setMarketUsage(marketUsage);
        return this;
    }

    public void setMarketUsage(String marketUsage) {
        this.marketUsage = marketUsage;
    }

    public Integer getQuantityOverride() {
        return this.quantityOverride;
    }

    public WarehouseNoteInfoDetail quantityOverride(Integer quantityOverride) {
        this.setQuantityOverride(quantityOverride);
        return this;
    }

    public void setQuantityOverride(Integer quantityOverride) {
        this.quantityOverride = quantityOverride;
    }

    public String getShelfTime() {
        return this.shelfTime;
    }

    public WarehouseNoteInfoDetail shelfTime(String shelfTime) {
        this.setShelfTime(shelfTime);
        return this;
    }

    public void setShelfTime(String shelfTime) {
        this.shelfTime = shelfTime;
    }

    public String getSpMaterialName() {
        return this.spMaterialName;
    }

    public WarehouseNoteInfoDetail spMaterialName(String spMaterialName) {
        this.setSpMaterialName(spMaterialName);
        return this;
    }

    public void setSpMaterialName(String spMaterialName) {
        this.spMaterialName = spMaterialName;
    }

    public String getWarningLimit() {
        return this.warningLimit;
    }

    public WarehouseNoteInfoDetail warningLimit(String warningLimit) {
        this.setWarningLimit(warningLimit);
        return this;
    }

    public void setWarningLimit(String warningLimit) {
        this.warningLimit = warningLimit;
    }

    public String getMaximumLimit() {
        return this.maximumLimit;
    }

    public WarehouseNoteInfoDetail maximumLimit(String maximumLimit) {
        this.setMaximumLimit(maximumLimit);
        return this;
    }

    public void setMaximumLimit(String maximumLimit) {
        this.maximumLimit = maximumLimit;
    }

    public String getComments() {
        return this.comments;
    }

    public WarehouseNoteInfoDetail comments(String comments) {
        this.setComments(comments);
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getWarmupTime() {
        return this.warmupTime;
    }

    public WarehouseNoteInfoDetail warmupTime(String warmupTime) {
        this.setWarmupTime(warmupTime);
        return this;
    }

    public void setWarmupTime(String warmupTime) {
        this.warmupTime = warmupTime;
    }

    public String getStorageUnit() {
        return this.storageUnit;
    }

    public WarehouseNoteInfoDetail storageUnit(String storageUnit) {
        this.setStorageUnit(storageUnit);
        return this;
    }

    public void setStorageUnit(String storageUnit) {
        this.storageUnit = storageUnit;
    }

    public String getSubStorageUnit() {
        return this.subStorageUnit;
    }

    public WarehouseNoteInfoDetail subStorageUnit(String subStorageUnit) {
        this.setSubStorageUnit(subStorageUnit);
        return this;
    }

    public void setSubStorageUnit(String subStorageUnit) {
        this.subStorageUnit = subStorageUnit;
    }

    public String getLocationOverride() {
        return this.locationOverride;
    }

    public WarehouseNoteInfoDetail locationOverride(String locationOverride) {
        this.setLocationOverride(locationOverride);
        return this;
    }

    public void setLocationOverride(String locationOverride) {
        this.locationOverride = locationOverride;
    }

    public String getExpirationDate() {
        return this.expirationDate;
    }

    public WarehouseNoteInfoDetail expirationDate(String expirationDate) {
        this.setExpirationDate(expirationDate);
        return this;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getManufacturingDate() {
        return this.manufacturingDate;
    }

    public WarehouseNoteInfoDetail manufacturingDate(String manufacturingDate) {
        this.setManufacturingDate(manufacturingDate);
        return this;
    }

    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public String getPartClass() {
        return this.partClass;
    }

    public WarehouseNoteInfoDetail partClass(String partClass) {
        this.setPartClass(partClass);
        return this;
    }

    public void setPartClass(String partClass) {
        this.partClass = partClass;
    }

    public String getSapCode() {
        return this.sapCode;
    }

    public WarehouseNoteInfoDetail sapCode(String sapCode) {
        this.setSapCode(sapCode);
        return this;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public Long getmaLenhSanXuatId() {
        return this.maLenhSanXuatId;
    }

    public WarehouseNoteInfoDetail maLenhSanXuatId(Long maLenhSanXuatId) {
        this.setLenhSanXuatId(maLenhSanXuatId);
        return this;
    }

    public void setmaLenhSanXuatId(Long maLenhSanXuatId) {
        this.maLenhSanXuatId = maLenhSanXuatId;
    }

    public Long getLenhSanXuatId() {
        return this.lenhSanXuatId;
    }

    public WarehouseNoteInfoDetail lenhSanXuatId(Long lenhSanXuatId) {
        this.setLenhSanXuatId(lenhSanXuatId);
        return this;
    }

    public void setLenhSanXuatId(Long lenhSanXuatId) {
        this.lenhSanXuatId = lenhSanXuatId;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public WarehouseNoteInfoDetail trangThai(String trangThai) {
        this.setTrangThai(trangThai);
        return this;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Integer getChecked() {
        return this.checked;
    }

    public WarehouseNoteInfoDetail checked(Integer checked) {
        this.setChecked(checked);
        return this;
    }

    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public WarehouseNoteInfoDetail createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getWmsSendStatus() {
        return this.wmsSendStatus;
    }

    public WarehouseNoteInfoDetail wmsSendStatus(Boolean wmsSendStatus) {
        this.setWmsSendStatus(wmsSendStatus);
        return this;
    }

    public void setWmsSendStatus(Boolean wmsSendStatus) {
        this.wmsSendStatus = wmsSendStatus;
    }

    public WarehouseNoteInfo getMaLenhSanXuat() {
        return this.maLenhSanXuat;
    }

    public void setMaLenhSanXuat(WarehouseNoteInfo warehouseStampInfo) {
        this.maLenhSanXuat = warehouseStampInfo;
    }

    public WarehouseNoteInfoDetail maLenhSanXuat(
        WarehouseNoteInfo warehouseStampInfo
    ) {
        this.setMaLenhSanXuat(warehouseStampInfo);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WarehouseNoteInfoDetail)) {
            return false;
        }
        return (
            getId() != null &&
            getId().equals(((WarehouseNoteInfoDetail) o).getId())
        );
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WarehouseNoteInfoDetail{" +
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
                ", lenhSanXuatId=" + getLenhSanXuatId() +
                ", trangThai='" + getTrangThai() + "'" +
                ", checked=" + getChecked() +
                ", createdAt='" + getCreatedAt() + "'" +
                ", wmsSendStatus=" + getWmsSendStatus() +
                "}";
    }
}
