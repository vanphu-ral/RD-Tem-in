package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A simple DTO for the {@link com.mycompany.myapp.domain.GenTemConfig} entity
 * containing only the table fields without nested objects.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GenTemConfigSimpleDTO implements Serializable {

    private Long id;

    @Size(max = 50)
    private String tpNk;

    @Size(max = 50)
    private String rank;

    @Size(max = 50)
    private String mfg;

    private Integer quantityPerBox;

    @Size(max = 255)
    private String note;

    private Integer numBoxPerPallet;

    @Size(max = 50)
    private String branch;

    @Size(max = 50)
    private String groupName;

    @Size(max = 255)
    private String customerName;

    @Size(max = 255)
    private String poNumber;

    @Size(max = 10)
    private String dateCode;

    @Size(max = 255)
    private String itemNoSku;

    @Size(max = 255)
    private String qdsxNo;

    private LocalDate productionDate;

    @Size(max = 100)
    private String inspectorName;

    @Size(max = 100)
    private String inspectionResult;

    private Instant updatedAt;

    @Size(max = 50)
    private String updatedBy;

    private Long maLenhSanXuatId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTpNk() {
        return tpNk;
    }

    public void setTpNk(String tpNk) {
        this.tpNk = tpNk;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getMfg() {
        return mfg;
    }

    public void setMfg(String mfg) {
        this.mfg = mfg;
    }

    public Integer getQuantityPerBox() {
        return quantityPerBox;
    }

    public void setQuantityPerBox(Integer quantityPerBox) {
        this.quantityPerBox = quantityPerBox;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getNumBoxPerPallet() {
        return numBoxPerPallet;
    }

    public void setNumBoxPerPallet(Integer numBoxPerPallet) {
        this.numBoxPerPallet = numBoxPerPallet;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getDateCode() {
        return dateCode;
    }

    public void setDateCode(String dateCode) {
        this.dateCode = dateCode;
    }

    public String getItemNoSku() {
        return itemNoSku;
    }

    public void setItemNoSku(String itemNoSku) {
        this.itemNoSku = itemNoSku;
    }

    public String getQdsxNo() {
        return qdsxNo;
    }

    public void setQdsxNo(String qdsxNo) {
        this.qdsxNo = qdsxNo;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public String getInspectorName() {
        return inspectorName;
    }

    public void setInspectorName(String inspectorName) {
        this.inspectorName = inspectorName;
    }

    public String getInspectionResult() {
        return inspectionResult;
    }

    public void setInspectionResult(String inspectionResult) {
        this.inspectionResult = inspectionResult;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getMaLenhSanXuatId() {
        return maLenhSanXuatId;
    }

    public void setMaLenhSanXuatId(Long maLenhSanXuatId) {
        this.maLenhSanXuatId = maLenhSanXuatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GenTemConfigSimpleDTO)) {
            return false;
        }

        GenTemConfigSimpleDTO genTemConfigSimpleDTO = (GenTemConfigSimpleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, genTemConfigSimpleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GenTemConfigSimpleDTO{" +
                "id=" + getId() +
                ", tpNk='" + getTpNk() + "'" +
                ", rank='" + getRank() + "'" +
                ", mfg='" + getMfg() + "'" +
                ", quantityPerBox=" + getQuantityPerBox() +
                ", note='" + getNote() + "'" +
                ", numBoxPerPallet=" + getNumBoxPerPallet() +
                ", branch='" + getBranch() + "'" +
                ", groupName='" + getGroupName() + "'" +
                ", customerName='" + getCustomerName() + "'" +
                ", poNumber='" + getPoNumber() + "'" +
                ", dateCode='" + getDateCode() + "'" +
                ", itemNoSku='" + getItemNoSku() + "'" +
                ", qdsxNo='" + getQdsxNo() + "'" +
                ", productionDate='" + getProductionDate() + "'" +
                ", inspectorName='" + getInspectorName() + "'" +
                ", inspectionResult='" + getInspectionResult() + "'" +
                ", updatedAt='" + getUpdatedAt() + "'" +
                ", updatedBy='" + getUpdatedBy() + "'" +
                ", maLenhSanXuatId=" + getMaLenhSanXuatId() +
                "}";
    }
}
