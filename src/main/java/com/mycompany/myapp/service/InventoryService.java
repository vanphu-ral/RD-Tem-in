package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.InventoriesResponse;
import com.mycompany.myapp.service.dto.InventoryDTO;
import com.mycompany.myapp.service.dto.InventoryRequestDTO;
import com.mycompany.panacimmc.domain.Inventory;
import com.mycompany.panacimmc.domain.InventoryResponse;
import com.mycompany.panacimmc.repository.InventoryRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    @Autowired
    InventoryRepository inventoryRepository;

    public List<Inventory> getAll() {
        return this.inventoryRepository.findAll();
    }

    public List<InventoryResponse> serachNew() {
        return this.inventoryRepository.getDataNew();
    }

    public InventoriesResponse getDataGroupByPartNumber(
        InventoryRequestDTO request
    ) {
        InventoriesResponse inventoriesResponse = new InventoriesResponse();
        inventoriesResponse.setTotalItems(
            this.inventoryRepository.getTotalDataGroupByPartNumber(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%"
            )
        );
        inventoriesResponse.setInventories(
            this.inventoryRepository.getDataGroupByPartNumber(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                (request.getPageNumber() - 1) * request.getItemPerPage(),
                request.getItemPerPage()
            )
        );
        return inventoriesResponse;
    }

    public InventoriesResponse getDataGroupByLotNumber(
        InventoryRequestDTO request
    ) {
        InventoriesResponse inventoriesResponse = new InventoriesResponse();
        inventoriesResponse.setTotalItems(
            this.inventoryRepository.getTotalDataGroupByLotNumber(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLotNumber()).orElse("") +
                "%"
            )
        );
        inventoriesResponse.setInventories(
            this.inventoryRepository.getDataGroupByLotNumber(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLotNumber()).orElse("") +
                "%",
                (request.getPageNumber() - 1) * request.getItemPerPage(),
                request.getItemPerPage()
            )
        );
        return inventoriesResponse;
    }

    public InventoriesResponse getDataGroupByUserData4(
        InventoryRequestDTO request
    ) {
        InventoriesResponse inventoriesResponse = new InventoriesResponse();
        inventoriesResponse.setTotalItems(
            this.inventoryRepository.getTotalDataGroupByUserData4(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getUserData4()).orElse("") +
                "%"
            )
        );
        inventoriesResponse.setInventories(
            this.inventoryRepository.getDataGroupByUserData4(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getUserData4()).orElse("") +
                "%",
                (request.getPageNumber() - 1) * request.getItemPerPage(),
                request.getItemPerPage()
            )
        );
        return inventoriesResponse;
    }

    public InventoriesResponse getDataGroupByLocationName(
        InventoryRequestDTO request
    ) {
        InventoriesResponse inventoriesResponse = new InventoriesResponse();
        inventoriesResponse.setTotalItems(
            this.inventoryRepository.getTotalDataGroupByLocationName(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLocationName()).orElse("") +
                "%"
            )
        );
        inventoriesResponse.setInventories(
            this.inventoryRepository.getDataGroupByLocationName(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLocationName()).orElse("") +
                "%",
                (request.getPageNumber() - 1) * request.getItemPerPage(),
                request.getItemPerPage()
            )
        );
        return inventoriesResponse;
    }

    public InventoriesResponse getDataDetail(InventoryRequestDTO request) {
        InventoriesResponse inventoriesResponse = new InventoriesResponse();
        inventoriesResponse.setTotalItems(
            this.inventoryRepository.getTotalDataDetail(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLotNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getUserData4()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLocationName()).orElse("") +
                "%"
            )
        );
        inventoriesResponse.setInventories(
            this.inventoryRepository.getDataDetail(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLotNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getUserData4()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLocationName()).orElse("") +
                "%",
                (request.getPageNumber() - 1) * request.getItemPerPage(),
                request.getItemPerPage()
            )
        );
        return inventoriesResponse;
    }

    public InventoriesResponse getInventoriesResponse(
        InventoryRequestDTO request
    ) {
        List<InventoryResponse> inventories = this.getInventories(request);
        Integer totalItems = this.getTotalInventories(request);
        return new InventoriesResponse(totalItems, inventories);
    }

    public List<InventoryResponse> getInventories(InventoryRequestDTO request) {
        System.out.println("get pageNumber: " + request.getPageNumber());
        Long expirationTimestamp = Optional.ofNullable(
            request.getExpirationDate()
        )
            .filter(s -> !s.isEmpty())
            .map(Long::parseLong)
            .orElse(null);
        return this.inventoryRepository.getInventories(
            "%" +
            Optional.ofNullable(request.getMaterialIdentifier()).orElse("") +
            "%",
            "%" + Optional.ofNullable(request.getStatus()).orElse("") + "%",
            "%" + Optional.ofNullable(request.getPartNumber()).orElse("") + "%",
            request.getQuantity(),
            request.getAvailableQuantity(),
            "%" + Optional.ofNullable(request.getLotNumber()).orElse("") + "%",
            "%" + Optional.ofNullable(request.getUserData4()).orElse("") + "%",
            "%" +
            Optional.ofNullable(request.getLocationName()).orElse("") +
            "%",
            expirationTimestamp,
            (request.getPageNumber() - 1) * request.getItemPerPage(),
            request.getItemPerPage()
        );
    }

    public Integer getTotalInventories(InventoryRequestDTO request) {
        Long expirationTimestamp = Optional.ofNullable(
            request.getExpirationDate()
        )
            .filter(s -> !s.isEmpty())
            .map(Long::parseLong)
            .orElse(null);
        return this.inventoryRepository.getTotalInventories(
            "%" +
            Optional.ofNullable(request.getMaterialIdentifier()).orElse("") +
            "%",
            "%" + Optional.ofNullable(request.getStatus()).orElse("") + "%",
            "%" + Optional.ofNullable(request.getPartNumber()).orElse("") + "%",
            request.getQuantity(),
            request.getAvailableQuantity(),
            "%" + Optional.ofNullable(request.getLotNumber()).orElse("") + "%",
            "%" + Optional.ofNullable(request.getUserData4()).orElse("") + "%",
            "%" +
            Optional.ofNullable(request.getLocationName()).orElse("") +
            "%",
            expirationTimestamp
        );
    }

    public boolean updateInventory(List<InventoryDTO> request) {
        boolean updated = false;
        try {
            for (InventoryDTO inventoryDTO : request) {
                Inventory inventory =
                    this.inventoryRepository.findAllByMaterialIdentifier(
                        inventoryDTO.getMaterialIdentifier()
                    );
                if (inventory != null) {
                    long timestampNow = Instant.now().getEpochSecond();
                    updated = true;
                    inventory.setQuantity(inventoryDTO.getQuantity());
                    inventory.setLocationId(inventoryDTO.getLocationId());
                    inventory.setStatus(inventoryDTO.getStatus());
                    inventory.setUpdatedBy(inventoryDTO.getUpdatedBy());
                    inventory.setUpdatedDate(timestampNow);

                    if (inventoryDTO.getType()) {
                        // *gia han
                        long timestamp = LocalDate.now()
                            .plusDays(15)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toEpochSecond();
                        inventory.setExpirationDate(timestamp);
                    }
                    this.inventoryRepository.save(inventory);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error updating inventory:: " + e.getMessage()
            );
        }
        return updated;
    }
}
