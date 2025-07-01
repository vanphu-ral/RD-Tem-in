package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.RequestDTO;
import com.mycompany.panacimmc.domain.Inventory;
import com.mycompany.panacimmc.domain.Location;
import com.mycompany.panacimmc.repository.InventoryRepository;
import com.mycompany.panacimmc.repository.LocationRepository;
import com.mycompany.wmsral.domain.InventoryUpdateRequests;
import com.mycompany.wmsral.domain.InventoryUpdateRequestsDetail;
import com.mycompany.wmsral.domain.InventoryUpdateRequestsHistory;
import com.mycompany.wmsral.repository.InventoryUpdateRequestsDetailRepository;
import com.mycompany.wmsral.repository.InventoryUpdateRequestsHistoryRepository;
import com.mycompany.wmsral.repository.InventoryUpdateRequestsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class InventoryUpdateRequestsService {
    @Autowired
    InventoryUpdateRequestsRepository inventoryUpdateRequestsRepository;
    @Autowired
    InventoryUpdateRequestsDetailRepository inventoryUpdateRequestsDetailRepository;
    @Autowired
    InventoryRepository inventoryRepository;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    InventoryUpdateRequestsHistoryRepository inventoryUpdateRequestsHistoryRepository;

    public List<InventoryUpdateRequests> getAll() {
        return this.inventoryUpdateRequestsRepository.findAll();
    }
    public void updateInfo(RequestDTO requestDTO) {
        InventoryUpdateRequests requests = requestDTO.getRequest();
        this.inventoryUpdateRequestsRepository.save(requests);
        System.out.println("check request id :::" + requests.getId());
        for (InventoryUpdateRequestsDetail item : requestDTO.getDetail()) {
            item.setRequestId(requests.getId());
            //insert history
            Inventory inventory = this.inventoryRepository.findAllByMaterialIdentifier(item.getMaterialId());
            Location location = this.locationRepository.findById(inventory.getLocationId()).orElse(null);
            InventoryUpdateRequestsHistory history = new InventoryUpdateRequestsHistory();
            history.setMaterialId(item.getMaterialId());
            history.setRequestedTime(item.getCreatedTime());
            history.setApprovedTime(item.getUpdatedTime());
            history.setRequestedBy(requests.getRequestedBy());
            history.setApprovedBy(requests.getApprovedBy());
            history.setRequestCode(requests.getRequestCode());
            history.setOldLocation(location.getLocationName());
            history.setNewLocation(item.getLocationName());
            history.setRequestType(item.getType());
            history.setQuantity(item.getQuantity());
            history.setQuantityChange(item.getQuantityChange());
            history.setStatus(item.getStatus() );
            System.out.println("check status :: "+ requests.getStatus());
            this.inventoryUpdateRequestsDetailRepository.save(item);
            if (requests.getStatus().equals("APPROVE")) {
                long timestampNow = Instant.now().getEpochSecond();
                if (item.getType().equals("EXTEND")) {// *gia han
                    long timestamp = LocalDate.now().plusDays(15)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toEpochSecond();
                    history.setExpiredTime(timestamp);
                    System.out.println("tao thong tin history expired");
                    this.inventoryUpdateRequestsHistoryRepository.save(history);
                    this.inventoryRepository.extendIventory(item.getQuantity(), item.getUpdatedBy(), String.valueOf(timestamp), String.valueOf(timestampNow), item.getMaterialId());
                } else if (item.getType().equals("MOVE")) {// *MOVE
                    System.out.println("tao thong tin history move"+ item.getLocationName());
                    this.inventoryRepository.updateIventory(item.getQuantity(), item.getUpdatedBy(),  String.valueOf(timestampNow),item.getMaterialId());
                    this.inventoryUpdateRequestsHistoryRepository.save(history);
                }
            }
            if (requests.getStatus().equals("REJECT")) {
                long timestampNow = Instant.now().getEpochSecond();
                if (item.getType().equals("EXTEND")) {// *gia han
                    long timestamp = LocalDate.now().plusDays(15)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toEpochSecond();
                    history.setExpiredTime(timestamp);
                    System.out.println("tao thong tin history expired");
                    this.inventoryUpdateRequestsHistoryRepository.save(history);
                    this.inventoryRepository.extendIventory(item.getQuantity(), item.getUpdatedBy(), String.valueOf(timestamp), String.valueOf(timestampNow), item.getMaterialId());
                } else if (item.getType().equals("MOVE")) {// *MOVE
                    System.out.println("tao thong tin history move"+ item.getLocationName());
                    this.inventoryRepository.updateIventory(item.getQuantity(), item.getUpdatedBy(),  String.valueOf(timestampNow),item.getMaterialId());
                    this.inventoryUpdateRequestsHistoryRepository.save(history);
                }
            }
        }
    }
}
