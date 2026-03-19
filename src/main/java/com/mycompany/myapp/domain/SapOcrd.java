package com.mycompany.myapp.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Entity SapOcrd
 */
@Entity
@Table(name = "SAP_OCRD")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SapOcrd implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequenceGenerator"
    )
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 510)
    @Column(name = "CardCode", length = 510)
    private String cardCode;

    @Size(max = 510)
    @Column(name = "CardType", length = 510)
    private String cardType;

    @Size(max = 510)
    @Column(name = "CardName", length = 510)
    private String cardName;

    @Size(max = 510)
    @Column(name = "CardFName", length = 510)
    private String cardFName;

    @Size(max = 510)
    @Column(name = "GroupCode", length = 510)
    private String groupCode;

    @Size(max = 510)
    @Column(name = "Currency", length = 510)
    private String currency;

    @Size(max = 510)
    @Column(name = "LicTradNum", length = 510)
    private String licTradNum;

    @Size(max = 510)
    @Column(name = "AddID", length = 510)
    private String addId;

    @Size(max = 510)
    @Column(name = "E_Mail", length = 510)
    private String eMail;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SapOcrd id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardCode() {
        return this.cardCode;
    }

    public SapOcrd cardCode(String cardCode) {
        this.setCardCode(cardCode);
        return this;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getCardType() {
        return this.cardType;
    }

    public SapOcrd cardType(String cardType) {
        this.setCardType(cardType);
        return this;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardName() {
        return this.cardName;
    }

    public SapOcrd cardName(String cardName) {
        this.setCardName(cardName);
        return this;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardFName() {
        return this.cardFName;
    }

    public SapOcrd cardFName(String cardFName) {
        this.setCardFName(cardFName);
        return this;
    }

    public void setCardFName(String cardFName) {
        this.cardFName = cardFName;
    }

    public String getGroupCode() {
        return this.groupCode;
    }

    public SapOcrd groupCode(String groupCode) {
        this.setGroupCode(groupCode);
        return this;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getCurrency() {
        return this.currency;
    }

    public SapOcrd currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLicTradNum() {
        return this.licTradNum;
    }

    public SapOcrd licTradNum(String licTradNum) {
        this.setLicTradNum(licTradNum);
        return this;
    }

    public void setLicTradNum(String licTradNum) {
        this.licTradNum = licTradNum;
    }

    public String getaddId() {
        return this.addId;
    }

    public SapOcrd addId(String addId) {
        this.setaddId(addId);
        return this;
    }

    public void setaddId(String addId) {
        this.addId = addId;
    }

    public String geteMail() {
        return this.eMail;
    }

    public SapOcrd eMail(String eMail) {
        this.seteMail(eMail);
        return this;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SapOcrd)) {
            return false;
        }
        return getId() != null && getId().equals(((SapOcrd) o).getId());
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SapOcrd{" +
                "id=" + getId() +
                ", cardCode='" + getCardCode() + "'" +
                ", cardType='" + getCardType() + "'" +
                ", cardName='" + getCardName() + "'" +
                ", cardFName='" + getCardFName() + "'" +
                ", groupCode='" + getGroupCode() + "'" +
                ", currency='" + getCurrency() + "'" +
                ", licTradNum='" + getLicTradNum() + "'" +
                ", addId='" + getaddId() + "'" +
                ", eMail='" + geteMail() + "'" +
                "}";
    }
}
