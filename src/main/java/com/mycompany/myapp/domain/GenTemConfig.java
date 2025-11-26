package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A GenTemConfig.
 */
@Entity
@Table(name = "gen_note_config")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GenTemConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequenceGenerator"
    )
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 50)
    @Column(name = "tp_nk", length = 50)
    private String tpNk;

    @Size(max = 50)
    @Column(name = "rank", length = 50)
    private String rank;

    @Size(max = 50)
    @Column(name = "mfg", length = 50)
    private String mfg;

    @Column(name = "quantity_per_box")
    private Integer quantityPerBox;

    @Size(max = 255)
    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "num_box_per_pallet")
    private Integer numBoxPerPallet;

    @Size(max = 50)
    @Column(name = "branch", length = 50)
    private String branch;

    @Size(max = 50)
    @Column(name = "group_name", length = 50)
    private String groupName;

    @Size(max = 255)
    @Column(name = "customer_name", length = 255)
    private String customerName;

    @Size(max = 255)
    @Column(name = "po_number", length = 255)
    private String poNumber;

    @Size(max = 10)
    @Column(name = "date_code", length = 10)
    private String dateCode;

    @Size(max = 255)
    @Column(name = "item_no_sku", length = 255)
    private String itemNoSku;

    @Size(max = 255)
    @Column(name = "qdsx_no", length = 255)
    private String qdsxNo;

    @Column(name = "production_date")
    private LocalDate productionDate;

    @Size(max = 100)
    @Column(name = "inspector_name", length = 100)
    private String inspectorName;

    @Size(max = 100)
    @Column(name = "inspection_result", length = 100)
    private String inspectionResult;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Size(max = 50)
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_lenh_san_xuat_id")
    @JsonIgnoreProperties(
        value = { "genTemConfigs", "serialMappings", "details" },
        allowSetters = true
    )
    private WarehouseStampInfo maLenhSanXuat;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GenTemConfig id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTpNk() {
        return this.tpNk;
    }

    public GenTemConfig tpNk(String tpNk) {
        this.setTpNk(tpNk);
        return this;
    }

    public void setTpNk(String tpNk) {
        this.tpNk = tpNk;
    }

    public String getRank() {
        return this.rank;
    }

    public GenTemConfig rank(String rank) {
        this.setRank(rank);
        return this;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getMfg() {
        return this.mfg;
    }

    public GenTemConfig mfg(String mfg) {
        this.setMfg(mfg);
        return this;
    }

    public void setMfg(String mfg) {
        this.mfg = mfg;
    }

    public Integer getQuantityPerBox() {
        return this.quantityPerBox;
    }

    public GenTemConfig quantityPerBox(Integer quantityPerBox) {
        this.setQuantityPerBox(quantityPerBox);
        return this;
    }

    public void setQuantityPerBox(Integer quantityPerBox) {
        this.quantityPerBox = quantityPerBox;
    }

    public String getNote() {
        return this.note;
    }

    public GenTemConfig note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getNumBoxPerPallet() {
        return this.numBoxPerPallet;
    }

    public GenTemConfig numBoxPerPallet(Integer numBoxPerPallet) {
        this.setNumBoxPerPallet(numBoxPerPallet);
        return this;
    }

    public void setNumBoxPerPallet(Integer numBoxPerPallet) {
        this.numBoxPerPallet = numBoxPerPallet;
    }

    public String getBranch() {
        return this.branch;
    }

    public GenTemConfig branch(String branch) {
        this.setBranch(branch);
        return this;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public GenTemConfig groupName(String groupName) {
        this.setGroupName(groupName);
        return this;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public GenTemConfig customerName(String customerName) {
        this.setCustomerName(customerName);
        return this;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPoNumber() {
        return this.poNumber;
    }

    public GenTemConfig poNumber(String poNumber) {
        this.setPoNumber(poNumber);
        return this;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getDateCode() {
        return this.dateCode;
    }

    public GenTemConfig dateCode(String dateCode) {
        this.setDateCode(dateCode);
        return this;
    }

    public void setDateCode(String dateCode) {
        this.dateCode = dateCode;
    }

    public String getItemNoSku() {
        return this.itemNoSku;
    }

    public GenTemConfig itemNoSku(String itemNoSku) {
        this.setItemNoSku(itemNoSku);
        return this;
    }

    public void setItemNoSku(String itemNoSku) {
        this.itemNoSku = itemNoSku;
    }

    public String getQdsxNo() {
        return this.qdsxNo;
    }

    public GenTemConfig qdsxNo(String qdsxNo) {
        this.setQdsxNo(qdsxNo);
        return this;
    }

    public void setQdsxNo(String qdsxNo) {
        this.qdsxNo = qdsxNo;
    }

    public LocalDate getProductionDate() {
        return this.productionDate;
    }

    public GenTemConfig productionDate(LocalDate productionDate) {
        this.setProductionDate(productionDate);
        return this;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public String getInspectorName() {
        return this.inspectorName;
    }

    public GenTemConfig inspectorName(String inspectorName) {
        this.setInspectorName(inspectorName);
        return this;
    }

    public void setInspectorName(String inspectorName) {
        this.inspectorName = inspectorName;
    }

    public String getInspectionResult() {
        return this.inspectionResult;
    }

    public GenTemConfig inspectionResult(String inspectionResult) {
        this.setInspectionResult(inspectionResult);
        return this;
    }

    public void setInspectionResult(String inspectionResult) {
        this.inspectionResult = inspectionResult;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public GenTemConfig updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public GenTemConfig updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public WarehouseStampInfo getMaLenhSanXuat() {
        return this.maLenhSanXuat;
    }

    public void setMaLenhSanXuat(WarehouseStampInfo warehouseStampInfo) {
        this.maLenhSanXuat = warehouseStampInfo;
    }

    public GenTemConfig maLenhSanXuat(WarehouseStampInfo warehouseStampInfo) {
        this.setMaLenhSanXuat(warehouseStampInfo);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GenTemConfig)) {
            return false;
        }
        return getId() != null && getId().equals(((GenTemConfig) o).getId());
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
        return "GenTemConfig{" +
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
                "}";
    }
}
