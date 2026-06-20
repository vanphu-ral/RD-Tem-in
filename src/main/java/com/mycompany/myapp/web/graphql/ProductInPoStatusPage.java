package com.mycompany.myapp.web.graphql;

import com.mycompany.myapp.service.dto.ProductInPoStatusDTO;
import java.util.List;

public class ProductInPoStatusPage {

    private List<ProductInPoStatusDTO> content;
    private int totalElements;
    private int page;
    private int size;
    private int totalPages;

    public ProductInPoStatusPage() {}

    public ProductInPoStatusPage(
        List<ProductInPoStatusDTO> content,
        int totalElements,
        int page,
        int size,
        int totalPages
    ) {
        this.content = content;
        this.totalElements = totalElements;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
    }

    public List<ProductInPoStatusDTO> getContent() {
        return content;
    }

    public void setContent(List<ProductInPoStatusDTO> content) {
        this.content = content;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
