package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Entity PoDetail
 */
@Entity
@Table(name = "po_detail")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PoDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequenceGenerator"
    )
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 20)
    @Column(name = "sap_code", length = 20)
    private String sapCode;

    @Size(max = 255)
    @Column(name = "sap_name", length = 255)
    private String sapName;

    @Column(name = "quantity_container")
    private Integer quantityContainer;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Size(max = 50)
    @Column(name = "part_number", length = 50)
    private String partNumber;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "poDetail")
    @JsonIgnoreProperties(value = { "poDetail" }, allowSetters = true)
    private Set<VendorTemDetail> vendorTemDetails = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "poDetails" }, allowSetters = true)
    private ImportVendorTemTransactions importVendorTemTransactions;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PoDetail id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSapCode() {
        return this.sapCode;
    }

    public PoDetail sapCode(String sapCode) {
        this.setSapCode(sapCode);
        return this;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public String getSapName() {
        return this.sapName;
    }

    public PoDetail sapName(String sapName) {
        this.setSapName(sapName);
        return this;
    }

    public void setSapName(String sapName) {
        this.sapName = sapName;
    }

    public Integer getQuantityContainer() {
        return this.quantityContainer;
    }

    public PoDetail quantityContainer(Integer quantityContainer) {
        this.setQuantityContainer(quantityContainer);
        return this;
    }

    public void setQuantityContainer(Integer quantityContainer) {
        this.quantityContainer = quantityContainer;
    }

    public Integer getTotalQuantity() {
        return this.totalQuantity;
    }

    public PoDetail totalQuantity(Integer totalQuantity) {
        this.setTotalQuantity(totalQuantity);
        return this;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getPartNumber() {
        return this.partNumber;
    }

    public PoDetail partNumber(String partNumber) {
        this.setPartNumber(partNumber);
        return this;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public Set<VendorTemDetail> getVendorTemDetails() {
        return this.vendorTemDetails;
    }

    public void setVendorTemDetails(Set<VendorTemDetail> vendorTemDetails) {
        if (this.vendorTemDetails != null) {
            this.vendorTemDetails.forEach(i -> i.setPoDetail(null));
        }
        if (vendorTemDetails != null) {
            vendorTemDetails.forEach(i -> i.setPoDetail(this));
        }
        this.vendorTemDetails = vendorTemDetails;
    }

    public PoDetail vendorTemDetails(Set<VendorTemDetail> vendorTemDetails) {
        this.setVendorTemDetails(vendorTemDetails);
        return this;
    }

    public PoDetail addVendorTemDetail(VendorTemDetail vendorTemDetail) {
        this.vendorTemDetails.add(vendorTemDetail);
        vendorTemDetail.setPoDetail(this);
        return this;
    }

    public PoDetail removeVendorTemDetail(VendorTemDetail vendorTemDetail) {
        this.vendorTemDetails.remove(vendorTemDetail);
        vendorTemDetail.setPoDetail(null);
        return this;
    }

    public ImportVendorTemTransactions getImportVendorTemTransactions() {
        return this.importVendorTemTransactions;
    }

    public void setImportVendorTemTransactions(
        ImportVendorTemTransactions importVendorTemTransactions
    ) {
        this.importVendorTemTransactions = importVendorTemTransactions;
    }

    public PoDetail importVendorTemTransactions(
        ImportVendorTemTransactions importVendorTemTransactions
    ) {
        this.setImportVendorTemTransactions(importVendorTemTransactions);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PoDetail)) {
            return false;
        }
        return getId() != null && getId().equals(((PoDetail) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PoDetail{" +
            "id=" + getId() +
            ", sapCode='" + getSapCode() + "'" +
            ", sapName='" + getSapName() + "'" +
            ", quantityContainer=" + getQuantityContainer() +
            ", totalQuantity=" + getTotalQuantity() +
            ", partNumber='" + getPartNumber() + "'" +
            "}";
    }
}
