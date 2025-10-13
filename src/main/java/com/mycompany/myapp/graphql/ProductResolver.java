package com.mycompany.myapp.graphql;

import com.mycompany.myapp.graphql.dto.CreateProductInput;
import com.mycompany.myapp.service.ListProductOfRequestService;
import com.mycompany.renderQr.domain.ListProductOfRequest;
import com.mycompany.renderQr.domain.ListProductOfRequestResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Controller
public class ProductResolver {

    @Autowired
    private ListProductOfRequestService service;

    @QueryMapping
    public List<ListProductOfRequestResponse> listProductOfRequests() {
        return service.getAll();
    }

    @MutationMapping
    public ListProductOfRequest createProduct(
        @Argument CreateProductInput input
    ) {
        return service.createProduct(input);
    }

    @MutationMapping
    public List<ListProductOfRequest> createProductsBatch(
        @Argument Integer requestId,
        @Argument List<CreateProductInput> products
    ) {
        return service.createProductsBatch(requestId, products);
    }
}
