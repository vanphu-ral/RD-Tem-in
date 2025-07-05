package com.mycompany.myapp.domain;

import com.mycompany.panacimmc.domain.InventoryResponse;
import java.util.List;

public class InventoriesResponse {

    private Integer totalItems;
    private List<InventoryResponse> inventories;

    public InventoriesResponse(
        Integer totalItems,
        List<InventoryResponse> inventories
    ) {
        this.totalItems = totalItems;
        this.inventories = inventories;
    }

    public InventoriesResponse() {}

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public List<InventoryResponse> getInventories() {
        return inventories;
    }

    public void setInventories(List<InventoryResponse> inventories) {
        this.inventories = inventories;
    }
}
