package com.mycompany.myapp.service.dto;

import com.mycompany.wmsral.domain.InventoryUpdateRequests;
import com.mycompany.wmsral.domain.InventoryUpdateRequestsDetail;

import java.util.List;

public class RequestDTO {
    private InventoryUpdateRequests request;
    private List<InventoryUpdateRequestsDetail> detail;

    public InventoryUpdateRequests getRequest() {
        return request;
    }

    public void setRequest(InventoryUpdateRequests request) {
        this.request = request;
    }

    public List<InventoryUpdateRequestsDetail> getDetail() {
        return detail;
    }

    public void setDetail(List<InventoryUpdateRequestsDetail> detail) {
        this.detail = detail;
    }
}
