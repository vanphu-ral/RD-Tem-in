package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * DTO for PO Import response.
 * Used for both Case 1 (newly created records) and Case 2 (retrieved data with
 * subclasses).
 */
@Schema(description = "PO Import Response DTO")
public class PoImportResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private PoImportTemDTO poImportTem;

    private ImportVendorTemTransactionsDetailDTO vendorTransaction;

    private String caseType; // "CASE_1" or "CASE_2"

    public PoImportResponseDTO() {}

    public PoImportResponseDTO(
        PoImportTemDTO poImportTem,
        ImportVendorTemTransactionsDetailDTO vendorTransaction,
        String caseType
    ) {
        this.poImportTem = poImportTem;
        this.vendorTransaction = vendorTransaction;
        this.caseType = caseType;
    }

    // public PoImportResponseDTO(PoImportTemDTO poImportTem,
    // ImportVendorTemTransactionsDetailDTO vendorTransaction,
    // String caseType, String message) {
    // this.poImportTem = poImportTem;
    // this.vendorTransaction = vendorTransaction;
    // this.caseType = caseType;
    // }

    public PoImportTemDTO getPoImportTem() {
        return poImportTem;
    }

    public void setPoImportTem(PoImportTemDTO poImportTem) {
        this.poImportTem = poImportTem;
    }

    public ImportVendorTemTransactionsDetailDTO getVendorTransaction() {
        return vendorTransaction;
    }

    public void setVendorTransaction(
        ImportVendorTemTransactionsDetailDTO vendorTransaction
    ) {
        this.vendorTransaction = vendorTransaction;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    @Override
    public String toString() {
        return (
            "PoImportResponseDTO{" +
            "caseType='" +
            caseType +
            '\'' +
            ", poImportTem=" +
            poImportTem +
            ", vendorTransaction=" +
            vendorTransaction +
            '}'
        );
    }
}
