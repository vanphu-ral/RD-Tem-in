package com.mycompany.myapp.service.dto;

import java.io.Serializable;

/**
 * Aggregation result: the total {@code initial_quantity} per {@code sap_code}
 * for records whose {@code user_data_5} matches a given value.
 */
public class VendorLabelInfoUserData5SumDTO implements Serializable {

    private String sapCode;

    private Long totalInitialQuantity;

    public VendorLabelInfoUserData5SumDTO() {}

    public VendorLabelInfoUserData5SumDTO(
        String sapCode,
        Long totalInitialQuantity
    ) {
        this.sapCode = sapCode;
        this.totalInitialQuantity = totalInitialQuantity;
    }

    public String getSapCode() {
        return sapCode;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public Long getTotalInitialQuantity() {
        return totalInitialQuantity;
    }

    public void setTotalInitialQuantity(Long totalInitialQuantity) {
        this.totalInitialQuantity = totalInitialQuantity;
    }
}
