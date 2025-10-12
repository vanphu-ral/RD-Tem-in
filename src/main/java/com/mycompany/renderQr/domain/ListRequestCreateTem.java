package com.mycompany.renderQr.domain;

import javax.persistence.*;

@Entity
@Table(name = "list_request_create_tem")
public class ListRequestCreateTem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Vendor", length = 100)
    private String vendor;

    @Column(name = "UserData5", length = 255)
    private String userData5;

    @Column(name = "Created_by", length = 20)
    private String createdBy;

    @Column(name = "Number_production")
    private Short numberProduction;

    @Column(name = "Total_quantity")
    private Long totalQuantity;

    @Column(name = "Status", length = 20)
    private String status;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Short getNumberProduction() {
        return numberProduction;
    }

    public void setNumberProduction(Short numberProduction) {
        this.numberProduction = numberProduction;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
