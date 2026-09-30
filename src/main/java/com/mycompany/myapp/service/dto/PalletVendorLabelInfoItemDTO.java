package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

/**
 * One saved {@code vendor_label_info} row, keeping every field of
 * {@link VendorLabelInfoDTO} and adding the {@code pallet_box_mapping} row that
 * links it to the pallet. The parent objects ({@code deliveryNotification},
 * {@code sapPor1}) are left out to avoid duplicated/recursive payloads.
 */
@JsonIgnoreProperties(
    ignoreUnknown = true,
    value = { "deliveryNotification", "sapPor1" }
)
public class PalletVendorLabelInfoItemDTO
    extends VendorLabelInfoDTO
    implements Serializable {

    private static final long serialVersionUID = 1L;

    public PalletVendorLabelInfoItemDTO() {
        super();
    }

    @Override
    public String toString() {
        return (
            "PalletVendorLabelInfoItemDTO{" +
            "id=" +
            getId() +
            ", reelId='" +
            getReelId() +
            '\'' +
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
