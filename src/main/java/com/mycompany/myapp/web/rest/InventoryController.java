package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.InventoriesResponse;
import com.mycompany.myapp.service.InventoryService;
import com.mycompany.myapp.service.dto.InventoryDTO;
import com.mycompany.myapp.service.dto.InventoryRequestDTO;
import com.mycompany.panacimmc.domain.Inventory;
import com.mycompany.panacimmc.domain.InventoryResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/inventory")
public class InventoryController {

    @Autowired
    InventoryService inventoryService;

    @PostMapping("")
    public InventoriesResponse getInventoriesResponse(
        @RequestBody InventoryRequestDTO request
    ) {
        return this.inventoryService.getInventoriesResponse(request);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateInventory(
        @RequestBody List<InventoryDTO> request
    ) {
        try {
            boolean isUpdated = inventoryService.updateInventory(request);

            if (isUpdated) {
                return ResponseEntity.ok("Inventory updated successfully.");
            } else {
                return ResponseEntity.status(404).body(
                    "No inventory data found."
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                "Error updating inventory:: " + e.getMessage()
            );
        }
    }
}
