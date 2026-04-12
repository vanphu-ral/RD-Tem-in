package com.mycompany.myapp.domain.partner4;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Entity SapOitm
 */
@Schema(description = "Entity SapOitm")
@Entity
@Table(name = "SAP_OITM")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SapOitm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 510)
    @Column(name = "ItemCode", length = 510)
    private String itemCode;

    @Size(max = 510)
    @Column(name = "ItemName", length = 510)
    private String itemName;

    @Size(max = 510)
    @Column(name = "ItmsGrpCod", length = 510)
    private String itmsGrpCod;

    @Size(max = 510)
    @Column(name = "U_PartNumber", length = 510)
    private String uPartNumber;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SapOitm id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemCode() {
        return this.itemCode;
    }

    public SapOitm itemCode(String itemCode) {
        this.setItemCode(itemCode);
        return this;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return this.itemName;
    }

    public SapOitm itemName(String itemName) {
        this.setItemName(itemName);
        return this;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItmsGrpCod() {
        return this.itmsGrpCod;
    }

    public SapOitm itmsGrpCod(String itmsGrpCod) {
        this.setItmsGrpCod(itmsGrpCod);
        return this;
    }

    public void setItmsGrpCod(String itmsGrpCod) {
        this.itmsGrpCod = itmsGrpCod;
    }

    public String getuPartNumber() {
        return this.uPartNumber;
    }

    public SapOitm uPartNumber(String uPartNumber) {
        this.setuPartNumber(uPartNumber);
        return this;
    }

    public void setuPartNumber(String uPartNumber) {
        this.uPartNumber = uPartNumber;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SapOitm)) {
            return false;
        }
        return getId() != null && getId().equals(((SapOitm) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SapOitm{" +
            "id=" + getId() +
            ", itemCode='" + getItemCode() + "'" +
            ", itemName='" + getItemName() + "'" +
            ", itmsGrpCod='" + getItmsGrpCod() + "'" +
            ", uPartNumber='" + getuPartNumber() + "'" +
            "}";
    }
}
