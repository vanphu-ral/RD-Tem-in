package com.mycompany.myapp.domain;

import java.util.ArrayList;
import java.util.List;

public class WarehouseAreaInventoryItemsResponse {

    private List<WarehouseAreaInventoryItemDto> items = new ArrayList<>();
    private Integer totalCount;
    private Integer previewLimit;
    private Boolean previewLimited;

    public List<WarehouseAreaInventoryItemDto> getItems() {
        return items;
    }

    public void setItems(List<WarehouseAreaInventoryItemDto> items) {
        this.items = items;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPreviewLimit() {
        return previewLimit;
    }

    public void setPreviewLimit(Integer previewLimit) {
        this.previewLimit = previewLimit;
    }

    public Boolean getPreviewLimited() {
        return previewLimited;
    }

    public void setPreviewLimited(Boolean previewLimited) {
        this.previewLimited = previewLimited;
    }
}
