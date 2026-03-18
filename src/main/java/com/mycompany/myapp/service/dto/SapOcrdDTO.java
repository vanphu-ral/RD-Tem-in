package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.SapOcrd} entity.
 */
@Schema(description = "Entity SapOcrd")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SapOcrdDTO implements Serializable {

    private Long id;

    @Size(max = 510)
    private String cardCode;

    @Size(max = 510)
    private String cardType;

    @Size(max = 510)
    private String cardName;

    @Size(max = 510)
    private String cardFName;

    @Size(max = 510)
    private String groupCode;

    @Size(max = 510)
    private String currency;

    @Size(max = 510)
    private String licTradNum;

    @Size(max = 510)
    private String dddId;

    @Size(max = 510)
    private String eMail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardFName() {
        return cardFName;
    }

    public void setCardFName(String cardFName) {
        this.cardFName = cardFName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLicTradNum() {
        return licTradNum;
    }

    public void setLicTradNum(String licTradNum) {
        this.licTradNum = licTradNum;
    }

    public String getDddId() {
        return dddId;
    }

    public void setDddId(String dddId) {
        this.dddId = dddId;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SapOcrdDTO)) {
            return false;
        }

        SapOcrdDTO sapOcrdDTO = (SapOcrdDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, sapOcrdDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SapOcrdDTO{" +
            "id=" + getId() +
            ", cardCode='" + getCardCode() + "'" +
            ", cardType='" + getCardType() + "'" +
            ", cardName='" + getCardName() + "'" +
            ", cardFName='" + getCardFName() + "'" +
            ", groupCode='" + getGroupCode() + "'" +
            ", currency='" + getCurrency() + "'" +
            ", licTradNum='" + getLicTradNum() + "'" +
            ", dddId='" + getDddId() + "'" +
            ", eMail='" + geteMail() + "'" +
            "}";
    }
}
