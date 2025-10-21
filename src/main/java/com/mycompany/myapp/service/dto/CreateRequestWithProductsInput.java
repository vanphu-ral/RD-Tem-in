package com.mycompany.myapp.service.dto;

import java.util.List;

public class CreateRequestWithProductsInput {

    private String vendor;
    private String vendorName;
    private String userData5;
    private String createdBy;
    private String createdDate;
    private List<CreateProductInput> products;

    // Getters and Setters
    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getUserData5() {
        return userData5;
    }

    public void setUserData5(String userData5) {
        this.userData5 = userData5;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<CreateProductInput> getProducts() {
        return products;
    }

    public void setProducts(List<CreateProductInput> products) {
        this.products = products;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
