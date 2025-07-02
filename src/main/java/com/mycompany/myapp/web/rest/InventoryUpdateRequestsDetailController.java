package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.InventoryUpdateRequestsDetailService;
import com.mycompany.wmsral.domain.InventoryUpdateRequestsDetail;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/request/detail")
public class InventoryUpdateRequestsDetailController {

    @Autowired
    InventoryUpdateRequestsDetailService inventoryUpdateRequestsDetailService;

    @GetMapping("/{id}")
    public List<InventoryUpdateRequestsDetail> getDetail(
        @PathVariable Long id
    ) {
        return this.inventoryUpdateRequestsDetailService.getByRequestId(id);
    }
}
