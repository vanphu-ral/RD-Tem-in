package com.mycompany.myapp.graphql.dto;

import java.util.List;

public class CreateRequestWithProductsInput {

    private String vendor;
    private String userData5;
    private String createdBy;
    private List<CreateProductInput> products;

    // Getters and Setters
    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
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
}
