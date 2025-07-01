package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.InventoryUpdateRequestsService;
import com.mycompany.myapp.service.dto.RequestDTO;
import com.mycompany.wmsral.domain.InventoryUpdateRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("request")
public class InventoryUpdateRequestsController {
    @Autowired
    InventoryUpdateRequestsService inventoryUpdateRequestsService;

    @PostMapping("")
    public void updateInSert(@RequestBody RequestDTO requestDTO) {
        this.inventoryUpdateRequestsService. updateInfo(requestDTO);
    }
    @GetMapping("")
    public List<InventoryUpdateRequests> getAll(){
        return this.inventoryUpdateRequestsService.getAll();
    }
}
