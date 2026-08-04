package com.mycompany.myapp.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "sap_por1")
public class SapPor1 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequenceGenerator"
    )
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 255)
    @Column(name = "LineNum", length = 255)
    private String lineNum;

    @Size(max = 255)
    @Column(name = "BaseRef", length = 255)
    private String baseRef;

    @Size(max = 255)
    @Column(name = "BaseEntry", length = 255)
    private String baseEntry;

    @Size(max = 255)
    @Column(name = "BaseLine", length = 255)
    private String baseLine;

    @Size(max = 255)
    @Column(name = "LineStatus", length = 255)
    private String lineStatus;

    @Size(max = 255)
    @Column(name = "ItemCode", length = 255)
    private String itemCode;

    @Size(max = 255)
    @Column(name = "Dscription", length = 255)
    private String dscription;

    @Column(name = "Quantity", precision = 18, scale = 3)
    private BigDecimal quantity;

    @Column(name = "ShipDate")
    private Instant shipDate;

    @Size(max = 255)
    @Column(name = "Price", length = 255)
    private String price;

    @Size(max = 255)
    @Column(name = "Currency", length = 255)
    private String currency;

    @Size(max = 255)
    @Column(name = "DiscPrcnt", length = 255)
    private String discPrcnt;

    @Size(max = 255)
    @Column(name = "TotalSumSy", length = 255)
    private String totalSumSy;

    @Size(max = 255)
    @Column(name = "OpenSumSys", length = 255)
    private String openSumSys;

    @Size(max = 255)
    @Column(name = "InvntSttus", length = 255)
    private String invntSttus;

    @Size(max = 255)
    @Column(name = "BaseDocNum", length = 255)
    private String baseDocNum;

    @Size(max = 255)
    @Column(name = "U_Tenkythuat", length = 255)
    private String uTenkythuat;

    @Size(max = 255)
    @Column(name = "U_SO", length = 255)
    private String uSo;

    @Size(max = 255)
    @Column(name = "U_MCode", length = 255)
    private String uMCode;

    @Size(max = 255)
    @Column(name = "DocEntry", length = 255)
    private String docEntry;

    @Column(name = "TotalFrgn")
    private Double totalFrgn;

    @Size(max = 255)
    @Column(name = "VatGroup", length = 255)
    private String vatGroup;

    @Size(max = 255)
    @Column(name = "UOMCode", length = 255)
    private String uomCode;

    @Size(max = 255)
    @Column(name = "UnitMsr", length = 255)
    private String unitMsr;

    @Size(max = 255)
    @Column(name = "LineVendor", length = 255)
    private String lineVendor;

    @Size(max = 255)
    @Column(name = "TrgetEntry", length = 255)
    private String trgetEntry;

    @Column(name = "LineTotal", precision = 18, scale = 0)
    private BigDecimal lineTotal;

    @Column(name = "VatPrcnt", precision = 18, scale = 0)
    private BigDecimal vatPrcnt;

    @Column(name = "PriceAfVat", precision = 18, scale = 0)
    private BigDecimal priceAfVat;

    @Size(max = 255)
    @Column(name = "WhsCode", length = 255)
    private String whsCode;

    // --- GETTERS & SETTERS ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLineNum() {
        return lineNum;
    }

    public void setLineNum(String lineNum) {
        this.lineNum = lineNum;
    }

    public String getBaseRef() {
        return baseRef;
    }

    public void setBaseRef(String baseRef) {
        this.baseRef = baseRef;
    }

    public String getBaseEntry() {
        return baseEntry;
    }

    public void setBaseEntry(String baseEntry) {
        this.baseEntry = baseEntry;
    }

    public String getBaseLine() {
        return baseLine;
    }

    public void setBaseLine(String baseLine) {
        this.baseLine = baseLine;
    }

    public String getLineStatus() {
        return lineStatus;
    }

    public void setLineStatus(String lineStatus) {
        this.lineStatus = lineStatus;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDscription() {
        return dscription;
    }

    public void setDscription(String dscription) {
        this.dscription = dscription;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Instant getShipDate() {
        return shipDate;
    }

    public void setShipDate(Instant shipDate) {
        this.shipDate = shipDate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDiscPrcnt() {
        return discPrcnt;
    }

    public void setDiscPrcnt(String discPrcnt) {
        this.discPrcnt = discPrcnt;
    }

    public String getTotalSumSy() {
        return totalSumSy;
    }

    public void setTotalSumSy(String totalSumSy) {
        this.totalSumSy = totalSumSy;
    }

    public String getOpenSumSys() {
        return openSumSys;
    }

    public void setOpenSumSys(String openSumSys) {
        this.openSumSys = openSumSys;
    }

    public String getInvntSttus() {
        return invntSttus;
    }

    public void setInvntSttus(String invntSttus) {
        this.invntSttus = invntSttus;
    }

    public String getBaseDocNum() {
        return baseDocNum;
    }

    public void setBaseDocNum(String baseDocNum) {
        this.baseDocNum = baseDocNum;
    }

    public String getUTenkythuat() {
        return uTenkythuat;
    }

    public void setUTenkythuat(String uTenkythuat) {
        this.uTenkythuat = uTenkythuat;
    }

    public String getUSo() {
        return uSo;
    }

    public void setUSo(String uSo) {
        this.uSo = uSo;
    }

    public String getUMCode() {
        return uMCode;
    }

    public void setUMCode(String uMCode) {
        this.uMCode = uMCode;
    }

    public String getDocEntry() {
        return docEntry;
    }

    public void setDocEntry(String docEntry) {
        this.docEntry = docEntry;
    }

    public Double getTotalFrgn() {
        return totalFrgn;
    }

    public void setTotalFrgn(Double totalFrgn) {
        this.totalFrgn = totalFrgn;
    }

    public String getVatGroup() {
        return vatGroup;
    }

    public void setVatGroup(String vatGroup) {
        this.vatGroup = vatGroup;
    }

    public String getUomCode() {
        return uomCode;
    }

    public void setUomCode(String uomCode) {
        this.uomCode = uomCode;
    }

    public String getUnitMsr() {
        return unitMsr;
    }

    public void setUnitMsr(String unitMsr) {
        this.unitMsr = unitMsr;
    }

    public String getLineVendor() {
        return lineVendor;
    }

    public void setLineVendor(String lineVendor) {
        this.lineVendor = lineVendor;
    }

    public String getTrgetEntry() {
        return trgetEntry;
    }

    public void setTrgetEntry(String trgetEntry) {
        this.trgetEntry = trgetEntry;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public BigDecimal getVatPrcnt() {
        return vatPrcnt;
    }

    public void setVatPrcnt(BigDecimal vatPrcnt) {
        this.vatPrcnt = vatPrcnt;
    }

    public BigDecimal getPriceAfVat() {
        return priceAfVat;
    }

    public void setPriceAfVat(BigDecimal priceAfVat) {
        this.priceAfVat = priceAfVat;
    }

    public String getWhsCode() {
        return whsCode;
    }

    public void setWhsCode(String whsCode) {
        this.whsCode = whsCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SapPor1)) {
            return false;
        }
        return getId() != null && getId().equals(((SapPor1) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "SapPor1{" +
            "id=" +
            getId() +
            ", lineNum='" +
            getLineNum() +
            "'" +
            ", baseRef='" +
            getBaseRef() +
            "'" +
            ", baseEntry='" +
            getBaseEntry() +
            "'" +
            ", baseLine='" +
            getBaseLine() +
            "'" +
            ", lineStatus='" +
            getLineStatus() +
            "'" +
            ", itemCode='" +
            getItemCode() +
            "'" +
            ", dscription='" +
            getDscription() +
            "'" +
            ", quantity=" +
            getQuantity() +
            ", shipDate='" +
            getShipDate() +
            "'" +
            ", price='" +
            getPrice() +
            "'" +
            ", currency='" +
            getCurrency() +
            "'" +
            ", discPrcnt='" +
            getDiscPrcnt() +
            "'" +
            ", totalSumSy='" +
            getTotalSumSy() +
            "'" +
            ", openSumSys='" +
            getOpenSumSys() +
            "'" +
            ", invntSttus='" +
            getInvntSttus() +
            "'" +
            ", baseDocNum='" +
            getBaseDocNum() +
            "'" +
            ", uTenkythuat='" +
            getUTenkythuat() +
            "'" +
            ", uSo='" +
            getUSo() +
            "'" +
            ", uMCode='" +
            getUMCode() +
            "'" +
            ", docEntry='" +
            getDocEntry() +
            "'" +
            ", totalFrgn=" +
            getTotalFrgn() +
            ", vatGroup='" +
            getVatGroup() +
            "'" +
            ", uomCode='" +
            getUomCode() +
            "'" +
            ", unitMsr='" +
            getUnitMsr() +
            "'" +
            ", lineVendor='" +
            getLineVendor() +
            "'" +
            ", trgetEntry='" +
            getTrgetEntry() +
            "'" +
            ", lineTotal=" +
            getLineTotal() +
            ", vatPrcnt=" +
            getVatPrcnt() +
            ", priceAfVat=" +
            getPriceAfVat() +
            ", whsCode='" +
            getWhsCode() +
            "'" +
            "}"
        );
    }
}
