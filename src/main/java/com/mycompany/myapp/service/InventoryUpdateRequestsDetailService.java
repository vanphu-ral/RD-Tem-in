package com.mycompany.myapp.service;

import com.mycompany.wmsral.domain.InventoryUpdateRequestsDetail;
import com.mycompany.wmsral.repository.InventoryUpdateRequestsDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryUpdateRequestsDetailService {
    @Autowired
    InventoryUpdateRequestsDetailRepository inventoryUpdateRequestsDetailRepository;
    public List<InventoryUpdateRequestsDetail> getByRequestId(Long id){
        return this.inventoryUpdateRequestsDetailRepository.findAllByRequestId(id);
    }
}
