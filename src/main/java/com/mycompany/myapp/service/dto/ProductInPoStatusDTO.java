package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ProductInPoStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank
    @Size(max = 50)
    private String sapCode;

    @NotBlank
    @Size(max = 255)
    private String productName;

    @Size(max = 6)
    private String whsCode;

    @Size(max = 50)
    private String userData5;

    private LocalDateTime createdAt;

    @Size(max = 100)
    private String createBy;

    private Integer quantityByPo;

    @Size(max = 100)
    private String vendor;

    @Size(max = 255)
    private String vendorName;

    @Size(max = 50)
    private String UOMCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSapCode() {
        return sapCode;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getWhsCode() {
        return whsCode;
    }

    public void setWhsCode(String whsCode) {
        this.whsCode = whsCode;
    }

    public String getUserData5() {
        return userData5;
    }

    public void setUserData5(String userData5) {
        this.userData5 = userData5;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Integer getQuantityByPo() {
        return quantityByPo;
    }

    public void setQuantityByPo(Integer quantityByPo) {
        this.quantityByPo = quantityByPo;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getUOMCode() {
        return UOMCode;
    }

    public void setUOMCode(String UOMCode) {
        this.UOMCode = UOMCode;
    }
}
