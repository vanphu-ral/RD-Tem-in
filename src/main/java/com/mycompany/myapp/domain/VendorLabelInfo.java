package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A VendorLabelInfo.
 */
@Entity
@Table(name = "vendor_label_info")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VendorLabelInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

   @Size(max = 50)
   @Column(name = "reel_id", length = 50, unique = true)
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

  @Lob
  @Column(name = "vendor_qr_code")
  private String vendorQrCode;

  @Size(max = 50)
  @Column(name = "status", length = 50)
  private String status;

  @Size(max = 20)
  @Column(name = "created_by", length = 20)
  private String createdBy;

  @Column(name = "created_at")
  private Instant createdAt;

  @Size(max = 20)
  @Column(name = "updated_by", length = 20)
  private String updatedBy;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Size(max = 510)
  @Column(name = "vendor_additional_data", length = 510)
  private String vendorAdditionalData;

  @Column(name = "pana_send_status")
  private Boolean panaSendStatus;

  @Column(name = "sap_send_status")
  private Boolean sapSendStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delivery_notification_id", referencedColumnName = "id")
  private DeliveryNotification deliveryNotification;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sap_por1_id", referencedColumnName = "id")
  @JsonIgnoreProperties(value = { "deliveryNotification" }, allowSetters = true)
  private SapPor1R1 sapPor1;

   @OneToOne(mappedBy = "vendorLabelInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @JsonIgnoreProperties(value = { "palletMngt", "vendorLabelInfo" }, allowSetters = true)
   private PalletBoxMapping palletBoxMapping;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public VendorLabelInfo id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getReelId() {
    return this.reelId;
  }

  public VendorLabelInfo reelId(String reelId) {
    this.setReelId(reelId);
    return this;
  }

  public void setReelId(String reelId) {
    this.reelId = reelId;
  }

  public String getPartNumber() {
    return this.partNumber;
  }

  public VendorLabelInfo partNumber(String partNumber) {
    this.setPartNumber(partNumber);
    return this;
  }

  public void setPartNumber(String partNumber) {
    this.partNumber = partNumber;
  }

  public String getVendor() {
    return this.vendor;
  }

  public VendorLabelInfo vendor(String vendor) {
    this.setVendor(vendor);
    return this;
  }

  public void setVendor(String vendor) {
    this.vendor = vendor;
  }

  public String getLot() {
    return this.lot;
  }

  public VendorLabelInfo lot(String lot) {
    this.setLot(lot);
    return this;
  }

  public void setLot(String lot) {
    this.lot = lot;
  }

  public String getUserData1() {
    return this.userData1;
  }

  public VendorLabelInfo userData1(String userData1) {
    this.setUserData1(userData1);
    return this;
  }

  public void setUserData1(String userData1) {
    this.userData1 = userData1;
  }

  public String getUserData2() {
    return this.userData2;
  }

  public VendorLabelInfo userData2(String userData2) {
    this.setUserData2(userData2);
    return this;
  }

  public void setUserData2(String userData2) {
    this.userData2 = userData2;
  }

  public String getUserData3() {
    return this.userData3;
  }

  public VendorLabelInfo userData3(String userData3) {
    this.setUserData3(userData3);
    return this;
  }

  public void setUserData3(String userData3) {
    this.userData3 = userData3;
  }

  public String getUserData4() {
    return this.userData4;
  }

  public VendorLabelInfo userData4(String userData4) {
    this.setUserData4(userData4);
    return this;
  }

  public void setUserData4(String userData4) {
    this.userData4 = userData4;
  }

  public String getUserData5() {
    return this.userData5;
  }

  public VendorLabelInfo userData5(String userData5) {
    this.setUserData5(userData5);
    return this;
  }

  public void setUserData5(String userData5) {
    this.userData5 = userData5;
  }

  public Integer getInitialQuantity() {
    return this.initialQuantity;
  }

  public VendorLabelInfo initialQuantity(Integer initialQuantity) {
    this.setInitialQuantity(initialQuantity);
    return this;
  }

  public void setInitialQuantity(Integer initialQuantity) {
    this.initialQuantity = initialQuantity;
  }

  public String getMsdLevel() {
    return this.msdLevel;
  }

  public VendorLabelInfo msdLevel(String msdLevel) {
    this.setMsdLevel(msdLevel);
    return this;
  }

  public void setMsdLevel(String msdLevel) {
    this.msdLevel = msdLevel;
  }

  public String getMsdInitialFloorTime() {
    return this.msdInitialFloorTime;
  }

  public VendorLabelInfo msdInitialFloorTime(String msdInitialFloorTime) {
    this.setMsdInitialFloorTime(msdInitialFloorTime);
    return this;
  }

  public void setMsdInitialFloorTime(String msdInitialFloorTime) {
    this.msdInitialFloorTime = msdInitialFloorTime;
  }

  public String getMsdBagSealDate() {
    return this.msdBagSealDate;
  }

  public VendorLabelInfo msdBagSealDate(String msdBagSealDate) {
    this.setMsdBagSealDate(msdBagSealDate);
    return this;
  }

  public void setMsdBagSealDate(String msdBagSealDate) {
    this.msdBagSealDate = msdBagSealDate;
  }

  public String getMarketUsage() {
    return this.marketUsage;
  }

  public VendorLabelInfo marketUsage(String marketUsage) {
    this.setMarketUsage(marketUsage);
    return this;
  }

  public void setMarketUsage(String marketUsage) {
    this.marketUsage = marketUsage;
  }

  public Integer getQuantityOverride() {
    return this.quantityOverride;
  }

  public VendorLabelInfo quantityOverride(Integer quantityOverride) {
    this.setQuantityOverride(quantityOverride);
    return this;
  }

  public void setQuantityOverride(Integer quantityOverride) {
    this.quantityOverride = quantityOverride;
  }

  public String getShelfTime() {
    return this.shelfTime;
  }

  public VendorLabelInfo shelfTime(String shelfTime) {
    this.setShelfTime(shelfTime);
    return this;
  }

  public void setShelfTime(String shelfTime) {
    this.shelfTime = shelfTime;
  }

  public String getSpMaterialName() {
    return this.spMaterialName;
  }

  public VendorLabelInfo spMaterialName(String spMaterialName) {
    this.setSpMaterialName(spMaterialName);
    return this;
  }

  public void setSpMaterialName(String spMaterialName) {
    this.spMaterialName = spMaterialName;
  }

  public String getWarningLimit() {
    return this.warningLimit;
  }

  public VendorLabelInfo warningLimit(String warningLimit) {
    this.setWarningLimit(warningLimit);
    return this;
  }

  public void setWarningLimit(String warningLimit) {
    this.warningLimit = warningLimit;
  }

  public String getMaximumLimit() {
    return this.maximumLimit;
  }

  public VendorLabelInfo maximumLimit(String maximumLimit) {
    this.setMaximumLimit(maximumLimit);
    return this;
  }

  public void setMaximumLimit(String maximumLimit) {
    this.maximumLimit = maximumLimit;
  }

  public String getComments() {
    return this.comments;
  }

  public VendorLabelInfo comments(String comments) {
    this.setComments(comments);
    return this;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getWarmupTime() {
    return this.warmupTime;
  }

  public VendorLabelInfo warmupTime(String warmupTime) {
    this.setWarmupTime(warmupTime);
    return this;
  }

  public void setWarmupTime(String warmupTime) {
    this.warmupTime = warmupTime;
  }

  public String getStorageUnit() {
    return this.storageUnit;
  }

  public VendorLabelInfo storageUnit(String storageUnit) {
    this.setStorageUnit(storageUnit);
    return this;
  }

  public void setStorageUnit(String storageUnit) {
    this.storageUnit = storageUnit;
  }

  public String getSubStorageUnit() {
    return this.subStorageUnit;
  }

  public VendorLabelInfo subStorageUnit(String subStorageUnit) {
    this.setSubStorageUnit(subStorageUnit);
    return this;
  }

  public void setSubStorageUnit(String subStorageUnit) {
    this.subStorageUnit = subStorageUnit;
  }

  public String getLocationOverride() {
    return this.locationOverride;
  }

  public VendorLabelInfo locationOverride(String locationOverride) {
    this.setLocationOverride(locationOverride);
    return this;
  }

  public void setLocationOverride(String locationOverride) {
    this.locationOverride = locationOverride;
  }

  public String getExpirationDate() {
    return this.expirationDate;
  }

  public VendorLabelInfo expirationDate(String expirationDate) {
    this.setExpirationDate(expirationDate);
    return this;
  }

  public void setExpirationDate(String expirationDate) {
    this.expirationDate = expirationDate;
  }

  public String getManufacturingDate() {
    return this.manufacturingDate;
  }

  public VendorLabelInfo manufacturingDate(String manufacturingDate) {
    this.setManufacturingDate(manufacturingDate);
    return this;
  }

  public void setManufacturingDate(String manufacturingDate) {
    this.manufacturingDate = manufacturingDate;
  }

  public String getPartClass() {
    return this.partClass;
  }

  public VendorLabelInfo partClass(String partClass) {
    this.setPartClass(partClass);
    return this;
  }

  public void setPartClass(String partClass) {
    this.partClass = partClass;
  }

  public String getSapCode() {
    return this.sapCode;
  }

  public VendorLabelInfo sapCode(String sapCode) {
    this.setSapCode(sapCode);
    return this;
  }

  public void setSapCode(String sapCode) {
    this.sapCode = sapCode;
  }

  public String getVendorQrCode() {
    return this.vendorQrCode;
  }

  public VendorLabelInfo vendorQrCode(String vendorQrCode) {
    this.setVendorQrCode(vendorQrCode);
    return this;
  }

  public void setVendorQrCode(String vendorQrCode) {
    this.vendorQrCode = vendorQrCode;
  }

  public String getStatus() {
    return this.status;
  }

  public VendorLabelInfo status(String status) {
    this.setStatus(status);
    return this;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getCreatedBy() {
    return this.createdBy;
  }

  public VendorLabelInfo createdBy(String createdBy) {
    this.setCreatedBy(createdBy);
    return this;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Instant getCreatedAt() {
    return this.createdAt;
  }

  public VendorLabelInfo createdAt(Instant createdAt) {
    this.setCreatedAt(createdAt);
    return this;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedBy() {
    return this.updatedBy;
  }

  public VendorLabelInfo updatedBy(String updatedBy) {
    this.setUpdatedBy(updatedBy);
    return this;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public Instant getUpdatedAt() {
    return this.updatedAt;
  }

  public VendorLabelInfo updatedAt(Instant updatedAt) {
    this.setUpdatedAt(updatedAt);
    return this;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getVendorAdditionalData() {
    return this.vendorAdditionalData;
  }

  public VendorLabelInfo vendorAdditionalData(String vendorAdditionalData) {
    this.setVendorAdditionalData(vendorAdditionalData);
    return this;
  }

  public void setVendorAdditionalData(String vendorAdditionalData) {
    this.vendorAdditionalData = vendorAdditionalData;
  }

  public Boolean getPanaSendStatus() {
    return this.panaSendStatus;
  }

  public VendorLabelInfo panaSendStatus(Boolean panaSendStatus) {
    this.setPanaSendStatus(panaSendStatus);
    return this;
  }

  public void setPanaSendStatus(Boolean panaSendStatus) {
    this.panaSendStatus = panaSendStatus;
  }

  public Boolean getSapSendStatus() {
    return this.sapSendStatus;
  }

  public VendorLabelInfo sapSendStatus(Boolean sapSendStatus) {
    this.setSapSendStatus(sapSendStatus);
    return this;
  }

  public void setSapSendStatus(Boolean sapSendStatus) {
    this.sapSendStatus = sapSendStatus;
  }

  public DeliveryNotification getDeliveryNotification() {
    return this.deliveryNotification;
  }

  public void setDeliveryNotification(
    DeliveryNotification deliveryNotification
  ) {
    this.deliveryNotification = deliveryNotification;
  }

  public VendorLabelInfo deliveryNotification(
    DeliveryNotification deliveryNotification
  ) {
    this.setDeliveryNotification(deliveryNotification);
    return this;
  }

  public SapPor1R1 getSapPor1() {
    return this.sapPor1;
  }

  public void setSapPor1(SapPor1R1 sapPor1R1) {
    this.sapPor1 = sapPor1R1;
  }

  public VendorLabelInfo sapPor1(SapPor1R1 sapPor1R1) {
    this.setSapPor1(sapPor1R1);
    return this;
  }

  public PalletBoxMapping getPalletBoxMapping() {
    return this.palletBoxMapping;
  }

   public void setPalletBoxMapping(PalletBoxMapping palletBoxMapping) {
     if (palletBoxMapping == null) {
       if (this.palletBoxMapping != null) {
         this.palletBoxMapping.setVendorLabelInfo(null);
       }
     } else {
       palletBoxMapping.setVendorLabelInfo(this);
     }
     this.palletBoxMapping = palletBoxMapping;
   }

  public VendorLabelInfo palletBoxMapping(PalletBoxMapping palletBoxMapping) {
    this.setPalletBoxMapping(palletBoxMapping);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof VendorLabelInfo)) {
      return false;
    }
    return getId() != null && getId().equals(((VendorLabelInfo) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "VendorLabelInfo{" +
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
            ", vendorQrCode='" + getVendorQrCode() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", vendorAdditionalData='" + getVendorAdditionalData() + "'" +
            ", panaSendStatus='" + getPanaSendStatus() + "'" +
            ", sapSendStatus='" + getSapSendStatus() + "'" +
            "}";
    }
}
