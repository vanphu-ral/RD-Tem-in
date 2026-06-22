package com.mycompany.myapp.domain;

import java.util.ArrayList;
import java.util.List;

public class WarehouseAreaLocationDto {

    private Long locationId;
    private String locationName;
    private String locationFullName;
    private Double xPos;
    private Double yPos;
    private Integer productLimit;
    private Integer totalQuantity;
    private Integer materialIdentifierCount;
    private Integer materialTypeCount;
    private String dominantMaterialType;
    private boolean containsSelectedMaterialType;
    private boolean empty;
    private List<String> materialTypes = new ArrayList<>();
    private List<String> itemCodes = new ArrayList<>();

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

    public Double getXPos() {
        return xPos;
    }

    public void setXPos(Double xPos) {
        this.xPos = xPos;
    }

    public Double getYPos() {
        return yPos;
    }

    public void setYPos(Double yPos) {
        this.yPos = yPos;
    }

    public Integer getProductLimit() {
        return productLimit;
    }

    public void setProductLimit(Integer productLimit) {
        this.productLimit = productLimit;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getMaterialIdentifierCount() {
        return materialIdentifierCount;
    }

    public void setMaterialIdentifierCount(Integer materialIdentifierCount) {
        this.materialIdentifierCount = materialIdentifierCount;
    }

    public Integer getMaterialTypeCount() {
        return materialTypeCount;
    }

    public void setMaterialTypeCount(Integer materialTypeCount) {
        this.materialTypeCount = materialTypeCount;
    }

    public String getDominantMaterialType() {
        return dominantMaterialType;
    }

    public void setDominantMaterialType(String dominantMaterialType) {
        this.dominantMaterialType = dominantMaterialType;
    }

    public boolean isContainsSelectedMaterialType() {
        return containsSelectedMaterialType;
    }

    public void setContainsSelectedMaterialType(
        boolean containsSelectedMaterialType
    ) {
        this.containsSelectedMaterialType = containsSelectedMaterialType;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public List<String> getMaterialTypes() {
        return materialTypes;
    }

    public void setMaterialTypes(List<String> materialTypes) {
        this.materialTypes = materialTypes;
    }

    public List<String> getItemCodes() {
        return itemCodes;
    }

    public void setItemCodes(List<String> itemCodes) {
        this.itemCodes = itemCodes;
    }
}
