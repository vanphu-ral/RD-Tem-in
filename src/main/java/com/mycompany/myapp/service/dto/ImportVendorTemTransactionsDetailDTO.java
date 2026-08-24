package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

/**
 * DTO for detailed view of ImportVendorTemTransactions with all related data.
 * Contains the main transaction info along with poDetails and their
 * vendorTemDetails.
 */
@Schema(description = "Detailed ImportVendorTemTransactions with related data")
public class ImportVendorTemTransactionsDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ImportVendorTemTransactionsDTO transaction;

    private List<PoDetailDTO> poDetails;

    public ImportVendorTemTransactionsDetailDTO() {}

    public ImportVendorTemTransactionsDetailDTO(
        ImportVendorTemTransactionsDTO transaction,
        List<PoDetailDTO> poDetails
    ) {
        this.transaction = transaction;
        this.poDetails = poDetails;
    }

    public ImportVendorTemTransactionsDTO getTransaction() {
        return transaction;
    }

    public void setTransaction(ImportVendorTemTransactionsDTO transaction) {
        this.transaction = transaction;
    }

    public List<PoDetailDTO> getPoDetails() {
        return poDetails;
    }

    public void setPoDetails(List<PoDetailDTO> poDetails) {
        this.poDetails = poDetails;
    }
}
