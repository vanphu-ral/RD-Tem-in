package com.mycompany.panacimmc.domain;

import javax.persistence.*;

@Entity
@Table(name = "Inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"Inventory_Id\"")
    private Long inventoryId;

    @Column(name = "\"Inventory_PartId\"")
    private Long partId;

    @Column(name = "\"Inventory_PartNumber\"")
    private String partNumber;

    @Column(name = "\"Inventory_TrackingType\"")
    private String trackingType;

    @Column(name = "\"Inventory_MaterialTraceId\"")
    private String materialTraceId;

    @Column(name = "\"Inventory_Quantity\"")
    private Integer quantity;

    @Column(name = "\"Inventory_LocationId\"")
    private Long locationId;

    @Column(name = "\"Inventory_ParentLocationId\"")
    private Long parentLocationId;

    @Column(name = "\"Inventory_LastLocationId\"")
    private Long lastLocationId;

    @Column(name = "\"Inventory_MaterialControl\"")
    private String materialControl;

    @Column(name = "\"Inventory_MaterialIdentifier\"")
    private String materialIdentifier;

    @Column(name = "\"Inventory_Status\"")
    private String status;

    @Column(name = "\"Inventory_ReservationReference\"")
    private String reservationReference;

    @Column(name = "\"Inventory_ExpirationDate\"")
    private Long expirationDate;

    @Column(name = "\"Inventory_ReceivedDate\"")
    private String receivedDate;

    @Column(name = "\"Inventory_UOMId\"")
    private Long uomId;

    @Column(name = "\"Inventory_UOMName\"")
    private String uomName;

    @Column(name = "\"Inventory_UpdatedDate\"")
    private Long updatedDate;

    @Column(name = "\"Inventory_UpdatedBy\"")
    private String updatedBy;

    @Column(name = "\"Inventory_LabelingStatus\"")
    private String labelingStatus;

    @Column(name = "\"Inventory_Printer\"")
    private String printer;

    @Column(name = "\"Inventory_SplicedMaterialIdentifier\"")
    private String splicedMaterialIdentifier;

    @Column(name = "\"Inventory_SplicedInventoryId\"")
    private Long splicedInventoryId;

    @Column(name = "\"Inventory_CarrierId\"")
    private Long carrierId;

    @Column(name = "\"Inventory_CarrierNumber\"")
    private String carrierNumber;

    @Column(name = "\"Inventory_ReservedQuantity\"")
    private Integer reservedQuantity;

    @Column(name = "\"Inventory_CalculatedStatus\"")
    private String calculatedStatus;

    @Column(name = "\"Inventory_InitialQuantity\"")
    private Integer initialQuantity;

    @Column(name = "\"Inventory_AvailableQuantity\"", insertable = false, updatable = false)
    private Integer availableQuantity;

    @Column(name = "\"Inventory_ConsumedQuantity\"")
    private Integer consumedQuantity;

    @Column(name = "\"Inventory_ScrappedQuantity\"")
    private Integer scrappedQuantity;

    @Column(name = "\"Inventory_ParentInventory_Id\"")
    private Long parentInventoryId;

    @Column(name = "\"Inventory_ParentMaterialIdentifier\"")
    private String parentMaterialIdentifier;

    @Column(name = "\"Inventroy_MaterialName\"")
    private String materialName;

    @Column(name = "\"Inventory_PU_Location\"")
    private String puLocation;

    @Column(name = "\"Inventory_LifetimeCount\"")
    private Integer lifetimeCount;

    @Column(name = "\"Inventory_Bulk_Barcode\"")
    private String bulkBarcode;

    @Column(name = "\"Inventory_Is_Bulk\"")
    private Boolean isBulk;

    @Column(name = "\"Inventory_ManufacturingDate\"")
    private String manufacturingDate;

    @Column(name = "\"Inventory_PartClass\"")
    private String partClass;

    @Column(name = "\"Inventory_MaterialType\"")
    private String materialType;

    @Column(name = "\"Inventory_CheckinDate\"")
    private String checkinDate;

    @Column(name = "\"Inventory_UsageCount\"")
    private Integer usageCount;

    @Column(name = "\"Inventory_PartAlternateNumbers_Id\"")
    private Long partAlternateNumbersId;

    @Column(name = "\"Inventory_ReelNumber\"")
    private String reelNumber;

    @Column(name = "\"Inventory_MainReelId\"")
    private Long mainReelId;

    @Column(name = "\"Inventory_ReasonCode\"")
    private String reasonCode;

    @Column(name = "\"Inventory_LastCarrierNumber\"")
    private String lastCarrierNumber;

    // Getters and Setters




    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getTrackingType() {
        return trackingType;
    }

    public void setTrackingType(String trackingType) {
        this.trackingType = trackingType;
    }

    public String getMaterialTraceId() {
        return materialTraceId;
    }

    public void setMaterialTraceId(String materialTraceId) {
        this.materialTraceId = materialTraceId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getParentLocationId() {
        return parentLocationId;
    }

    public void setParentLocationId(Long parentLocationId) {
        this.parentLocationId = parentLocationId;
    }

    public Long getLastLocationId() {
        return lastLocationId;
    }

    public void setLastLocationId(Long lastLocationId) {
        this.lastLocationId = lastLocationId;
    }

    public String getMaterialControl() {
        return materialControl;
    }

    public void setMaterialControl(String materialControl) {
        this.materialControl = materialControl;
    }

    public String getMaterialIdentifier() {
        return materialIdentifier;
    }

    public void setMaterialIdentifier(String materialIdentifier) {
        this.materialIdentifier = materialIdentifier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReservationReference() {
        return reservationReference;
    }

    public void setReservationReference(String reservationReference) {
        this.reservationReference = reservationReference;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Long getUomId() {
        return uomId;
    }

    public void setUomId(Long uomId) {
        this.uomId = uomId;
    }

    public String getUomName() {
        return uomName;
    }

    public void setUomName(String uomName) {
        this.uomName = uomName;
    }

    public Long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Long updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getLabelingStatus() {
        return labelingStatus;
    }

    public void setLabelingStatus(String labelingStatus) {
        this.labelingStatus = labelingStatus;
    }

    public String getPrinter() {
        return printer;
    }

    public void setPrinter(String printer) {
        this.printer = printer;
    }

    public String getSplicedMaterialIdentifier() {
        return splicedMaterialIdentifier;
    }

    public void setSplicedMaterialIdentifier(String splicedMaterialIdentifier) {
        this.splicedMaterialIdentifier = splicedMaterialIdentifier;
    }

    public Long getSplicedInventoryId() {
        return splicedInventoryId;
    }

    public void setSplicedInventoryId(Long splicedInventoryId) {
        this.splicedInventoryId = splicedInventoryId;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public String getCarrierNumber() {
        return carrierNumber;
    }

    public void setCarrierNumber(String carrierNumber) {
        this.carrierNumber = carrierNumber;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public String getCalculatedStatus() {
        return calculatedStatus;
    }

    public void setCalculatedStatus(String calculatedStatus) {
        this.calculatedStatus = calculatedStatus;
    }

    public Integer getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getConsumedQuantity() {
        return consumedQuantity;
    }

    public void setConsumedQuantity(Integer consumedQuantity) {
        this.consumedQuantity = consumedQuantity;
    }

    public Integer getScrappedQuantity() {
        return scrappedQuantity;
    }

    public void setScrappedQuantity(Integer scrappedQuantity) {
        this.scrappedQuantity = scrappedQuantity;
    }

    public Long getParentInventoryId() {
        return parentInventoryId;
    }

    public void setParentInventoryId(Long parentInventoryId) {
        this.parentInventoryId = parentInventoryId;
    }

    public String getParentMaterialIdentifier() {
        return parentMaterialIdentifier;
    }

    public void setParentMaterialIdentifier(String parentMaterialIdentifier) {
        this.parentMaterialIdentifier = parentMaterialIdentifier;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getPuLocation() {
        return puLocation;
    }

    public void setPuLocation(String puLocation) {
        this.puLocation = puLocation;
    }

    public Integer getLifetimeCount() {
        return lifetimeCount;
    }

    public void setLifetimeCount(Integer lifetimeCount) {
        this.lifetimeCount = lifetimeCount;
    }

    public String getBulkBarcode() {
        return bulkBarcode;
    }

    public void setBulkBarcode(String bulkBarcode) {
        this.bulkBarcode = bulkBarcode;
    }

    public Boolean getBulk() {
        return isBulk;
    }

    public void setBulk(Boolean bulk) {
        isBulk = bulk;
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

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(String checkinDate) {
        this.checkinDate = checkinDate;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public Long getPartAlternateNumbersId() {
        return partAlternateNumbersId;
    }

    public void setPartAlternateNumbersId(Long partAlternateNumbersId) {
        this.partAlternateNumbersId = partAlternateNumbersId;
    }

    public String getReelNumber() {
        return reelNumber;
    }

    public void setReelNumber(String reelNumber) {
        this.reelNumber = reelNumber;
    }

    public Long getMainReelId() {
        return mainReelId;
    }

    public void setMainReelId(Long mainReelId) {
        this.mainReelId = mainReelId;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getLastCarrierNumber() {
        return lastCarrierNumber;
    }

    public void setLastCarrierNumber(String lastCarrierNumber) {
        this.lastCarrierNumber = lastCarrierNumber;
    }
}
