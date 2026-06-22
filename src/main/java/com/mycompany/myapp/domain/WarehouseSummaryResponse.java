package com.mycompany.myapp.domain;

import com.mycompany.panacimmc.domain.WarehouseAreaSummaryResponse;
import com.mycompany.panacimmc.domain.WarehouseSummaryStatsResponse;
import java.util.ArrayList;
import java.util.List;

public class WarehouseSummaryResponse {

    private Integer totalItems;
    private WarehouseSummaryStatsResponse stats;
    private List<WarehouseAreaSummaryResponse> inventories;
    private List<WarehouseOverviewAreaDto> overviewAreas = new ArrayList<>();
    private List<WarehouseOverviewMaterialTypeDto> overviewMaterialTypes =
        new ArrayList<>();

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

    public List<WarehouseOverviewAreaDto> getOverviewAreas() {
        return overviewAreas;
    }

    public void setOverviewAreas(List<WarehouseOverviewAreaDto> overviewAreas) {
        this.overviewAreas = overviewAreas;
    }

    public List<WarehouseOverviewMaterialTypeDto> getOverviewMaterialTypes() {
        return overviewMaterialTypes;
    }

    public void setOverviewMaterialTypes(
        List<WarehouseOverviewMaterialTypeDto> overviewMaterialTypes
    ) {
        this.overviewMaterialTypes = overviewMaterialTypes;
    }
}
