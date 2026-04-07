package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GeneralInfoDTO {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("inventory_code")
    private String inventoryCode;

    @JsonProperty("inventory_name")
    private String inventoryName;

    @JsonProperty("wo_code")
    private String woCode;

    @JsonProperty("lot_number")
    private String lotNumber;

    private String note;

    @JsonProperty("created_by")
    private String createdBy;

    private String branch;

    @JsonProperty("production_team")
    private String productionTeam;

    @JsonProperty("number_of_pallet")
    private Integer numberOfPallets;

    @JsonProperty("number_of_box")
    private Integer numberOfBox;

    private Integer quantity;

    @JsonProperty("destination_warehouse")
    private Integer destinationWarehouse;

    @JsonProperty("pallet_note_creation_id")
    private Integer palletNoteCreationId;

    @JsonProperty("production_date")
    private String productionDate;

    @JsonProperty("item_no_sku")
    private String itemNoSku;

    @JsonProperty("list_pallet")
    private List<PalletDTO> listPallet;

    // getters and setters

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public String getInventoryName() {
        return inventoryName;
    }

    public void setInventoryName(String inventoryName) {
        this.inventoryName = inventoryName;
    }

    public String getWoCode() {
        return woCode;
    }

    public void setWoCode(String woCode) {
        this.woCode = woCode;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getProductionTeam() {
        return productionTeam;
    }

    public void setProductionTeam(String productionTeam) {
        this.productionTeam = productionTeam;
    }

    public Integer getNumberOfPallets() {
        return numberOfPallets;
    }

    public void setNumberOfPallets(Integer numberOfPallets) {
        this.numberOfPallets = numberOfPallets;
    }

    public Integer getNumberOfBox() {
        return numberOfBox;
    }

    public void setNumberOfBox(Integer numberOfBox) {
        this.numberOfBox = numberOfBox;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getDestinationWarehouse() {
        return destinationWarehouse;
    }

    public void setDestinationWarehouse(Integer destinationWarehouse) {
        this.destinationWarehouse = destinationWarehouse;
    }

    public Integer getPalletNoteCreationId() {
        return palletNoteCreationId;
    }

    public void setPalletNoteCreationId(Integer palletNoteCreationId) {
        this.palletNoteCreationId = palletNoteCreationId;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    public String getItemNoSku() {
        return itemNoSku;
    }

    public void setItemNoSku(String itemNoSku) {
        this.itemNoSku = itemNoSku;
    }

    public List<PalletDTO> getListPallet() {
        return listPallet;
    }

    public void setListPallet(List<PalletDTO> listPallet) {
        this.listPallet = listPallet;
    }
}
