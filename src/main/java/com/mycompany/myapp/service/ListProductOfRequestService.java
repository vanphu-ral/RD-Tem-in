package com.mycompany.myapp.service;

import com.mycompany.myapp.graphql.dto.CreateProductInput;
import com.mycompany.renderQr.domain.ListProductOfRequest;
import com.mycompany.renderQr.domain.ListProductOfRequestResponse;
import com.mycompany.renderQr.domain.ListRequestCreateTemResponse;
import com.mycompany.renderQr.repository.ListProductOfRequestRepository;
import com.mycompany.renderQr.repository.ListRequestCreateTemRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListProductOfRequestService {

    @Autowired
    private ListProductOfRequestRepository repository;

    public List<ListProductOfRequestResponse> getAll() {
        return repository.findAllProjectedBy();
    }

    @Transactional
    public ListProductOfRequest createProduct(CreateProductInput input) {
        ListProductOfRequest product = new ListProductOfRequest();
        mapInputToEntity(input, product);
        return repository.save(product);
    }

    @Transactional
    public List<ListProductOfRequest> createProductsBatch(
        Integer requestId,
        List<CreateProductInput> inputs
    ) {
        List<ListProductOfRequest> products = new ArrayList<>();

        for (CreateProductInput input : inputs) {
            ListProductOfRequest product = new ListProductOfRequest();
            // Override requestCreateTemId with the provided requestId
            input.setRequestCreateTemId(requestId);
            mapInputToEntity(input, product);
            products.add(product);
        }

        return repository.saveAll(products);
    }

    private void mapInputToEntity(
        CreateProductInput input,
        ListProductOfRequest product
    ) {
        product.setRequestCreateTemId(
            input.getRequestCreateTemId().longValue()
        );
        product.setSapCode(input.getSapCode());
        product.setTemQuantity(input.getTemQuantity());
        product.setPartNumber(input.getPartNumber());
        product.setLot(input.getLot());
        product.setInitialQuantity(input.getInitialQuantity().longValue());
        product.setVendor(input.getVendor());
        product.setUserData1(
            input.getUserData1() != null ? input.getUserData1() : ""
        );
        product.setUserData2(
            input.getUserData2() != null ? input.getUserData2() : ""
        );
        product.setUserData3(
            input.getUserData3() != null ? input.getUserData3() : ""
        );
        product.setUserData4(
            input.getUserData4() != null ? input.getUserData4() : ""
        );
        product.setUserData5(
            input.getUserData5() != null ? input.getUserData5() : ""
        );
        product.setStorageUnit(input.getStorageUnit());
        product.setNumberOfPrints(0);

        // Parse date strings to LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        product.setExpirationDate(
            parseDate(input.getExpirationDate(), formatter)
        );
        product.setManufacturingDate(
            parseDate(input.getManufacturingDate(), formatter)
        );
        product.setArrivalDate(parseDate(input.getArrivalDate(), formatter));
    }

    private LocalDateTime parseDate(
        String dateStr,
        DateTimeFormatter formatter
    ) {
        try {
            if (dateStr != null && !dateStr.isEmpty()) {
                return LocalDateTime.parse(dateStr + "T00:00:00");
            }
        } catch (Exception e) {
            System.err.println(
                "Error parsing date: " + dateStr + " - " + e.getMessage()
            );
        }
        return LocalDateTime.now();
    }
}
