package com.mycompany.wh.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A ItemData.
 */
@Entity
@Table(name = "ItemData")
public class ItemData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "ItemCode", length = 50, nullable = false)
    private String itemCode;

    @Size(max = 255)
    @Column(name = "ItemName", length = 255)
    private String itemName;

    @Size(max = 50)
    @Column(name = "ItmsGrpCod", length = 50)
    private String itmsGrpCod;

    @Size(max = 50)
    @Column(name = "Itm_Gr_Name", length = 50)
    private String itmGrName;

    @Size(max = 50)
    @Column(name = "U_IGroup", length = 50)
    private String uIGroup;

    @Size(max = 50)
    @Column(name = "U_IGroupName", length = 50)
    private String uIGroupName;

    @Size(max = 50)
    @Column(name = "U_SUBGR", length = 50)
    private String uSUBGR;

    @Size(max = 255)
    @Column(name = "U_SUBGRName", length = 255)
    private String uSUBGRName;

    @Size(max = 50)
    @Column(name = "U_ShortName", length = 50)
    private String uShortName;

    @Size(max = 50)
    @Column(name = "U_ProductBranch", length = 50)
    private String uProductBranch;

    @NotNull
    @Size(max = 50)
    @Column(name = "U_ProductGroup", length = 50, nullable = false)
    private String uProductGroup;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not
    // remove

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public ItemData itemCode(String itemCode) {
        this.itemCode = itemCode;
        return this;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public ItemData itemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItmsGrpCod() {
        return itmsGrpCod;
    }

    public ItemData itmsGrpCod(String itmsGrpCod) {
        this.itmsGrpCod = itmsGrpCod;
        return this;
    }

    public void setItmsGrpCod(String itmsGrpCod) {
        this.itmsGrpCod = itmsGrpCod;
    }

    public String getItmGrName() {
        return itmGrName;
    }

    public ItemData itmGrName(String itmGrName) {
        this.itmGrName = itmGrName;
        return this;
    }

    public void setItmGrName(String itmGrName) {
        this.itmGrName = itmGrName;
    }

    public String getuIGroup() {
        return uIGroup;
    }

    public ItemData uIGroup(String uIGroup) {
        this.uIGroup = uIGroup;
        return this;
    }

    public void setuIGroup(String uIGroup) {
        this.uIGroup = uIGroup;
    }

    public String getuIGroupName() {
        return uIGroupName;
    }

    public ItemData uIGroupName(String uIGroupName) {
        this.uIGroupName = uIGroupName;
        return this;
    }

    public void setuIGroupName(String uIGroupName) {
        this.uIGroupName = uIGroupName;
    }

    public String getuSUBGR() {
        return uSUBGR;
    }

    public ItemData uSUBGR(String uSUBGR) {
        this.uSUBGR = uSUBGR;
        return this;
    }

    public void setuSUBGR(String uSUBGR) {
        this.uSUBGR = uSUBGR;
    }

    public String getuSUBGRName() {
        return uSUBGRName;
    }

    public ItemData uSUBGRName(String uSUBGRName) {
        this.uSUBGRName = uSUBGRName;
        return this;
    }

    public void setuSUBGRName(String uSUBGRName) {
        this.uSUBGRName = uSUBGRName;
    }

    public String getuShortName() {
        return uShortName;
    }

    public ItemData uShortName(String uShortName) {
        this.uShortName = uShortName;
        return this;
    }

    public void setuShortName(String uShortName) {
        this.uShortName = uShortName;
    }

    public String getuProductBranch() {
        return uProductBranch;
    }

    public ItemData uProductBranch(String uProductBranch) {
        this.uProductBranch = uProductBranch;
        return this;
    }

    public void setuProductBranch(String uProductBranch) {
        this.uProductBranch = uProductBranch;
    }

    public String getuProductGroup() {
        return uProductGroup;
    }

    public ItemData uProductGroup(String uProductGroup) {
        this.uProductGroup = uProductGroup;
        return this;
    }

    public void setuProductGroup(String uProductGroup) {
        this.uProductGroup = uProductGroup;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemData)) {
            return false;
        }
        return id != null && id.equals(((ItemData) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemData{" +
                "id=" + getId() +
                ", itemCode='" + getItemCode() + "'" +
                ", itemName='" + getItemName() + "'" +
                ", itmsGrpCod='" + getItmsGrpCod() + "'" +
                ", itmGrName='" + getItmGrName() + "'" +
                ", uIGroup='" + getuIGroup() + "'" +
                ", uIGroupName='" + getuIGroupName() + "'" +
                ", uSUBGR='" + getuSUBGR() + "'" +
                ", uSUBGRName='" + getuSUBGRName() + "'" +
                ", uShortName='" + getuShortName() + "'" +
                ", uProductBranch='" + getuProductBranch() + "'" +
                ", uProductGroup='" + getuProductGroup() + "'" +
                "}";
    }
}
