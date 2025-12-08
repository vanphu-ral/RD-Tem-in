package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.PalletInforDetail} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PalletInforDetailDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String serialPallet;

    private Long maLenhSanXuatId;

    private Integer quantityPerBox;

    @Size(max = 255)
    private String note;

    private Integer numBoxPerPallet;

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

    private Integer scanProgress;

    private Integer numBoxActual;

    private Integer numBoxConfig;

    private Instant updatedAt;

    @Size(max = 50)
    private String updatedBy;

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("serial_pallet")
    public String getSerialPallet() {
        return serialPallet;
    }

    @JsonProperty("serial_pallet")
    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

    @JsonProperty("ma_lenh_san_xuat_id")
    public Long getMaLenhSanXuatId() {
        return maLenhSanXuatId;
    }

    @JsonProperty("ma_lenh_san_xuat_id")
    public void setMaLenhSanXuatId(Long maLenhSanXuatId) {
        this.maLenhSanXuatId = maLenhSanXuatId;
    }

    @JsonProperty("quantity_per_box")
    public Integer getQuantityPerBox() {
        return quantityPerBox;
    }

    @JsonProperty("quantity_per_box")
    public void setQuantityPerBox(Integer quantityPerBox) {
        this.quantityPerBox = quantityPerBox;
    }

    @JsonProperty("note")
    public String getNote() {
        return note;
    }

    @JsonProperty("note")
    public void setNote(String note) {
        this.note = note;
    }

    @JsonProperty("num_box_per_pallet")
    public Integer getNumBoxPerPallet() {
        return numBoxPerPallet;
    }

    @JsonProperty("num_box_per_pallet")
    public void setNumBoxPerPallet(Integer numBoxPerPallet) {
        this.numBoxPerPallet = numBoxPerPallet;
    }

    @JsonProperty("customer_name")
    public String getCustomerName() {
        return customerName;
    }

    @JsonProperty("customer_name")
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @JsonProperty("po_number")
    public String getPoNumber() {
        return poNumber;
    }

    @JsonProperty("po_number")
    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    @JsonProperty("date_code")
    public String getDateCode() {
        return dateCode;
    }

    @JsonProperty("date_code")
    public void setDateCode(String dateCode) {
        this.dateCode = dateCode;
    }

    @JsonProperty("item_no_sku")
    public String getItemNoSku() {
        return itemNoSku;
    }

    @JsonProperty("item_no_sku")
    public void setItemNoSku(String itemNoSku) {
        this.itemNoSku = itemNoSku;
    }

    @JsonProperty("qdsx_no")
    public String getQdsxNo() {
        return qdsxNo;
    }

    @JsonProperty("qdsx_no")
    public void setQdsxNo(String qdsxNo) {
        this.qdsxNo = qdsxNo;
    }

    @JsonProperty("production_date")
    public LocalDate getProductionDate() {
        return productionDate;
    }

    @JsonProperty("production_date")
    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    @JsonProperty("inspector_name")
    public String getInspectorName() {
        return inspectorName;
    }

    @JsonProperty("inspector_name")
    public void setInspectorName(String inspectorName) {
        this.inspectorName = inspectorName;
    }

    @JsonProperty("inspection_result")
    public String getInspectionResult() {
        return inspectionResult;
    }

    @JsonProperty("inspection_result")
    public void setInspectionResult(String inspectionResult) {
        this.inspectionResult = inspectionResult;
    }

    @JsonProperty("scan_progress")
    public Integer getScanProgress() {
        return scanProgress;
    }

    @JsonProperty("scan_progress")
    public void setScanProgress(Integer scanProgress) {
        this.scanProgress = scanProgress;
    }

    @JsonProperty("num_box_actual")
    public Integer getNumBoxActual() {
        return numBoxActual;
    }

    @JsonProperty("num_box_actual")
    public void setNumBoxActual(Integer numBoxActual) {
        this.numBoxActual = numBoxActual;
    }

    @JsonProperty("num_box_config")
    public Integer getNumBoxConfig() {
        return numBoxConfig;
    }

    @JsonProperty("num_box_config")
    public void setNumBoxConfig(Integer numBoxConfig) {
        this.numBoxConfig = numBoxConfig;
    }

    @JsonProperty("updated_at")
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updated_at")
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("updated_by")
    public String getUpdatedBy() {
        return updatedBy;
    }

    @JsonProperty("updated_by")
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PalletInforDetailDTO)) {
            return false;
        }

        PalletInforDetailDTO palletInforDetailDTO = (PalletInforDetailDTO) o;
        if (this.serialPallet == null) {
            return false;
        }
        return this.serialPallet.equals(palletInforDetailDTO.serialPallet);
    }

    @Override
    public int hashCode() {
        return serialPallet != null ? serialPallet.hashCode() : 0;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PalletInforDetailDTO{" +
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
                ", updatedAt='" + getUpdatedAt() + "'" +
                ", updatedBy='" + getUpdatedBy() + "'" +
                "}";
    }
}
