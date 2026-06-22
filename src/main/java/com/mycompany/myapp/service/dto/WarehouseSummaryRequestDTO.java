package com.mycompany.myapp.service.dto;

public class WarehouseSummaryRequestDTO {

    private String warehouseName;
    private String warehouseAreaName;
    private String materialType;
    private Integer pageNumber;
    private Integer itemPerPage;
    private Boolean refreshCache;
    private Boolean includeOverview;

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getWarehouseAreaName() {
        return warehouseAreaName;
    }

    public void setWarehouseAreaName(String warehouseAreaName) {
        this.warehouseAreaName = warehouseAreaName;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getItemPerPage() {
        return itemPerPage;
    }

    public void setItemPerPage(Integer itemPerPage) {
        this.itemPerPage = itemPerPage;
    }

    public Boolean getRefreshCache() {
        return refreshCache;
    }

    public void setRefreshCache(Boolean refreshCache) {
        this.refreshCache = refreshCache;
    }

    public Boolean getIncludeOverview() {
        return includeOverview;
    }

    public void setIncludeOverview(Boolean includeOverview) {
        this.includeOverview = includeOverview;
    }
}
