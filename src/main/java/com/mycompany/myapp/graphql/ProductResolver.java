package com.mycompany.myapp.graphql;

import com.mycompany.myapp.graphql.dto.CreateProductInput;
import com.mycompany.myapp.graphql.dto.CreateRequestWithProductsInput;
import com.mycompany.myapp.graphql.dto.CreateRequestWithProductsResponse;
import com.mycompany.myapp.service.ListProductOfRequestService;
import com.mycompany.renderQr.domain.ListProductOfRequest;
import com.mycompany.renderQr.domain.ListProductOfRequestResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Controller
public class ProductResolver {

    private static final Logger log = LoggerFactory.getLogger(
        ProductResolver.class
    );

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

    @MutationMapping(name = "createRequestAndProducts")
    public CreateRequestWithProductsResponse createRequestAndProducts(
        @Argument("input") CreateRequestWithProductsInput input
    ) {
        log.info("ProductResolver: Received createRequestAndProducts request");
        log.info(
            "Input: vendor={}, userData5={}, createdBy={}, products count={}",
            input.getVendor(),
            input.getVendorName(),
            input.getUserData5(),
            input.getCreatedBy(),
            input.getProducts() != null ? input.getProducts().size() : 0
        );

        // Validate input
        if (input == null) {
            log.error("Input is null");
            throw new IllegalArgumentException("Input cannot be null");
        }

        if (input.getProducts() == null || input.getProducts().isEmpty()) {
            log.error("Products list is null or empty");
            throw new IllegalArgumentException(
                "Products list cannot be null or empty"
            );
        }

        CreateRequestWithProductsResponse response =
            service.createRequestAndProducts(input);

        if (response == null) {
            log.error("Service returned null response");
            throw new IllegalStateException("Service returned null response");
        }

        if (response.getRequestId() == null) {
            log.error("Response requestId is null");
            throw new IllegalStateException(
                "Response requestId cannot be null"
            );
        }

        if (response.getProducts() == null) {
            log.error("Response products is null");
            throw new IllegalStateException("Response products cannot be null");
        }

        log.info(
            "ProductResolver: Successfully created request with ID: {}",
            response.getRequestId()
        );
        log.info(
            "ProductResolver: Created {} products",
            response.getProducts().size()
        );
        log.info("Input vendorName: {}", input.getVendorName());
        if (input.getProducts() != null && !input.getProducts().isEmpty()) {
            log.info(
                "First product vendorName: {}",
                input.getProducts().get(0).getVendorName()
            );
        }
        return response;
    }

    @MutationMapping
    public List<ListProductOfRequest> updateRequestProducts(
        @Argument Integer requestId,
        @Argument List<CreateProductInput> products
    ) {
        return service.updateRequestProducts(requestId, products);
    }
}
