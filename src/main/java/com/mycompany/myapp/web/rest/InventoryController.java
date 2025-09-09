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
        System.out.println("Cong doan: 1");
        return this.inventoryService.getInventoriesResponse(request);
    }

    @PostMapping("/group-by-part-number")
    public InventoriesResponse getDataGroupByPartNumber(
        @RequestBody InventoryRequestDTO request
    ) {
        System.out.println("Cong doan: 2");
        return this.inventoryService.getDataGroupByPartNumber(request);
    }

    @PostMapping("/group-by-lot-number")
    public InventoriesResponse getDataGroupByLotNumber(
        @RequestBody InventoryRequestDTO request
    ) {
        System.out.println("Cong doan: 3");
        return this.inventoryService.getDataGroupByLotNumber(request);
    }

    @PostMapping("/group-by-user-data-4")
    public InventoriesResponse getDataGroupByUserData4(
        @RequestBody InventoryRequestDTO request
    ) {
        System.out.println("Cong doan: 4");
        return this.inventoryService.getDataGroupByUserData4(request);
    }

    @PostMapping("/group-by-location-name")
    public InventoriesResponse getDataGroupByLocationName(
        @RequestBody InventoryRequestDTO request
    ) {
        System.out.println("Cong doan: 5");
        return this.inventoryService.getDataGroupByLocationName(request);
    }

    @PostMapping("/detail")
    public InventoriesResponse getDataDetail(
        @RequestBody InventoryRequestDTO request
    ) {
        System.out.println("Cong doan: 6");
        return this.inventoryService.getDataDetail(request);
    }

    @GetMapping("")
    public List<InventoryResponse> getDataNew(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "1000") int size
    ) {
        System.out.println("Cong doan: 7");
        int offset = page * size;
        return this.inventoryService.serachNew(offset, size);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateInventory(
        @RequestBody List<InventoryDTO> request
    ) {
        System.out.println("Cong doan: 8");
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

    @GetMapping("/{materialIdentifier}")
    public ResponseEntity<InventoryResponse> getInventoryByMaterialIdentifier(
        @PathVariable String materialIdentifier
    ) {
        System.out.println("Cong doan: 9");
        InventoryResponse response = inventoryService.findByInventoryId(
            materialIdentifier
        );
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/scan/{locationName}")
    public ResponseEntity<List<InventoryResponse>> scanByLocation(
        @PathVariable String locationName
    ) {
        System.out.println("Cong doan: 10");
        List<InventoryResponse> list =
            inventoryService.findByInventoryLocationName(locationName);
        if (list.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }
}
