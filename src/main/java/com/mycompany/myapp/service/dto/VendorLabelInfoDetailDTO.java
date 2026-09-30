package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.PalletBoxMapping;
import com.mycompany.myapp.domain.VendorLabelInfo;
import java.io.Serializable;

/**
 * A DTO for a VendorLabelInfo with the pallet it belongs to, resolved from
 * PalletBoxMapping (pallet_box_mapping.reel_id_box = vendor_label_info.reel_id).
 * Carries every field of the entity; the parent objects are left out to avoid
 * duplicated/recursive payloads.
 */
@JsonIgnoreProperties(
    ignoreUnknown = true,
    value = { "deliveryNotification", "sapPor1", "palletBoxMapping" }
)
public class VendorLabelInfoDetailDTO
    extends VendorLabelInfoDTO
    implements Serializable {

    private String serialPallet;

    public VendorLabelInfoDetailDTO() {
        super();
    }

    public VendorLabelInfoDetailDTO(
        VendorLabelInfo entity,
        PalletBoxMapping palletBoxMapping
    ) {
        super();
        if (entity != null) {
            this.setId(entity.getId());
            this.setReelId(entity.getReelId());
            this.setPartNumber(entity.getPartNumber());
            this.setVendor(entity.getVendor());
            this.setLot(entity.getLot());
            this.setUserData1(entity.getUserData1());
            this.setUserData2(entity.getUserData2());
            this.setUserData3(entity.getUserData3());
            this.setUserData4(entity.getUserData4());
            this.setUserData5(entity.getUserData5());
            this.setInitialQuantity(entity.getInitialQuantity());
            this.setMsdLevel(entity.getMsdLevel());
            this.setMsdInitialFloorTime(entity.getMsdInitialFloorTime());
            this.setMsdBagSealDate(entity.getMsdBagSealDate());
            this.setMarketUsage(entity.getMarketUsage());
            this.setQuantityOverride(entity.getQuantityOverride());
            this.setShelfTime(entity.getShelfTime());
            this.setSpMaterialName(entity.getSpMaterialName());
            this.setWarningLimit(entity.getWarningLimit());
            this.setMaximumLimit(entity.getMaximumLimit());
            this.setComments(entity.getComments());
            this.setWarmupTime(entity.getWarmupTime());
            this.setStorageUnit(entity.getStorageUnit());
            this.setSubStorageUnit(entity.getSubStorageUnit());
            this.setLocationOverride(entity.getLocationOverride());
            this.setExpirationDate(entity.getExpirationDate());
            this.setManufacturingDate(entity.getManufacturingDate());
            this.setPartClass(entity.getPartClass());
            this.setSapCode(entity.getSapCode());
            this.setVendorQrCode(entity.getVendorQrCode());
            this.setStatus(entity.getStatus());
            this.setCreatedBy(entity.getCreatedBy());
            this.setCreatedAt(entity.getCreatedAt());
            this.setUpdatedBy(entity.getUpdatedBy());
            this.setUpdatedAt(entity.getUpdatedAt());
            this.setVendorAdditionalData(entity.getVendorAdditionalData());
            this.setPanaSendStatus(entity.getPanaSendStatus());
            this.setSapSendStatus(entity.getSapSendStatus());
            if (entity.getDeliveryNotification() != null) {
                this.setDeliveryNotificationId(
                    entity.getDeliveryNotification().getId()
                );
            }
            if (entity.getSapPor1() != null) {
                this.setSapPor1Id(entity.getSapPor1().getId());
            }
        }
        if (palletBoxMapping != null) {
            this.setPalletBoxMappingId(palletBoxMapping.getId());
            this.serialPallet = palletBoxMapping.getSerialPallet();
        }
    }

    public String getSerialPallet() {
        return serialPallet;
    }

    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

    @Override
    public String toString() {
        return (
            "VendorLabelInfoDetailDTO{" +
            "id=" +
            getId() +
            ", reelId='" +
            getReelId() +
            "'" +
            ", partNumber='" +
            getPartNumber() +
            "'" +
            ", lot='" +
            getLot() +
            "'" +
            ", serialPallet='" +
            serialPallet +
            "'" +
            ", deliveryNotificationId=" +
            getDeliveryNotificationId() +
            ", sapPor1Id=" +
            getSapPor1Id() +
            ", palletBoxMappingId=" +
            getPalletBoxMappingId() +
            "}"
        );
    }
}
