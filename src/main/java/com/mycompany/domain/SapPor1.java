package com.mycompany.domain;

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

    public Long getId() {
        return id;
    }

    public SapPor1 id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLineNum() {
        return lineNum;
    }

    public SapPor1 lineNum(String lineNum) {
        this.setLineNum(lineNum);
        return this;
    }

    public void setLineNum(String lineNum) {
        this.lineNum = lineNum;
    }

    public String getBaseRef() {
        return baseRef;
    }

    public SapPor1 baseRef(String baseRef) {
        this.setBaseRef(baseRef);
        return this;
    }

    public void setBaseRef(String baseRef) {
        this.baseRef = baseRef;
    }

    public String getBaseEntry() {
        return baseEntry;
    }

    public SapPor1 baseEntry(String baseEntry) {
        this.setBaseEntry(baseEntry);
        return this;
    }

    public void setBaseEntry(String baseEntry) {
        this.baseEntry = baseEntry;
    }

    public String getBaseLine() {
        return baseLine;
    }

    public SapPor1 baseLine(String baseLine) {
        this.setBaseLine(baseLine);
        return this;
    }

    public void setBaseLine(String baseLine) {
        this.baseLine = baseLine;
    }

    public String getLineStatus() {
        return lineStatus;
    }

    public SapPor1 lineStatus(String lineStatus) {
        this.setLineStatus(lineStatus);
        return this;
    }

    public void setLineStatus(String lineStatus) {
        this.lineStatus = lineStatus;
    }

    public String getItemCode() {
        return itemCode;
    }

    public SapPor1 itemCode(String itemCode) {
        this.setItemCode(itemCode);
        return this;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDscription() {
        return dscription;
    }

    public SapPor1 dscription(String dscription) {
        this.setDscription(dscription);
        return this;
    }

    public void setDscription(String dscription) {
        this.dscription = dscription;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public SapPor1 quantity(BigDecimal quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Instant getShipDate() {
        return shipDate;
    }

    public SapPor1 shipDate(Instant shipDate) {
        this.setShipDate(shipDate);
        return this;
    }

    public void setShipDate(Instant shipDate) {
        this.shipDate = shipDate;
    }

    public String getPrice() {
        return price;
    }

    public SapPor1 price(String price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public SapPor1 currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDiscPrcnt() {
        return discPrcnt;
    }

    public SapPor1 discPrcnt(String discPrcnt) {
        this.setDiscPrcnt(discPrcnt);
        return this;
    }

    public void setDiscPrcnt(String discPrcnt) {
        this.discPrcnt = discPrcnt;
    }

    public String getTotalSumSy() {
        return totalSumSy;
    }

    public SapPor1 totalSumSy(String totalSumSy) {
        this.setTotalSumSy(totalSumSy);
        return this;
    }

    public void setTotalSumSy(String totalSumSy) {
        this.totalSumSy = totalSumSy;
    }

    public String getOpenSumSys() {
        return openSumSys;
    }

    public SapPor1 openSumSys(String openSumSys) {
        this.setOpenSumSys(openSumSys);
        return this;
    }

    public void setOpenSumSys(String openSumSys) {
        this.openSumSys = openSumSys;
    }

    public String getInvntSttus() {
        return invntSttus;
    }

    public SapPor1 invntSttus(String invntSttus) {
        this.setInvntSttus(invntSttus);
        return this;
    }

    public void setInvntSttus(String invntSttus) {
        this.invntSttus = invntSttus;
    }

    public String getBaseDocNum() {
        return baseDocNum;
    }

    public SapPor1 baseDocNum(String baseDocNum) {
        this.setBaseDocNum(baseDocNum);
        return this;
    }

    public void setBaseDocNum(String baseDocNum) {
        this.baseDocNum = baseDocNum;
    }

    public String getUTenkythuat() {
        return uTenkythuat;
    }

    public SapPor1 uTenkythuat(String uTenkythuat) {
        this.setUTenkythuat(uTenkythuat);
        return this;
    }

    public void setUTenkythuat(String uTenkythuat) {
        this.uTenkythuat = uTenkythuat;
    }

    public String getUSo() {
        return uSo;
    }

    public SapPor1 uSo(String uSo) {
        this.setUSo(uSo);
        return this;
    }

    public void setUSo(String uSo) {
        this.uSo = uSo;
    }

    public String getUMCode() {
        return uMCode;
    }

    public SapPor1 uMCode(String uMCode) {
        this.setUMCode(uMCode);
        return this;
    }

    public void setUMCode(String uMCode) {
        this.uMCode = uMCode;
    }

    public String getDocEntry() {
        return docEntry;
    }

    public SapPor1 docEntry(String docEntry) {
        this.setDocEntry(docEntry);
        return this;
    }

    public void setDocEntry(String docEntry) {
        this.docEntry = docEntry;
    }

    public Double getTotalFrgn() {
        return totalFrgn;
    }

    public SapPor1 totalFrgn(Double totalFrgn) {
        this.setTotalFrgn(totalFrgn);
        return this;
    }

    public void setTotalFrgn(Double totalFrgn) {
        this.totalFrgn = totalFrgn;
    }

    public String getVatGroup() {
        return vatGroup;
    }

    public SapPor1 vatGroup(String vatGroup) {
        this.setVatGroup(vatGroup);
        return this;
    }

    public void setVatGroup(String vatGroup) {
        this.vatGroup = vatGroup;
    }

    public String getUomCode() {
        return uomCode;
    }

    public SapPor1 uomCode(String uomCode) {
        this.setUomCode(uomCode);
        return this;
    }

    public void setUomCode(String uomCode) {
        this.uomCode = uomCode;
    }

    public String getUnitMsr() {
        return unitMsr;
    }

    public SapPor1 unitMsr(String unitMsr) {
        this.setUnitMsr(unitMsr);
        return this;
    }

    public void setUnitMsr(String unitMsr) {
        this.unitMsr = unitMsr;
    }

    public String getLineVendor() {
        return lineVendor;
    }

    public SapPor1 lineVendor(String lineVendor) {
        this.setLineVendor(lineVendor);
        return this;
    }

    public void setLineVendor(String lineVendor) {
        this.lineVendor = lineVendor;
    }

    public String getTrgetEntry() {
        return trgetEntry;
    }

    public SapPor1 trgetEntry(String trgetEntry) {
        this.setTrgetEntry(trgetEntry);
        return this;
    }

    public void setTrgetEntry(String trgetEntry) {
        this.trgetEntry = trgetEntry;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public SapPor1 lineTotal(BigDecimal lineTotal) {
        this.setLineTotal(lineTotal);
        return this;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public BigDecimal getVatPrcnt() {
        return vatPrcnt;
    }

    public SapPor1 vatPrcnt(BigDecimal vatPrcnt) {
        this.setVatPrcnt(vatPrcnt);
        return this;
    }

    public void setVatPrcnt(BigDecimal vatPrcnt) {
        this.vatPrcnt = vatPrcnt;
    }

    public BigDecimal getPriceAfVat() {
        return priceAfVat;
    }

    public SapPor1 priceAfVat(BigDecimal priceAfVat) {
        this.setPriceAfVat(priceAfVat);
        return this;
    }

    public void setPriceAfVat(BigDecimal priceAfVat) {
        this.priceAfVat = priceAfVat;
    }

    public String getWhsCode() {
        return whsCode;
    }

    public SapPor1 whsCode(String whsCode) {
        this.setWhsCode(whsCode);
        return this;
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
