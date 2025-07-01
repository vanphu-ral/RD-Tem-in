package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.TimeRangeDTO;
import com.mycompany.wmsral.domain.InventoryUpdateRequestsHistory;
import com.mycompany.wmsral.repository.InventoryUpdateRequestsHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryUpdateRequestsHistoryService {
    @Autowired
    InventoryUpdateRequestsHistoryRepository inventoryUpdateRequestsHistoryRepository;
    public List<InventoryUpdateRequestsHistory> getAllByRange(TimeRangeDTO timeRangeDTO){
        return  this.inventoryUpdateRequestsHistoryRepository.getByRequestedTimeRange(timeRangeDTO.getStartTime(), timeRangeDTO.getEndTime());
    }
    public List<InventoryUpdateRequestsHistory> findAllByRequestCode(String requestCode){
        return this.inventoryUpdateRequestsHistoryRepository.findAllByRequestCode(requestCode);
    }
}
