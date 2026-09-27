package com.mycompany.myapp.domain;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A SapPor1R1.
 */
@Entity
@Table(name = "sap_por1_r1")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SapPor1R1 implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Size(max = 255)
  @Column(name = "line_num", length = 255)
  private String lineNum;

  @Size(max = 255)
  @Column(name = "base_ref", length = 255)
  private String baseRef;

  @Size(max = 255)
  @Column(name = "base_entry", length = 255)
  private String baseEntry;

  @Size(max = 255)
  @Column(name = "base_line", length = 255)
  private String baseLine;

  @Size(max = 255)
  @Column(name = "line_status", length = 255)
  private String lineStatus;

  @Size(max = 255)
  @Column(name = "item_code", length = 255)
  private String itemCode;

  @Size(max = 255)
  @Column(name = "dscription", length = 255)
  private String dscription;

  @Column(name = "quantity", precision = 21, scale = 2)
  private BigDecimal quantity;

  @Column(name = "ship_date")
  private Instant shipDate;

  @Size(max = 255)
  @Column(name = "price", length = 255)
  private String price;

  @Size(max = 255)
  @Column(name = "currency", length = 255)
  private String currency;

  @Size(max = 255)
  @Column(name = "disc_prcnt", length = 255)
  private String discPrcnt;

  @Size(max = 255)
  @Column(name = "total_sum_sy", length = 255)
  private String totalSumSy;

  @Size(max = 255)
  @Column(name = "open_sum_sys", length = 255)
  private String openSumSys;

  @Size(max = 255)
  @Column(name = "invnt_sttus", length = 255)
  private String invntSttus;

  @Size(max = 255)
  @Column(name = "base_doc_num", length = 255)
  private String baseDocNum;

  @Size(max = 255)
  @Column(name = "u_tenkythuat", length = 255)
  private String uTenkythuat;

  @Size(max = 255)
  @Column(name = "u_so", length = 255)
  private String uSo;

  @Size(max = 255)
  @Column(name = "u_mcode", length = 255)
  private String uMCode;

  @Size(max = 255)
  @Column(name = "doc_entry", length = 255)
  private String docEntry;

  @Column(name = "total_frgn")
  private Double totalFrgn;

  @Size(max = 255)
  @Column(name = "vat_group", length = 255)
  private String vatGroup;

  @Size(max = 255)
  @Column(name = "uom_code", length = 255)
  private String uomCode;

  @Size(max = 255)
  @Column(name = "unit_msr", length = 255)
  private String unitMsr;

  @Size(max = 255)
  @Column(name = "line_vendor", length = 255)
  private String lineVendor;

  @Size(max = 255)
  @Column(name = "trget_entry", length = 255)
  private String trgetEntry;

  @Column(name = "line_total", precision = 21, scale = 2)
  private BigDecimal lineTotal;

  @Column(name = "vat_prcnt", precision = 21, scale = 2)
  private BigDecimal vatPrcnt;

  @Column(name = "price_af_vat", precision = 21, scale = 2)
  private BigDecimal priceAfVat;

  @Size(max = 255)
  @Column(name = "whs_code", length = 255)
  private String whsCode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delivery_notification_id", referencedColumnName = "id")
  private DeliveryNotification deliveryNotification;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public SapPor1R1 id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLineNum() {
    return this.lineNum;
  }

  public SapPor1R1 lineNum(String lineNum) {
    this.setLineNum(lineNum);
    return this;
  }

  public void setLineNum(String lineNum) {
    this.lineNum = lineNum;
  }

  public String getBaseRef() {
    return this.baseRef;
  }

  public SapPor1R1 baseRef(String baseRef) {
    this.setBaseRef(baseRef);
    return this;
  }

  public void setBaseRef(String baseRef) {
    this.baseRef = baseRef;
  }

  public String getBaseEntry() {
    return this.baseEntry;
  }

  public SapPor1R1 baseEntry(String baseEntry) {
    this.setBaseEntry(baseEntry);
    return this;
  }

  public void setBaseEntry(String baseEntry) {
    this.baseEntry = baseEntry;
  }

  public String getBaseLine() {
    return this.baseLine;
  }

  public SapPor1R1 baseLine(String baseLine) {
    this.setBaseLine(baseLine);
    return this;
  }

  public void setBaseLine(String baseLine) {
    this.baseLine = baseLine;
  }

  public String getLineStatus() {
    return this.lineStatus;
  }

  public SapPor1R1 lineStatus(String lineStatus) {
    this.setLineStatus(lineStatus);
    return this;
  }

  public void setLineStatus(String lineStatus) {
    this.lineStatus = lineStatus;
  }

  public String getItemCode() {
    return this.itemCode;
  }

  public SapPor1R1 itemCode(String itemCode) {
    this.setItemCode(itemCode);
    return this;
  }

  public void setItemCode(String itemCode) {
    this.itemCode = itemCode;
  }

  public String getDscription() {
    return this.dscription;
  }

  public SapPor1R1 dscription(String dscription) {
    this.setDscription(dscription);
    return this;
  }

  public void setDscription(String dscription) {
    this.dscription = dscription;
  }

  public BigDecimal getQuantity() {
    return this.quantity;
  }

  public SapPor1R1 quantity(BigDecimal quantity) {
    this.setQuantity(quantity);
    return this;
  }

  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }

  public Instant getShipDate() {
    return this.shipDate;
  }

  public SapPor1R1 shipDate(Instant shipDate) {
    this.setShipDate(shipDate);
    return this;
  }

  public void setShipDate(Instant shipDate) {
    this.shipDate = shipDate;
  }

  public String getPrice() {
    return this.price;
  }

  public SapPor1R1 price(String price) {
    this.setPrice(price);
    return this;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public String getCurrency() {
    return this.currency;
  }

  public SapPor1R1 currency(String currency) {
    this.setCurrency(currency);
    return this;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getDiscPrcnt() {
    return this.discPrcnt;
  }

  public SapPor1R1 discPrcnt(String discPrcnt) {
    this.setDiscPrcnt(discPrcnt);
    return this;
  }

  public void setDiscPrcnt(String discPrcnt) {
    this.discPrcnt = discPrcnt;
  }

  public String getTotalSumSy() {
    return this.totalSumSy;
  }

  public SapPor1R1 totalSumSy(String totalSumSy) {
    this.setTotalSumSy(totalSumSy);
    return this;
  }

  public void setTotalSumSy(String totalSumSy) {
    this.totalSumSy = totalSumSy;
  }

  public String getOpenSumSys() {
    return this.openSumSys;
  }

  public SapPor1R1 openSumSys(String openSumSys) {
    this.setOpenSumSys(openSumSys);
    return this;
  }

  public void setOpenSumSys(String openSumSys) {
    this.openSumSys = openSumSys;
  }

  public String getInvntSttus() {
    return this.invntSttus;
  }

  public SapPor1R1 invntSttus(String invntSttus) {
    this.setInvntSttus(invntSttus);
    return this;
  }

  public void setInvntSttus(String invntSttus) {
    this.invntSttus = invntSttus;
  }

  public String getBaseDocNum() {
    return this.baseDocNum;
  }

  public SapPor1R1 baseDocNum(String baseDocNum) {
    this.setBaseDocNum(baseDocNum);
    return this;
  }

  public void setBaseDocNum(String baseDocNum) {
    this.baseDocNum = baseDocNum;
  }

  public String getuTenkythuat() {
    return this.uTenkythuat;
  }

  public SapPor1R1 uTenkythuat(String uTenkythuat) {
    this.setuTenkythuat(uTenkythuat);
    return this;
  }

  public void setuTenkythuat(String uTenkythuat) {
    this.uTenkythuat = uTenkythuat;
  }

  public String getuSo() {
    return this.uSo;
  }

  public SapPor1R1 uSo(String uSo) {
    this.setuSo(uSo);
    return this;
  }

  public void setuSo(String uSo) {
    this.uSo = uSo;
  }

  public String getuMCode() {
    return this.uMCode;
  }

  public SapPor1R1 uMCode(String uMCode) {
    this.setuMCode(uMCode);
    return this;
  }

  public void setuMCode(String uMCode) {
    this.uMCode = uMCode;
  }

  public String getDocEntry() {
    return this.docEntry;
  }

  public SapPor1R1 docEntry(String docEntry) {
    this.setDocEntry(docEntry);
    return this;
  }

  public void setDocEntry(String docEntry) {
    this.docEntry = docEntry;
  }

  public Double getTotalFrgn() {
    return this.totalFrgn;
  }

  public SapPor1R1 totalFrgn(Double totalFrgn) {
    this.setTotalFrgn(totalFrgn);
    return this;
  }

  public void setTotalFrgn(Double totalFrgn) {
    this.totalFrgn = totalFrgn;
  }

  public String getVatGroup() {
    return this.vatGroup;
  }

  public SapPor1R1 vatGroup(String vatGroup) {
    this.setVatGroup(vatGroup);
    return this;
  }

  public void setVatGroup(String vatGroup) {
    this.vatGroup = vatGroup;
  }

  public String getUomCode() {
    return this.uomCode;
  }

  public SapPor1R1 uomCode(String uomCode) {
    this.setUomCode(uomCode);
    return this;
  }

  public void setUomCode(String uomCode) {
    this.uomCode = uomCode;
  }

  public String getUnitMsr() {
    return this.unitMsr;
  }

  public SapPor1R1 unitMsr(String unitMsr) {
    this.setUnitMsr(unitMsr);
    return this;
  }

  public void setUnitMsr(String unitMsr) {
    this.unitMsr = unitMsr;
  }

  public String getLineVendor() {
    return this.lineVendor;
  }

  public SapPor1R1 lineVendor(String lineVendor) {
    this.setLineVendor(lineVendor);
    return this;
  }

  public void setLineVendor(String lineVendor) {
    this.lineVendor = lineVendor;
  }

  public String getTrgetEntry() {
    return this.trgetEntry;
  }

  public SapPor1R1 trgetEntry(String trgetEntry) {
    this.setTrgetEntry(trgetEntry);
    return this;
  }

  public void setTrgetEntry(String trgetEntry) {
    this.trgetEntry = trgetEntry;
  }

  public BigDecimal getLineTotal() {
    return this.lineTotal;
  }

  public SapPor1R1 lineTotal(BigDecimal lineTotal) {
    this.setLineTotal(lineTotal);
    return this;
  }

  public void setLineTotal(BigDecimal lineTotal) {
    this.lineTotal = lineTotal;
  }

  public BigDecimal getVatPrcnt() {
    return this.vatPrcnt;
  }

  public SapPor1R1 vatPrcnt(BigDecimal vatPrcnt) {
    this.setVatPrcnt(vatPrcnt);
    return this;
  }

  public void setVatPrcnt(BigDecimal vatPrcnt) {
    this.vatPrcnt = vatPrcnt;
  }

  public BigDecimal getPriceAfVat() {
    return this.priceAfVat;
  }

  public SapPor1R1 priceAfVat(BigDecimal priceAfVat) {
    this.setPriceAfVat(priceAfVat);
    return this;
  }

  public void setPriceAfVat(BigDecimal priceAfVat) {
    this.priceAfVat = priceAfVat;
  }

  public String getWhsCode() {
    return this.whsCode;
  }

  public SapPor1R1 whsCode(String whsCode) {
    this.setWhsCode(whsCode);
    return this;
  }

  public void setWhsCode(String whsCode) {
    this.whsCode = whsCode;
  }

  public DeliveryNotification getDeliveryNotification() {
    return this.deliveryNotification;
  }

  public void setDeliveryNotification(
    DeliveryNotification deliveryNotification
  ) {
    this.deliveryNotification = deliveryNotification;
  }

  public SapPor1R1 deliveryNotification(
    DeliveryNotification deliveryNotification
  ) {
    this.setDeliveryNotification(deliveryNotification);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SapPor1R1)) {
      return false;
    }
    return getId() != null && getId().equals(((SapPor1R1) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "SapPor1R1{" +
            "id=" + getId() +
            ", lineNum='" + getLineNum() + "'" +
            ", baseRef='" + getBaseRef() + "'" +
            ", baseEntry='" + getBaseEntry() + "'" +
            ", baseLine='" + getBaseLine() + "'" +
            ", lineStatus='" + getLineStatus() + "'" +
            ", itemCode='" + getItemCode() + "'" +
            ", dscription='" + getDscription() + "'" +
            ", quantity=" + getQuantity() +
            ", shipDate='" + getShipDate() + "'" +
            ", price='" + getPrice() + "'" +
            ", currency='" + getCurrency() + "'" +
            ", discPrcnt='" + getDiscPrcnt() + "'" +
            ", totalSumSy='" + getTotalSumSy() + "'" +
            ", openSumSys='" + getOpenSumSys() + "'" +
            ", invntSttus='" + getInvntSttus() + "'" +
            ", baseDocNum='" + getBaseDocNum() + "'" +
            ", uTenkythuat='" + getuTenkythuat() + "'" +
            ", uSo='" + getuSo() + "'" +
            ", uMCode='" + getuMCode() + "'" +
            ", docEntry='" + getDocEntry() + "'" +
            ", totalFrgn=" + getTotalFrgn() +
            ", vatGroup='" + getVatGroup() + "'" +
            ", uomCode='" + getUomCode() + "'" +
            ", unitMsr='" + getUnitMsr() + "'" +
            ", lineVendor='" + getLineVendor() + "'" +
            ", trgetEntry='" + getTrgetEntry() + "'" +
            ", lineTotal=" + getLineTotal() +
            ", vatPrcnt=" + getVatPrcnt() +
            ", priceAfVat=" + getPriceAfVat() +
            ", whsCode='" + getWhsCode() + "'" +
            "}";
    }
}
