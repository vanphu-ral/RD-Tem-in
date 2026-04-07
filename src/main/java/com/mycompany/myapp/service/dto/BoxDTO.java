package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoxDTO {

    @JsonProperty("box_code")
    @JsonAlias("boxCode")
    private String boxCode;

    private Integer quantity;

    private String note;

    @JsonProperty("list_serial_items")
    @JsonAlias("listSerialItems")
    private String listSerialItems;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getListSerialItems() {
        return listSerialItems;
    }

    public void setListSerialItems(String listSerialItems) {
        this.listSerialItems = listSerialItems;
    }
}
