package com.mycompany.myapp.service.dto;

public class ScanWorkOrderDTO {

    private String workOrder;
    private String lot;
    private String productCode;
    private String productName;
    private String groupName;
    private String createAt;
    private Integer pageNumber;
    private Integer itemPerPage;

    public ScanWorkOrderDTO() {}

    public String getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(String workOrder) {
        this.workOrder = workOrder;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
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
}
