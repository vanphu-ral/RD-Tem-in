package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Box information in InboundWMSPalletScanResponse.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BoxInfoDTO implements Serializable {

    private String note;

    private String boxCode;

    private Integer quantity;

    private String listSerialItems;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getListSerialItems() {
        return listSerialItems;
    }

    public void setListSerialItems(String listSerialItems) {
        this.listSerialItems = listSerialItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoxInfoDTO)) {
            return false;
        }

        BoxInfoDTO boxInfoDTO = (BoxInfoDTO) o;
        if (this.boxCode == null) {
            return false;
        }
        return Objects.equals(this.boxCode, boxInfoDTO.boxCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.boxCode);
    }

    @Override
    public String toString() {
        return (
            "BoxInfoDTO{" +
            "note='" +
            getNote() +
            "'" +
            ", boxCode='" +
            getBoxCode() +
            "'" +
            ", quantity=" +
            getQuantity() +
            ", listSerialItems='" +
            getListSerialItems() +
            "'" +
            "}"
        );
    }
}
