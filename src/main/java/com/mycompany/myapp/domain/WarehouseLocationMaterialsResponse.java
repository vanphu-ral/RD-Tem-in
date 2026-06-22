package com.mycompany.myapp.domain;

import java.util.ArrayList;
import java.util.List;

public class WarehouseLocationMaterialsResponse {

    private List<WarehouseLocationMaterialItemDto> items = new ArrayList<>();
    private Integer totalCount;
    private Integer page;
    private Integer size;

    public List<WarehouseLocationMaterialItemDto> getItems() {
        return items;
    }

    public void setItems(List<WarehouseLocationMaterialItemDto> items) {
        this.items = items;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
