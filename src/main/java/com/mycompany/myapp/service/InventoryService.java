package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.InventoryDTO;
import com.mycompany.panacimmc.domain.Inventory;
import com.mycompany.panacimmc.domain.InventoryResponse;
import com.mycompany.panacimmc.repository.InventoryRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    @Autowired
    InventoryRepository inventoryRepository;
    public List<Inventory> getAll(){
        return  this.inventoryRepository.findAll();
    }
    public List<InventoryResponse> getInventories(){
        return this.inventoryRepository.getInventories();
    }
    public boolean updateInventory(List<InventoryDTO> request){
        boolean updated = false;
        try {
            for (InventoryDTO inventoryDTO : request) {
                Inventory inventory = this.inventoryRepository.findAllByMaterialIdentifier(inventoryDTO.getMaterialIdentifier());
                if (inventory != null) {
                    long timestampNow = Instant.now().getEpochSecond();
                    updated = true;
                    inventory.setQuantity(inventoryDTO.getQuantity());
                    inventory.setLocationId(inventoryDTO.getLocationId());
                    inventory.setStatus(inventoryDTO.getStatus());
                    inventory.setUpdatedBy(inventoryDTO.getUpdatedBy());
                    inventory.setUpdatedDate(timestampNow);

                    if (inventoryDTO.getType()) {// *gia han
                        long timestamp = LocalDate.now().plusDays(15)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toEpochSecond();
                        inventory.setExpirationDate(timestamp);
                    }
                    this.inventoryRepository.save(inventory);
                }
            }
        }catch (Exception e){
            throw new RuntimeException("Error updating inventory:: " + e.getMessage());
        }
        return updated;
    }
}
