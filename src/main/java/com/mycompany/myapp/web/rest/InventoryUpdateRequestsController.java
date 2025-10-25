package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.InventoryUpdateRequestsService;
import com.mycompany.myapp.service.dto.RequestDTO;
import com.mycompany.wmsral.domain.InventoryUpdateRequests;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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

    //gọi data phê duyệt/lic sử có theem phân trang
    @GetMapping("/pending")
    public ResponseEntity<Page<InventoryUpdateRequests>> getPendingRequests(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {
        Page<InventoryUpdateRequests> result =
            inventoryUpdateRequestsService.getPendingRequests(page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<Page<InventoryUpdateRequests>> getHistoryRequests(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {
        Page<InventoryUpdateRequests> result =
            inventoryUpdateRequestsService.getHistoryRequests(page, size);
        return ResponseEntity.ok(result);
    }
}
