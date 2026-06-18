package com.mycompany.myapp.domain;

import com.mycompany.panacimmc.domain.WarehouseAreaSummaryResponse;
import com.mycompany.panacimmc.domain.WarehouseSummaryStatsResponse;
import java.util.List;

public class WarehouseSummaryResponse {

    private Integer totalItems;
    private WarehouseSummaryStatsResponse stats;
    private List<WarehouseAreaSummaryResponse> inventories;

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public WarehouseSummaryStatsResponse getStats() {
        return stats;
    }

    public void setStats(WarehouseSummaryStatsResponse stats) {
        this.stats = stats;
    }

    public List<WarehouseAreaSummaryResponse> getInventories() {
        return inventories;
    }

    public void setInventories(List<WarehouseAreaSummaryResponse> inventories) {
        this.inventories = inventories;
    }
}
