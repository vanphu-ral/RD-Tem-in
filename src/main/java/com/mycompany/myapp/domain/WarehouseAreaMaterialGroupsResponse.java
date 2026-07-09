package com.mycompany.myapp.domain;

import java.util.ArrayList;
import java.util.List;

public class WarehouseAreaMaterialGroupsResponse {

    private List<WarehouseAreaMaterialGroupDto> groups = new ArrayList<>();
    private Integer totalCount;
    private Integer previewLimit;
    private Boolean previewLimited;

    public List<WarehouseAreaMaterialGroupDto> getGroups() {
        return groups;
    }

    public void setGroups(List<WarehouseAreaMaterialGroupDto> groups) {
        this.groups = groups;
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
