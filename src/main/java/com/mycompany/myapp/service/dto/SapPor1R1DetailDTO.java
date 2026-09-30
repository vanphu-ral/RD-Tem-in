package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.SapPor1R1;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A DTO for a SapPor1R1 line with the VendorLabelInfo boxes belonging to it.
 * Carries every field of the entity; the parent DeliveryNotification is left out
 * to avoid a duplicated payload (it is already the root of the response).
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = { "deliveryNotification" })
public class SapPor1R1DetailDTO extends SapPor1R1DTO implements Serializable {

    private List<VendorLabelInfoDetailDTO> vendorLabelInfoList;

    public SapPor1R1DetailDTO() {
        super();
    }

    public SapPor1R1DetailDTO(
        SapPor1R1 entity,
        List<VendorLabelInfoDetailDTO> vendorLabelInfoList
    ) {
        super();
        if (entity != null) {
            this.setId(entity.getId());
            this.setLineNum(entity.getLineNum());
            this.setBaseRef(entity.getBaseRef());
            this.setBaseEntry(entity.getBaseEntry());
            this.setBaseLine(entity.getBaseLine());
            this.setLineStatus(entity.getLineStatus());
            this.setItemCode(entity.getItemCode());
            this.setDscription(entity.getDscription());
            this.setQuantity(entity.getQuantity());
            this.setShipDate(entity.getShipDate());
            this.setPrice(entity.getPrice());
            this.setCurrency(entity.getCurrency());
            this.setDiscPrcnt(entity.getDiscPrcnt());
            this.setTotalSumSy(entity.getTotalSumSy());
            this.setOpenSumSys(entity.getOpenSumSys());
            this.setInvntSttus(entity.getInvntSttus());
            this.setBaseDocNum(entity.getBaseDocNum());
            this.setuTenkythuat(entity.getuTenkythuat());
            this.setuSo(entity.getuSo());
            this.setuMCode(entity.getuMCode());
            this.setDocEntry(entity.getDocEntry());
            this.setTotalFrgn(entity.getTotalFrgn());
            this.setVatGroup(entity.getVatGroup());
            this.setUomCode(entity.getUomCode());
            this.setUnitMsr(entity.getUnitMsr());
            this.setLineVendor(entity.getLineVendor());
            this.setTrgetEntry(entity.getTrgetEntry());
            this.setLineTotal(entity.getLineTotal());
            this.setVatPrcnt(entity.getVatPrcnt());
            this.setPriceAfVat(entity.getPriceAfVat());
            this.setWhsCode(entity.getWhsCode());
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
            "SapPor1R1DetailDTO{" +
            "id=" +
            getId() +
            ", lineNum='" +
            getLineNum() +
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
