package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.DetailDelivery;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A DTO for a DetailDelivery line with the VendorLabelInfo boxes belonging to it.
 * Carries every field of the entity; the parent Delivery is left out
 * to avoid a duplicated payload (it is already the root of the response).
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = { "delivery" })
public class DetailDeliveryDetailDTO
    extends DetailDeliveryDTO
    implements Serializable {

    private List<VendorLabelInfoDetailDTO> vendorLabelInfoList;

    public DetailDeliveryDetailDTO() {
        super();
    }

    public DetailDeliveryDetailDTO(
        DetailDelivery entity,
        List<VendorLabelInfoDetailDTO> vendorLabelInfoList
    ) {
        super();
        if (entity != null) {
            this.setLineId(entity.getLineId());
            this.setPoSmart(entity.getPoSmart());
            this.setPoErp(entity.getPoErp());
            this.setPrNumber(entity.getPrNumber());
            this.setContractNo(entity.getContractNo());
            this.setLineItemStt(entity.getLineItemStt());
            this.setPoLineItem(entity.getPoLineItem());
            this.setItemCode(entity.getItemCode());
            this.setItemName(entity.getItemName());
            this.setWhsCode(entity.getWhsCode());
            this.setUnit(entity.getUnit());
            this.setQuantity(entity.getQuantity());
            this.setInvoiceSmartNo(entity.getInvoiceSmartNo());
            this.setInvoiceNo(entity.getInvoiceNo());
            this.setInvoiceDate(entity.getInvoiceDate());
            this.setDeliveryTurn(entity.getDeliveryTurn());
        }
        this.vendorLabelInfoList = vendorLabelInfoList == null
            ? new ArrayList<>()
            : vendorLabelInfoList;
    }

    public List<VendorLabelInfoDetailDTO> getVendorLabelInfoList() {
        return vendorLabelInfoList;
    }

    public void setVendorLabelInfoList(
        List<VendorLabelInfoDetailDTO> vendorLabelInfoList
    ) {
        this.vendorLabelInfoList = vendorLabelInfoList;
    }

    @Override
    public String toString() {
        return (
            "DetailDeliveryDetailDTO{" +
            "lineId=" +
            getLineId() +
            ", poSmart='" +
            getPoSmart() +
            "'" +
            ", itemCode='" +
            getItemCode() +
            "'" +
            ", quantity=" +
            getQuantity() +
            ", vendorLabelInfoList=" +
            vendorLabelInfoList +
            "}"
        );
    }
}
