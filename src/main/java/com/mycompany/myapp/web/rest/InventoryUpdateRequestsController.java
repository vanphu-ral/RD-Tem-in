package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.InventoryUpdateRequestsService;
import com.mycompany.myapp.service.dto.RequestDTO;
import com.mycompany.wmsral.domain.InventoryUpdateRequests;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/request")
public class InventoryUpdateRequestsController {

    @Autowired
    InventoryUpdateRequestsService inventoryUpdateRequestsService;

    @PostMapping("")
    public void updateInSert(@RequestBody RequestDTO requestDTO) {
        System.out.println("Cong doan: 11");
        this.inventoryUpdateRequestsService.updateInfo(requestDTO);
    }

    @GetMapping("")
    public List<InventoryUpdateRequests> getAll() {
        System.out.println("Cong doan: 12");
        return this.inventoryUpdateRequestsService.getAll();
    }
}
