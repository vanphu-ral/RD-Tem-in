package com.mycompany.myapp.domain;

import java.time.ZonedDateTime;
import javax.persistence.*;

@Entity
@Table(name = "Scan_products")
public class scanProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_version")
    private String productVersion;

    @Column(name = "created_at")
    private String createAt;

    @Column(name = "update_at")
    private String updateAt;

    @Column(name = "username")
    private String username;

    @Column(name = "product_status")
    private Integer productStatus;

    public scanProduct(
        Long productId,
        String productCode,
        String productName,
        String productVersion,
        String createAt,
        String updateAt,
        String username,
        Integer productStatus
    ) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.productVersion = productVersion;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.username = username;
        this.productStatus = productStatus;
    }

    public scanProduct() {}

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(Integer productStatus) {
        this.productStatus = productStatus;
    }
}
