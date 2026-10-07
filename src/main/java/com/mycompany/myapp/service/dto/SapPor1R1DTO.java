package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.SapPor1R1} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SapPor1R1DTO implements Serializable {

    private Long id;

    @Size(max = 255)
    private String lineNum;

    @Size(max = 255)
    private String baseRef;

    @Size(max = 255)
    private String baseEntry;

    @Size(max = 255)
    private String baseLine;

    @Size(max = 255)
    private String lineStatus;

    @Size(max = 255)
    private String itemCode;

    @Size(max = 255)
    private String dscription;

    private BigDecimal quantity;

    private Instant shipDate;

    @Size(max = 255)
    private String price;

    @Size(max = 255)
    private String currency;

    @Size(max = 255)
    private String discPrcnt;

    @Size(max = 255)
    private String totalSumSy;

    @Size(max = 255)
    private String openSumSys;

    @Size(max = 255)
    private String invntSttus;

    @Size(max = 255)
    private String baseDocNum;

    @Size(max = 255)
    @JsonAlias({ "getuTenkythuat", "u_tenkythuat", "UTenkythuat" })
    private String uTenkythuat;

    @Size(max = 255)
    @JsonAlias({ "getuSo", "u_so", "USo" })
    private String uSo;

    @Size(max = 255)
    @JsonAlias({ "getuMCode", "u_mcode", "UMCode" })
    private String uMCode;

    @Size(max = 255)
    private String docEntry;

    private Double totalFrgn;

    @Size(max = 255)
    private String vatGroup;

    @Size(max = 255)
    private String uomCode;

    @Size(max = 255)
    private String unitMsr;

    @Size(max = 255)
    private String lineVendor;

    @Size(max = 255)
    private String trgetEntry;

    private BigDecimal lineTotal;

    private BigDecimal vatPrcnt;

    private BigDecimal priceAfVat;

    @Size(max = 255)
    private String whsCode;

    private DeliveryNotificationDTO deliveryNotification;

    @JsonIgnore
    private Long deliveryNotificationId;

    @JsonProperty("deliveryNotificationId")
    public void setDeliveryNotificationId(Long deliveryNotificationId) {
        this.deliveryNotificationId = deliveryNotificationId;
        if (deliveryNotificationId == null) {
            this.deliveryNotification = null;
        } else if (this.deliveryNotification == null) {
            this.deliveryNotification = new DeliveryNotificationDTO();
            this.deliveryNotification.setId(deliveryNotificationId);
        } else {
            this.deliveryNotification.setId(deliveryNotificationId);
        }
    }

    @JsonIgnore
    public Long getDeliveryNotificationId() {
        return deliveryNotificationId;
    }

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

    public String getuTenkythuat() {
        return uTenkythuat;
    }

    public void setuTenkythuat(String uTenkythuat) {
        this.uTenkythuat = uTenkythuat;
    }

    public String getuSo() {
        return uSo;
    }

    public void setuSo(String uSo) {
        this.uSo = uSo;
    }

    public String getuMCode() {
        return uMCode;
    }

    public void setuMCode(String uMCode) {
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

    public DeliveryNotificationDTO getDeliveryNotification() {
        return deliveryNotification;
    }

    public void setDeliveryNotification(
        DeliveryNotificationDTO deliveryNotification
    ) {
        this.deliveryNotification = deliveryNotification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SapPor1R1DTO)) {
            return false;
        }

        SapPor1R1DTO sapPor1R1DTO = (SapPor1R1DTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, sapPor1R1DTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SapPor1R1DTO{" +
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
            ", deliveryNotification=" + getDeliveryNotification() +
            "}";
    }
}
