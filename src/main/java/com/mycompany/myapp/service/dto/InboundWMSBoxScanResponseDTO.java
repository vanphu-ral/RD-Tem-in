package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.InboundWMSBox;
import java.io.Serializable;

/**
 * A DTO for the inbound WMS box scan response.
 */
public class InboundWMSBoxScanResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private InboundWMSBox inboundWMSBox;

    private WarehouseStampInfoDTO warehouseNoteInfo;

    private String partNumber;

    private String lot;

    private String userData1;

    private String userData2;

    private String userData3;

    private String userData4;

    private String userData5;

    private Integer initialQuantity;

    private Boolean wmsSendStatus;

    private String sapCode;

    // Constructors
    public InboundWMSBoxScanResponseDTO() {}

    public InboundWMSBoxScanResponseDTO(
        InboundWMSBox inboundWMSBox,
        WarehouseStampInfoDTO warehouseNoteInfo,
        String partNumber,
        String lot,
        String userData1,
        String userData2,
        String userData3,
        String userData4,
        String userData5,
        Integer initialQuantity,
        Boolean wmsSendStatus,
        String sapCode
    ) {
        this.inboundWMSBox = inboundWMSBox;
        this.warehouseNoteInfo = warehouseNoteInfo;
        this.partNumber = partNumber;
        this.lot = lot;
        this.userData1 = userData1;
        this.userData2 = userData2;
        this.userData3 = userData3;
        this.userData4 = userData4;
        this.userData5 = userData5;
        this.initialQuantity = initialQuantity;
        this.wmsSendStatus = wmsSendStatus;
        this.sapCode = sapCode;
    }

    // Getters and Setters
    public InboundWMSBox getInboundWMSBox() {
        return inboundWMSBox;
    }

    public void setInboundWMSBox(InboundWMSBox inboundWMSBox) {
        this.inboundWMSBox = inboundWMSBox;
    }

    public WarehouseStampInfoDTO getWarehouseNoteInfo() {
        return warehouseNoteInfo;
    }

    public void setWarehouseNoteInfo(WarehouseStampInfoDTO warehouseNoteInfo) {
        this.warehouseNoteInfo = warehouseNoteInfo;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getUserData1() {
        return userData1;
    }

    public void setUserData1(String userData1) {
        this.userData1 = userData1;
    }

    public String getUserData2() {
        return userData2;
    }

    public void setUserData2(String userData2) {
        this.userData2 = userData2;
    }

    public String getUserData3() {
        return userData3;
    }

    public void setUserData3(String userData3) {
        this.userData3 = userData3;
    }

    public String getUserData4() {
        return userData4;
    }

    public void setUserData4(String userData4) {
        this.userData4 = userData4;
    }

    public String getUserData5() {
        return userData5;
    }

    public void setUserData5(String userData5) {
        this.userData5 = userData5;
    }

    public Integer getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public Boolean getWmsSendStatus() {
        return wmsSendStatus;
    }

    public void setWmsSendStatus(Boolean wmsSendStatus) {
        this.wmsSendStatus = wmsSendStatus;
    }

    public String getSapCode() {
        return sapCode;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    @Override
    public String toString() {
        return (
            "InboundWMSBoxScanResponseDTO{" +
            "inboundWMSBox=" +
            inboundWMSBox +
            ", warehouseNoteInfo=" +
            warehouseNoteInfo +
            ", partNumber='" +
            partNumber +
            "'" +
            ", lot='" +
            lot +
            "'" +
            ", userData1='" +
            userData1 +
            "'" +
            ", userData2='" +
            userData2 +
            "'" +
            ", userData3='" +
            userData3 +
            "'" +
            ", userData4='" +
            userData4 +
            "'" +
            ", userData5='" +
            userData5 +
            "'" +
            ", initialQuantity=" +
            initialQuantity +
            ", wmsSendStatus=" +
            wmsSendStatus +
            ", sapCode='" +
            sapCode +
            "'" +
            "}"
        );
    }
}
