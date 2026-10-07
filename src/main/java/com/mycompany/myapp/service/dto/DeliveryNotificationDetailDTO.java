package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.DeliveryNotification;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A DTO for a DeliveryNotification with its SapPor1R1 lines, each line holding
 * the VendorLabelInfo boxes (enriched with serialPallet from PalletBoxMapping).
 */
public class DeliveryNotificationDetailDTO implements Serializable {

    private Long id;

    private String deliveryNotificationCode;

    private String invoiceNumber;

    private String contractCode;

    private String vendorName;

    private String contNo;

    private Instant entryDate;

    private Integer numberOfPo;

    private Integer numberOfItem;

    private String status;

    private String source;

    private String createdBy;

    private Instant createdAt;

    private List<SapPor1R1DetailDTO> sapPor1R1List;

    public DeliveryNotificationDetailDTO() {
        super();
    }

    public DeliveryNotificationDetailDTO(
        DeliveryNotification entity,
        List<SapPor1R1DetailDTO> sapPor1R1List
    ) {
        super();
        if (entity != null) {
            this.id = entity.getId();
            this.deliveryNotificationCode =
                entity.getDeliveryNotificationCode();
            this.invoiceNumber = entity.getInvoiceNumber();
            this.contractCode = entity.getContractCode();
            this.vendorName = entity.getVendorName();
            this.contNo = entity.getContNo();
            this.entryDate = entity.getEntryDate();
            this.numberOfPo = entity.getNumberOfPo();
            this.numberOfItem = entity.getNumberOfItem();
            this.status = entity.getStatus();
            this.source = entity.getSource();
            this.createdBy = entity.getCreatedBy();
            this.createdAt = entity.getCreatedAt();
        }
        this.sapPor1R1List = sapPor1R1List == null
            ? new ArrayList<>()
            : sapPor1R1List;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeliveryNotificationCode() {
        return deliveryNotificationCode;
    }

    public void setDeliveryNotificationCode(String deliveryNotificationCode) {
        this.deliveryNotificationCode = deliveryNotificationCode;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getContNo() {
        return contNo;
    }

    public void setContNo(String contNo) {
        this.contNo = contNo;
    }

    public Instant getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Instant entryDate) {
        this.entryDate = entryDate;
    }

    public Integer getNumberOfPo() {
        return numberOfPo;
    }

    public void setNumberOfPo(Integer numberOfPo) {
        this.numberOfPo = numberOfPo;
    }

    public Integer getNumberOfItem() {
        return numberOfItem;
    }

    public void setNumberOfItem(Integer numberOfItem) {
        this.numberOfItem = numberOfItem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<SapPor1R1DetailDTO> getSapPor1R1List() {
        return sapPor1R1List;
    }

    public void setSapPor1R1List(List<SapPor1R1DetailDTO> sapPor1R1List) {
        this.sapPor1R1List = sapPor1R1List;
    }

    @Override
    public String toString() {
        return (
            "DeliveryNotificationDetailDTO{" +
            "id=" +
            id +
            ", deliveryNotificationCode='" +
            deliveryNotificationCode +
            "'" +
            ", status='" +
            status +
            "'" +
            ", sapPor1R1List=" +
            sapPor1R1List +
            "}"
        );
    }
}
