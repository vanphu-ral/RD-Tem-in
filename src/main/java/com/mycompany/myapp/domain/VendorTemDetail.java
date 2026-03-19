package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Entity VendorTemDetail
 */
@Entity
@Table(name = "vendor_tem_detail")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VendorTemDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "po_detail_id")
    private Long poDetailId;

    @Column(name = "import_vendor_tem_transactions_id")
    private Long importVendorTemTransactionsId;

    @Size(max = 30)
    @Column(name = "reel_id", length = 30)
    private String reelId;

    @Size(max = 50)
    @Column(name = "part_number", length = 50)
    private String partNumber;

    @Size(max = 20)
    @Column(name = "vendor", length = 20)
    private String vendor;

    @Size(max = 30)
    @Column(name = "lot", length = 30)
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

    @Size(max = 20)
    @Column(name = "sp_material_name", length = 20)
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

    @Size(max = 510)
    @Column(name = "vendor_additional_data", length = 510)
    private String vendorAdditionalData;

    @Column(name = "vendor_qr_code", columnDefinition = "TEXT")
    private String vendorQrCode;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @Size(max = 20)
    @Column(name = "created_by", length = 20)
    private String createdBy;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Size(max = 20)
    @Column(name = "updated_by", length = 20)
    private String updatedBy;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "vendorTemDetails", "importVendorTemTransactions" },
        allowSetters = true
    )
    @JoinColumn(name = "po_detail_id", insertable = false, updatable = false)
    private PoDetail poDetail;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VendorTemDetail id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPoDetailId() {
        return this.poDetailId;
    }

    public VendorTemDetail poDetailId(Long poDetailId) {
        this.setPoDetailId(poDetailId);
        return this;
    }

    public void setPoDetailId(Long poDetailId) {
        this.poDetailId = poDetailId;
    }

    public Long getImportVendorTemTransactionsId() {
        return this.importVendorTemTransactionsId;
    }

    public VendorTemDetail importVendorTemTransactionsId(
        Long importVendorTemTransactionsId
    ) {
        this.setImportVendorTemTransactionsId(importVendorTemTransactionsId);
        return this;
    }

    public void setImportVendorTemTransactionsId(
        Long importVendorTemTransactionsId
    ) {
        this.importVendorTemTransactionsId = importVendorTemTransactionsId;
    }

    public String getReelId() {
        return this.reelId;
    }

    public VendorTemDetail reelId(String reelId) {
        this.setReelId(reelId);
        return this;
    }

    public void setReelId(String reelId) {
        this.reelId = reelId;
    }

    public String getPartNumber() {
        return this.partNumber;
    }

    public VendorTemDetail partNumber(String partNumber) {
        this.setPartNumber(partNumber);
        return this;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getVendor() {
        return this.vendor;
    }

    public VendorTemDetail vendor(String vendor) {
        this.setVendor(vendor);
        return this;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getLot() {
        return this.lot;
    }

    public VendorTemDetail lot(String lot) {
        this.setLot(lot);
        return this;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getUserData1() {
        return this.userData1;
    }

    public VendorTemDetail userData1(String userData1) {
        this.setUserData1(userData1);
        return this;
    }

    public void setUserData1(String userData1) {
        this.userData1 = userData1;
    }

    public String getUserData2() {
        return this.userData2;
    }

    public VendorTemDetail userData2(String userData2) {
        this.setUserData2(userData2);
        return this;
    }

    public void setUserData2(String userData2) {
        this.userData2 = userData2;
    }

    public String getUserData3() {
        return this.userData3;
    }

    public VendorTemDetail userData3(String userData3) {
        this.setUserData3(userData3);
        return this;
    }

    public void setUserData3(String userData3) {
        this.userData3 = userData3;
    }

    public String getUserData4() {
        return this.userData4;
    }

    public VendorTemDetail userData4(String userData4) {
        this.setUserData4(userData4);
        return this;
    }

    public void setUserData4(String userData4) {
        this.userData4 = userData4;
    }

    public String getUserData5() {
        return this.userData5;
    }

    public VendorTemDetail userData5(String userData5) {
        this.setUserData5(userData5);
        return this;
    }

    public void setUserData5(String userData5) {
        this.userData5 = userData5;
    }

    public Integer getInitialQuantity() {
        return this.initialQuantity;
    }

    public VendorTemDetail initialQuantity(Integer initialQuantity) {
        this.setInitialQuantity(initialQuantity);
        return this;
    }

    public void setInitialQuantity(Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public String getMsdLevel() {
        return this.msdLevel;
    }

    public VendorTemDetail msdLevel(String msdLevel) {
        this.setMsdLevel(msdLevel);
        return this;
    }

    public void setMsdLevel(String msdLevel) {
        this.msdLevel = msdLevel;
    }

    public String getMsdInitialFloorTime() {
        return this.msdInitialFloorTime;
    }

    public VendorTemDetail msdInitialFloorTime(String msdInitialFloorTime) {
        this.setMsdInitialFloorTime(msdInitialFloorTime);
        return this;
    }

    public void setMsdInitialFloorTime(String msdInitialFloorTime) {
        this.msdInitialFloorTime = msdInitialFloorTime;
    }

    public String getMsdBagSealDate() {
        return this.msdBagSealDate;
    }

    public VendorTemDetail msdBagSealDate(String msdBagSealDate) {
        this.setMsdBagSealDate(msdBagSealDate);
        return this;
    }

    public void setMsdBagSealDate(String msdBagSealDate) {
        this.msdBagSealDate = msdBagSealDate;
    }

    public String getMarketUsage() {
        return this.marketUsage;
    }

    public VendorTemDetail marketUsage(String marketUsage) {
        this.setMarketUsage(marketUsage);
        return this;
    }

    public void setMarketUsage(String marketUsage) {
        this.marketUsage = marketUsage;
    }

    public Integer getQuantityOverride() {
        return this.quantityOverride;
    }

    public VendorTemDetail quantityOverride(Integer quantityOverride) {
        this.setQuantityOverride(quantityOverride);
        return this;
    }

    public void setQuantityOverride(Integer quantityOverride) {
        this.quantityOverride = quantityOverride;
    }

    public String getShelfTime() {
        return this.shelfTime;
    }

    public VendorTemDetail shelfTime(String shelfTime) {
        this.setShelfTime(shelfTime);
        return this;
    }

    public void setShelfTime(String shelfTime) {
        this.shelfTime = shelfTime;
    }

    public String getSpMaterialName() {
        return this.spMaterialName;
    }

    public VendorTemDetail spMaterialName(String spMaterialName) {
        this.setSpMaterialName(spMaterialName);
        return this;
    }

    public void setSpMaterialName(String spMaterialName) {
        this.spMaterialName = spMaterialName;
    }

    public String getWarningLimit() {
        return this.warningLimit;
    }

    public VendorTemDetail warningLimit(String warningLimit) {
        this.setWarningLimit(warningLimit);
        return this;
    }

    public void setWarningLimit(String warningLimit) {
        this.warningLimit = warningLimit;
    }

    public String getMaximumLimit() {
        return this.maximumLimit;
    }

    public VendorTemDetail maximumLimit(String maximumLimit) {
        this.setMaximumLimit(maximumLimit);
        return this;
    }

    public void setMaximumLimit(String maximumLimit) {
        this.maximumLimit = maximumLimit;
    }

    public String getComments() {
        return this.comments;
    }

    public VendorTemDetail comments(String comments) {
        this.setComments(comments);
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getWarmupTime() {
        return this.warmupTime;
    }

    public VendorTemDetail warmupTime(String warmupTime) {
        this.setWarmupTime(warmupTime);
        return this;
    }

    public void setWarmupTime(String warmupTime) {
        this.warmupTime = warmupTime;
    }

    public String getStorageUnit() {
        return this.storageUnit;
    }

    public VendorTemDetail storageUnit(String storageUnit) {
        this.setStorageUnit(storageUnit);
        return this;
    }

    public void setStorageUnit(String storageUnit) {
        this.storageUnit = storageUnit;
    }

    public String getSubStorageUnit() {
        return this.subStorageUnit;
    }

    public VendorTemDetail subStorageUnit(String subStorageUnit) {
        this.setSubStorageUnit(subStorageUnit);
        return this;
    }

    public void setSubStorageUnit(String subStorageUnit) {
        this.subStorageUnit = subStorageUnit;
    }

    public String getLocationOverride() {
        return this.locationOverride;
    }

    public VendorTemDetail locationOverride(String locationOverride) {
        this.setLocationOverride(locationOverride);
        return this;
    }

    public void setLocationOverride(String locationOverride) {
        this.locationOverride = locationOverride;
    }

    public String getExpirationDate() {
        return this.expirationDate;
    }

    public VendorTemDetail expirationDate(String expirationDate) {
        this.setExpirationDate(expirationDate);
        return this;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getManufacturingDate() {
        return this.manufacturingDate;
    }

    public VendorTemDetail manufacturingDate(String manufacturingDate) {
        this.setManufacturingDate(manufacturingDate);
        return this;
    }

    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public String getPartClass() {
        return this.partClass;
    }

    public VendorTemDetail partClass(String partClass) {
        this.setPartClass(partClass);
        return this;
    }

    public void setPartClass(String partClass) {
        this.partClass = partClass;
    }

    public String getSapCode() {
        return this.sapCode;
    }

    public VendorTemDetail sapCode(String sapCode) {
        this.setSapCode(sapCode);
        return this;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public String getVendorAdditionalData() {
        return this.vendorAdditionalData;
    }

    public VendorTemDetail vendorAdditionalData(String vendorAdditionalData) {
        this.setVendorAdditionalData(vendorAdditionalData);
        return this;
    }

    public void setVendorAdditionalData(String vendorAdditionalData) {
        this.vendorAdditionalData = vendorAdditionalData;
    }

    public String getVendorQrCode() {
        return this.vendorQrCode;
    }

    public VendorTemDetail vendorQrCode(String vendorQrCode) {
        this.setVendorQrCode(vendorQrCode);
        return this;
    }

    public void setVendorQrCode(String vendorQrCode) {
        this.vendorQrCode = vendorQrCode;
    }

    public String getStatus() {
        return this.status;
    }

    public VendorTemDetail status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public VendorTemDetail createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public VendorTemDetail createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public VendorTemDetail updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public VendorTemDetail updatedAt(ZonedDateTime updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public PoDetail getPoDetail() {
        return this.poDetail;
    }

    public void setPoDetail(PoDetail poDetail) {
        this.poDetail = poDetail;
    }

    public VendorTemDetail poDetail(PoDetail poDetail) {
        this.setPoDetail(poDetail);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here
    @ManyToOne
    @JoinColumn(
        name = "import_vendor_tem_transactions_id",
        insertable = false,
        updatable = false
    ) // Thêm 2 thuộc tính
    // này
    private ImportVendorTemTransactions importVendorTemTransactions;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VendorTemDetail)) {
            return false;
        }
        return getId() != null && getId().equals(((VendorTemDetail) o).getId());
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
        return "VendorTemDetail{" +
                "id=" + getId() +
                ", poDetailId=" + getPoDetailId() +
                ", importVendorTemTransactionsId=" + getImportVendorTemTransactionsId() +
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
                ", vendorAdditionalData='" + getVendorAdditionalData() + "'" +
                ", vendorQrCode='" + getVendorQrCode() + "'" +
                ", status='" + getStatus() + "'" +
                ", createdBy='" + getCreatedBy() + "'" +
                ", createdAt='" + getCreatedAt() + "'" +
                ", updatedBy='" + getUpdatedBy() + "'" +
                ", updatedAt='" + getUpdatedAt() + "'" +
                "}";
    }
}
