package com.mycompany.panacimmc.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Location_Id")
    private Long locationId;

    @Column(name = "Location_Name")
    private String locationName;

    @Column(name = "Location_FullName")
    private String locationFullName;

    @Column(name = "Location_LocationType_Id")
    private Long locationTypeId;

    @Column(name = "Location_LocationType_Name")
    private String locationTypeName;

    @Column(name = "Location_Description")
    private String locationDescription;

    @Column(name = "Location_ParentLocationId")
    private Long parentLocationId;

    @Column(name = "Location_AreaId")
    private Long areaId;

    @Column(name = "Location_AreaName")
    private String areaName;

    @Column(name = "Location_IsMultiLocation")
    private Boolean isMultiLocation;

    @Column(name = "Location_RestrictOnePart")
    private Boolean restrictOnePart;

    @Column(name = "Location_PrefixName")
    private String prefixName;

    @Column(name = "Location_PrefixSeperator")
    private String prefixSeperator;

    @Column(name = "Location_ChildLocationRowCount")
    private Integer childLocationRowCount;

    @Column(name = "Location_ChildLocationColumnCount")
    private Integer childLocationColumnCount;

    @Column(name = "Location_SuffixSeperator")
    private String suffixSeperator;

    @Column(name = "Location_SuffixDigitLen")
    private Integer suffixDigitLen;

    @Column(name = "Location_XPos")
    private Double xPos;

    @Column(name = "Location_YPos")
    private Double yPos;

    @Column(name = "Location_ProductLimit")
    private Integer productLimit;

    @Column(name = "Location_UnLoadLocation")
    private Boolean unloadLocation;

    @Column(name = "Location_UniqueID")
    private String uniqueId;

    @Column(name = "Location_ControlFlag")
    private String controlFlag;

    @Column(name = "Location_Barcode")
    private String barcode;

    @Column(name = "Location_NameCannotBeChanged")
    private Boolean nameCannotBeChanged;

    @Column(name = "Location_DontAllowMaterials")
    private Boolean dontAllowMaterials;

    @Column(name = "Location_DontAllowCarriers")
    private Boolean dontAllowCarriers;

    @Column(name = "Location_OwnMaterial")
    private Boolean ownMaterial;

    @Column(name = "Location_TSMEnabled")
    private Boolean tsmEnabled;

    @Column(name = "Location_TSM")
    private String tsm;

    @Column(name = "Location_CreatedBy", updatable = false)
    private String createdBy;

    @Column(name = "Location_CreatedOn", updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "Location_UpdatedBy")
    private String updatedBy;

    @Column(name = "Location_UpdatedOn")
    private LocalDateTime updatedOn;

    @Column(name = "Location_Notification")
    private String notification;

    @Column(name = "notifyMaterialChange")
    private Boolean notifyMaterialChange;

    @Column(name = "Location_SubAreaId")
    private Long subAreaId;

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationFullName() {
        return locationFullName;
    }

    public void setLocationFullName(String locationFullName) {
        this.locationFullName = locationFullName;
    }

    public Long getLocationTypeId() {
        return locationTypeId;
    }

    public void setLocationTypeId(Long locationTypeId) {
        this.locationTypeId = locationTypeId;
    }

    public String getLocationTypeName() {
        return locationTypeName;
    }

    public void setLocationTypeName(String locationTypeName) {
        this.locationTypeName = locationTypeName;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public Long getParentLocationId() {
        return parentLocationId;
    }

    public void setParentLocationId(Long parentLocationId) {
        this.parentLocationId = parentLocationId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Boolean getMultiLocation() {
        return isMultiLocation;
    }

    public void setMultiLocation(Boolean multiLocation) {
        isMultiLocation = multiLocation;
    }

    public Boolean getRestrictOnePart() {
        return restrictOnePart;
    }

    public void setRestrictOnePart(Boolean restrictOnePart) {
        this.restrictOnePart = restrictOnePart;
    }

    public String getPrefixName() {
        return prefixName;
    }

    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }

    public String getPrefixSeperator() {
        return prefixSeperator;
    }

    public void setPrefixSeperator(String prefixSeperator) {
        this.prefixSeperator = prefixSeperator;
    }

    public Integer getChildLocationRowCount() {
        return childLocationRowCount;
    }

    public void setChildLocationRowCount(Integer childLocationRowCount) {
        this.childLocationRowCount = childLocationRowCount;
    }

    public Integer getChildLocationColumnCount() {
        return childLocationColumnCount;
    }

    public void setChildLocationColumnCount(Integer childLocationColumnCount) {
        this.childLocationColumnCount = childLocationColumnCount;
    }

    public String getSuffixSeperator() {
        return suffixSeperator;
    }

    public void setSuffixSeperator(String suffixSeperator) {
        this.suffixSeperator = suffixSeperator;
    }

    public Integer getSuffixDigitLen() {
        return suffixDigitLen;
    }

    public void setSuffixDigitLen(Integer suffixDigitLen) {
        this.suffixDigitLen = suffixDigitLen;
    }

    public Double getxPos() {
        return xPos;
    }

    public void setxPos(Double xPos) {
        this.xPos = xPos;
    }

    public Double getyPos() {
        return yPos;
    }

    public void setyPos(Double yPos) {
        this.yPos = yPos;
    }

    public Integer getProductLimit() {
        return productLimit;
    }

    public void setProductLimit(Integer productLimit) {
        this.productLimit = productLimit;
    }

    public Boolean getUnloadLocation() {
        return unloadLocation;
    }

    public void setUnloadLocation(Boolean unloadLocation) {
        this.unloadLocation = unloadLocation;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getControlFlag() {
        return controlFlag;
    }

    public void setControlFlag(String controlFlag) {
        this.controlFlag = controlFlag;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Boolean getNameCannotBeChanged() {
        return nameCannotBeChanged;
    }

    public void setNameCannotBeChanged(Boolean nameCannotBeChanged) {
        this.nameCannotBeChanged = nameCannotBeChanged;
    }

    public Boolean getDontAllowMaterials() {
        return dontAllowMaterials;
    }

    public void setDontAllowMaterials(Boolean dontAllowMaterials) {
        this.dontAllowMaterials = dontAllowMaterials;
    }

    public Boolean getDontAllowCarriers() {
        return dontAllowCarriers;
    }

    public void setDontAllowCarriers(Boolean dontAllowCarriers) {
        this.dontAllowCarriers = dontAllowCarriers;
    }

    public Boolean getOwnMaterial() {
        return ownMaterial;
    }

    public void setOwnMaterial(Boolean ownMaterial) {
        this.ownMaterial = ownMaterial;
    }

    public Boolean getTsmEnabled() {
        return tsmEnabled;
    }

    public void setTsmEnabled(Boolean tsmEnabled) {
        this.tsmEnabled = tsmEnabled;
    }

    public String getTsm() {
        return tsm;
    }

    public void setTsm(String tsm) {
        this.tsm = tsm;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public Boolean getNotifyMaterialChange() {
        return notifyMaterialChange;
    }

    public void setNotifyMaterialChange(Boolean notifyMaterialChange) {
        this.notifyMaterialChange = notifyMaterialChange;
    }

    public Long getSubAreaId() {
        return subAreaId;
    }

    public void setSubAreaId(Long subAreaId) {
        this.subAreaId = subAreaId;
    }
}
