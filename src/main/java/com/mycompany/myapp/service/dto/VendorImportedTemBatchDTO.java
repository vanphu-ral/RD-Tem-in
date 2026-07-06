package com.mycompany.myapp.service.dto;

import java.util.List;

public class VendorImportedTemBatchDTO {

    private Long productId;
    private List<VendorImportedReelDTO> reels;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public List<VendorImportedReelDTO> getReels() {
        return reels;
    }

    public void setReels(List<VendorImportedReelDTO> reels) {
        this.reels = reels;
    }
}
