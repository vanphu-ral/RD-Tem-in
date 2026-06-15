package com.mycompany.renderQr.domain;

import java.util.List;

public class RequestCreateTemPage {

    private final List<ListRequestCreateTem> content;
    private final long totalElements;
    private final int page;
    private final int size;
    private final int totalPages;

    public RequestCreateTemPage(
        List<ListRequestCreateTem> content,
        long totalElements,
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

    public List<ListRequestCreateTem> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
