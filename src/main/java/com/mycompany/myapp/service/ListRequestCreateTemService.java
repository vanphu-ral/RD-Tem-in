package com.mycompany.myapp.service;

import com.mycompany.renderQr.domain.ListProductOfRequestResponse;
import com.mycompany.renderQr.domain.ListRequestCreateTem;
import com.mycompany.renderQr.domain.ListRequestCreateTemResponse;
import com.mycompany.renderQr.repository.ListProductOfRequestRepository;
import com.mycompany.renderQr.repository.ListRequestCreateTemRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListRequestCreateTemService {

    @Autowired
    private ListRequestCreateTemRepository repository;

    public List<ListRequestCreateTemResponse> getAll() {
        return repository.findAllProjectedBy();
    }

    @Transactional
    public ListRequestCreateTem createRequest(
        String vendor,
        String userData5,
        String createdBy,
        int numberProduction,
        long totalQuantity
    ) {
        ListRequestCreateTem request = new ListRequestCreateTem();
        request.setVendor(vendor);
        request.setUserData5(userData5);
        request.setCreatedBy(createdBy != null ? createdBy : "system");
        request.setNumberProduction((short) numberProduction);
        request.setTotalQuantity(totalQuantity);
        request.setStatus("Bản nháp");

        return repository.save(request);
    }

    @Transactional
    public void deleteRequest(Long id) {
        repository.deleteById(id);
    }
}
