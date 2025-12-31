package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A PalletInforDetail.
 */
@Entity
@Table(name = "pallet_infor_detail")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PalletInforDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 50)
    @Column(name = "serial_pallet", length = 50)
    private String serialPallet;

    @Column(name = "ma_lenh_san_xuat_id")
    private Long maLenhSanXuatId;

    @Column(name = "quantity_per_box")
    private Integer quantityPerBox;

    @Size(max = 255)
    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "num_box_per_pallet")
    private Integer numBoxPerPallet;

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

    @Column(name = "scan_progress")
    private Integer scanProgress;

    @Column(name = "num_box_actual")
    private Integer numBoxActual;

    @Column(name = "num_box_config")
    private Integer numBoxConfig;

    @Column(name = "pallet_index")
    private Integer palletIndex;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Size(max = 50)
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "wms_send_status")
    private Boolean wmsSendStatus;

    @Column(name = "print_status")
    private Boolean printStatus;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PalletInforDetail id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialPallet() {
        return this.serialPallet;
    }

    public PalletInforDetail serialPallet(String serialPallet) {
        this.setSerialPallet(serialPallet);
        return this;
    }

    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

    public Long getMaLenhSanXuatId() {
        return this.maLenhSanXuatId;
    }

    public PalletInforDetail maLenhSanXuatId(Long maLenhSanXuatId) {
        this.setMaLenhSanXuatId(maLenhSanXuatId);
        return this;
    }

    public void setMaLenhSanXuatId(Long maLenhSanXuatId) {
        this.maLenhSanXuatId = maLenhSanXuatId;
    }

    public Integer getQuantityPerBox() {
        return this.quantityPerBox;
    }

    public PalletInforDetail quantityPerBox(Integer quantityPerBox) {
        this.setQuantityPerBox(quantityPerBox);
        return this;
    }

    public void setQuantityPerBox(Integer quantityPerBox) {
        this.quantityPerBox = quantityPerBox;
    }

    public String getNote() {
        return this.note;
    }

    public PalletInforDetail note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getNumBoxPerPallet() {
        return this.numBoxPerPallet;
    }

    public PalletInforDetail numBoxPerPallet(Integer numBoxPerPallet) {
        this.setNumBoxPerPallet(numBoxPerPallet);
        return this;
    }

    public void setNumBoxPerPallet(Integer numBoxPerPallet) {
        this.numBoxPerPallet = numBoxPerPallet;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public PalletInforDetail customerName(String customerName) {
        this.setCustomerName(customerName);
        return this;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPoNumber() {
        return this.poNumber;
    }

    public PalletInforDetail poNumber(String poNumber) {
        this.setPoNumber(poNumber);
        return this;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getDateCode() {
        return this.dateCode;
    }

    public PalletInforDetail dateCode(String dateCode) {
        this.setDateCode(dateCode);
        return this;
    }

    public void setDateCode(String dateCode) {
        this.dateCode = dateCode;
    }

    public String getItemNoSku() {
        return this.itemNoSku;
    }

    public PalletInforDetail itemNoSku(String itemNoSku) {
        this.setItemNoSku(itemNoSku);
        return this;
    }

    public void setItemNoSku(String itemNoSku) {
        this.itemNoSku = itemNoSku;
    }

    public String getQdsxNo() {
        return this.qdsxNo;
    }

    public PalletInforDetail qdsxNo(String qdsxNo) {
        this.setQdsxNo(qdsxNo);
        return this;
    }

    public void setQdsxNo(String qdsxNo) {
        this.qdsxNo = qdsxNo;
    }

    public LocalDate getProductionDate() {
        return this.productionDate;
    }

    public PalletInforDetail productionDate(LocalDate productionDate) {
        this.setProductionDate(productionDate);
        return this;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public String getInspectorName() {
        return this.inspectorName;
    }

    public PalletInforDetail inspectorName(String inspectorName) {
        this.setInspectorName(inspectorName);
        return this;
    }

    public void setInspectorName(String inspectorName) {
        this.inspectorName = inspectorName;
    }

    public String getInspectionResult() {
        return this.inspectionResult;
    }

    public PalletInforDetail inspectionResult(String inspectionResult) {
        this.setInspectionResult(inspectionResult);
        return this;
    }

    public void setInspectionResult(String inspectionResult) {
        this.inspectionResult = inspectionResult;
    }

    public Integer getScanProgress() {
        return this.scanProgress;
    }

    public PalletInforDetail scanProgress(Integer scanProgress) {
        this.setScanProgress(scanProgress);
        return this;
    }

    public void setScanProgress(Integer scanProgress) {
        this.scanProgress = scanProgress;
    }

    public Integer getNumBoxActual() {
        return this.numBoxActual;
    }

    public PalletInforDetail numBoxActual(Integer numBoxActual) {
        this.setNumBoxActual(numBoxActual);
        return this;
    }

    public void setNumBoxActual(Integer numBoxActual) {
        this.numBoxActual = numBoxActual;
    }

    public Integer getNumBoxConfig() {
        return this.numBoxConfig;
    }

    public PalletInforDetail numBoxConfig(Integer numBoxConfig) {
        this.setNumBoxConfig(numBoxConfig);
        return this;
    }

    public void setNumBoxConfig(Integer numBoxConfig) {
        this.numBoxConfig = numBoxConfig;
    }

    public Integer getPalletIndex() {
        return this.palletIndex;
    }

    public PalletInforDetail palletIndex(Integer palletIndex) {
        this.setPalletIndex(palletIndex);
        return this;
    }

    public void setPalletIndex(Integer palletIndex) {
        this.palletIndex = palletIndex;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public PalletInforDetail updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public PalletInforDetail updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Boolean getWmsSendStatus() {
        return this.wmsSendStatus;
    }

    public PalletInforDetail wmsSendStatus(Boolean wmsSendStatus) {
        this.setWmsSendStatus(wmsSendStatus);
        return this;
    }

    public void setWmsSendStatus(Boolean wmsSendStatus) {
        this.wmsSendStatus = wmsSendStatus;
    }

    public Boolean getPrintStatus() {
        return this.printStatus;
    }

    public PalletInforDetail printStatus(Boolean printStatus) {
        this.setPrintStatus(printStatus);
        return this;
    }

    public void setPrintStatus(Boolean printStatus) {
        this.printStatus = printStatus;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PalletInforDetail)) {
            return false;
        }
        return (
            getSerialPallet() != null &&
            getSerialPallet().equals(((PalletInforDetail) o).getSerialPallet())
        );
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
        return "PalletInforDetail{" +
                "id=" + getId() +
                ", serialPallet='" + getSerialPallet() + "'" +
                ", maLenhSanXuatId=" + getMaLenhSanXuatId() +
                ", quantityPerBox=" + getQuantityPerBox() +
                ", note='" + getNote() + "'" +
                ", numBoxPerPallet=" + getNumBoxPerPallet() +
                ", customerName='" + getCustomerName() + "'" +
                ", poNumber='" + getPoNumber() + "'" +
                ", dateCode='" + getDateCode() + "'" +
                ", itemNoSku='" + getItemNoSku() + "'" +
                ", qdsxNo='" + getQdsxNo() + "'" +
                ", productionDate='" + getProductionDate() + "'" +
                ", inspectorName='" + getInspectorName() + "'" +
                ", inspectionResult='" + getInspectionResult() + "'" +
                ", scanProgress=" + getScanProgress() +
                ", numBoxActual=" + getNumBoxActual() +
                ", numBoxConfig=" + getNumBoxConfig() +
                ", palletIndex=" + getPalletIndex() +
                ", updatedAt='" + getUpdatedAt() + "'" +
                ", updatedBy='" + getUpdatedBy() + "'" +
                ", wmsSendStatus=" + getWmsSendStatus() +
                "}";
    }
}
