package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.InventoriesResponse;
import com.mycompany.myapp.domain.WarehouseAreaInventoryItemsResponse;
import com.mycompany.myapp.domain.WarehouseAreaLocationsResponse;
import com.mycompany.myapp.domain.WarehouseLocationMaterialsResponse;
import com.mycompany.myapp.domain.WarehouseSummaryResponse;
import com.mycompany.myapp.service.InventoryService;
import com.mycompany.myapp.service.dto.InventoryDTO;
import com.mycompany.myapp.service.dto.InventoryRequestDTO;
import com.mycompany.myapp.service.dto.WarehouseSummaryRequestDTO;
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

    @PostMapping("/group-by-user-data-5")
    public InventoriesResponse getDataGroupByUserData5(
        @RequestBody InventoryRequestDTO request
    ) {
        System.out.println("Cong doan: 4.5");
        return this.inventoryService.getDataGroupByUserData5(request);
    }

    @PostMapping("/group-by-location-name")
    public InventoriesResponse getDataGroupByLocationName(
        @RequestBody InventoryRequestDTO request
    ) {
        System.out.println("Cong doan: 5");
        return this.inventoryService.getDataGroupByLocationName(request);
    }

    @GetMapping("/warehouse-summary/material-types")
    public List<String> getWarehouseSummaryMaterialTypes() {
        return this.inventoryService.getWarehouseSummaryMaterialTypes();
    }

    @GetMapping("/warehouse-summary/area-locations")
    public WarehouseAreaLocationsResponse getWarehouseAreaLocations(
        @RequestParam String areaCode,
        @RequestParam(required = false, defaultValue = "") String areaName,
        @RequestParam(required = false, defaultValue = "") String materialType,
        @RequestParam(required = false, defaultValue = "") String locationSearch
    ) {
        return this.inventoryService.getWarehouseAreaLocations(
            areaCode,
            areaName,
            materialType,
            locationSearch
        );
    }

    @GetMapping("/warehouse-summary/area-materials")
    public WarehouseAreaInventoryItemsResponse getWarehouseAreaInventoryItems(
        @RequestParam String areaCode,
        @RequestParam(required = false, defaultValue = "") String areaName,
        @RequestParam(
            required = false,
            defaultValue = ""
        ) String materialSearch,
        @RequestParam(
            required = false,
            defaultValue = "sap"
        ) String searchField,
        @RequestParam(
            required = false,
            defaultValue = "100"
        ) Integer previewLimit
    ) {
        return this.inventoryService.getWarehouseAreaInventoryItems(
            areaCode,
            areaName,
            materialSearch,
            searchField,
            previewLimit
        );
    }

    @PostMapping("/warehouse-summary")
    public WarehouseSummaryResponse getWarehouseSummary(
        @RequestBody WarehouseSummaryRequestDTO request,
        @RequestParam(
            value = "refreshCache",
            required = false,
            defaultValue = "false"
        ) boolean refreshCache
    ) {
        request.setRefreshCache(refreshCache);
        return this.inventoryService.getWarehouseSummary(request);
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

    @GetMapping("/warehouse-summary/location-materials")
    public WarehouseLocationMaterialsResponse getLocationInventoryMaterials(
        @RequestParam String locationName,
        @RequestParam(required = false, defaultValue = "") String qrCode,
        @RequestParam(required = false, defaultValue = "") String materialName,
        @RequestParam(required = false, defaultValue = "") String itemCode,
        @RequestParam(required = false, defaultValue = "") String partNumber,
        @RequestParam(required = false, defaultValue = "") String lotNumber,
        @RequestParam(required = false, defaultValue = "") String materialType,
        @RequestParam(required = false, defaultValue = "") String status,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "25") Integer size
    ) {
        return this.inventoryService.getLocationInventoryMaterials(
            locationName,
            qrCode,
            materialName,
            itemCode,
            partNumber,
            lotNumber,
            materialType,
            status,
            page,
            size
        );
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
