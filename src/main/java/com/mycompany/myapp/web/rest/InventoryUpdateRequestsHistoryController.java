package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.InventoryUpdateRequestsHistoryService;
import com.mycompany.myapp.service.dto.TimeRangeDTO;
import com.mycompany.wmsral.domain.InventoryUpdateRequestsHistory;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/request/history")
@RestController
public class InventoryUpdateRequestsHistoryController {

    @Autowired
    InventoryUpdateRequestsHistoryService inventoryUpdateRequestsHistoryService;

    @PostMapping
    public List<InventoryUpdateRequestsHistory> getAllByDateRange(
        @RequestBody TimeRangeDTO timeRangeDTO
    ) {
        return this.inventoryUpdateRequestsHistoryService.getAllByRange(
                timeRangeDTO
            );
    }

    @GetMapping("/{requestCode}")
    public List<InventoryUpdateRequestsHistory> findAllByRequestCode(
        @PathVariable String requestCode
    ) {
        return this.inventoryUpdateRequestsHistoryService.findAllByRequestCode(
                requestCode
            );
    }
}
