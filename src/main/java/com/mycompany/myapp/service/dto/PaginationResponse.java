package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PaginationResponse<T> {

    @JsonProperty("datas")
    private List<T> data;

    @JsonProperty("pagination")
    private Pagination pagination;

    public PaginationResponse() {}

    public PaginationResponse(List<T> data, Pagination pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    // Getters and Setters
    @JsonProperty("datas")
    public List<T> getDatas() {
        return data;
    }

    public void setDatas(List<T> data) {
        this.data = data;
    }

    @JsonProperty("pagination")
    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public static class Pagination {

        @JsonProperty("totalItems")
        private long totalItems;

        @JsonProperty("totalPages")
        private int totalPages;

        @JsonProperty("currentPage")
        private int currentPage;

        @JsonProperty("pageSize")
        private int pageSize;

        @JsonProperty("hasNext")
        private boolean hasNext;

        @JsonProperty("hasPrevious")
        private boolean hasPrevious;

        public Pagination() {}

        public Pagination(
            long totalItems,
            int totalPages,
            int currentPage,
            int pageSize,
            boolean hasNext,
            boolean hasPrevious
        ) {
            this.totalItems = totalItems;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
        }

        // Getters and Setters
        public long getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(long totalItems) {
            this.totalItems = totalItems;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public boolean isHasPrevious() {
            return hasPrevious;
        }

        public void setHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }
    }
}
