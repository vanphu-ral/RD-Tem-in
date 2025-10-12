package com.mycompany.myapp.service;

import com.mycompany.renderQr.domain.ListProductOfRequestResponse;
import com.mycompany.renderQr.domain.ListRequestCreateTemResponse;
import com.mycompany.renderQr.repository.ListProductOfRequestRepository;
import com.mycompany.renderQr.repository.ListRequestCreateTemRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

@Service
public class ListProductOfRequestService {

    @Autowired
    private ListProductOfRequestRepository repository;

    public List<ListProductOfRequestResponse> getAll() {
        return repository.findAllProjectedBy();
    }
}
