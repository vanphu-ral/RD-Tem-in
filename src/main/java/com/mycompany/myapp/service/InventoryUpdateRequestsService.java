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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

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

    //danh sach phe duyet va lich su co phan trang
    public Page<InventoryUpdateRequests> getPendingRequests(
        int page,
        int size
    ) {
        int offset = page * size;
        List<InventoryUpdateRequests> content =
            inventoryUpdateRequestsRepository.getPendingRequests(offset, size);
        long total = inventoryUpdateRequestsRepository.countPendingRequests();
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(content, pageable, total);
    }

    public Page<InventoryUpdateRequests> getHistoryRequests(
        int page,
        int size
    ) {
        int offset = page * size;
        List<InventoryUpdateRequests> content =
            inventoryUpdateRequestsRepository.getHistoryRequests(offset, size);
        long total = inventoryUpdateRequestsRepository.countHistoryRequests();
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(content, pageable, total);
    }

    public void updateInfo(RequestDTO requestDTO) {
        InventoryUpdateRequests requests = requestDTO.getRequest();
        this.inventoryUpdateRequestsRepository.save(requests);
        System.out.println("check request id :::" + requests.getId());
        for (InventoryUpdateRequestsDetail item : requestDTO.getDetail()) {
            item.setRequestId(requests.getId());
            //insert history
            Inventory inventory =
                this.inventoryRepository.findAllByMaterialIdentifier(
                    item.getMaterialId()
                );
            Location location = this.locationRepository.findById(
                inventory.getLocationId()
            ).orElse(null);
            InventoryUpdateRequestsHistory history =
                new InventoryUpdateRequestsHistory();
            history.setMaterialId(item.getMaterialId());
            history.setRequestedTime(item.getCreatedTime());
            history.setApprovedTime(item.getUpdatedTime());
            history.setRequestedBy(requests.getRequestedBy());
            history.setApprovedBy(requests.getApprovedBy());
            history.setReqApprover(requests.getReqApprover());
            history.setRequestCode(requests.getRequestCode());
            history.setOldLocation(location.getLocationName());
            history.setNewLocation(item.getLocationName());
            history.setRequestType(item.getType());
            history.setQuantity(item.getQuantity());
            history.setQuantityChange(item.getQuantityChange());
            history.setStatus(item.getStatus());
            System.out.println("check status :: " + requests.getStatus());
            this.inventoryUpdateRequestsDetailRepository.save(item);
            if (requests.getStatus().equals("APPROVE")) {
                long timestampNow = Instant.now().getEpochSecond();
                if (item.getType().equals("EXTEND")) {
                    // *gia han
                    long timestamp = LocalDate.now()
                        .plusDays(15)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toEpochSecond();
                    history.setExpiredTime(timestamp);
                    System.out.println("tao thong tin history expired");
                    this.inventoryUpdateRequestsHistoryRepository.save(history);
                    this.inventoryRepository.extendIventory(
                        item.getQuantityChange(),
                        item.getUpdatedBy(),
                        String.valueOf(timestamp),
                        String.valueOf(timestampNow),
                        item.getLocationId(),
                        item.getMaterialId()
                    );
                } else if (item.getType().equals("MOVE")) {
                    // *MOVE
                    System.out.println(
                        "tao thong tin history move" + item.getLocationName()
                    );
                    this.inventoryRepository.updateIventory(
                        item.getQuantityChange(),
                        item.getUpdatedBy(),
                        String.valueOf(timestampNow),
                        item.getLocationId(),
                        item.getMaterialId()
                    );
                    this.inventoryUpdateRequestsHistoryRepository.save(history);
                }
            }
            if (requests.getStatus().equals("REJECT")) {
                long timestampNow = Instant.now().getEpochSecond();

                if (item.getType().equals("EXTEND")) {
                    long timestamp = LocalDate.now()
                        .plusDays(15)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toEpochSecond();
                    history.setExpiredTime(timestamp);
                    System.out.println("Lưu lịch sử gia hạn (REJECT)");
                }

                if (item.getType().equals("MOVE")) {
                    System.out.println(
                        "Lưu lịch sử chuyển kho (REJECT): " +
                        item.getLocationName()
                    );
                }

                this.inventoryUpdateRequestsHistoryRepository.save(history);
            }
        }
    }
}
