package com.mycompany.myapp.service.dto;

import com.mycompany.renderQr.domain.ListProductOfRequest;
import java.util.List;

public class CreateRequestWithProductsResponse {

    private Integer requestId;
    private List<ListProductOfRequest> products;
    private String message;

    public CreateRequestWithProductsResponse() {}

    public CreateRequestWithProductsResponse(
        Integer requestId,
        List<ListProductOfRequest> products,
        String message
    ) {
        this.requestId = requestId;
        this.products = products;
        this.message = message;
    }

    // Getters and Setters
    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public List<ListProductOfRequest> getProducts() {
        return products;
    }

    public void setProducts(List<ListProductOfRequest> products) {
        this.products = products;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
