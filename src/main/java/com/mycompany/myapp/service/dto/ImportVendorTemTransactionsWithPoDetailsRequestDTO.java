package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * Request DTO for creating ImportVendorTemTransactions with PoDetails.
 * This DTO is used when creating a new import transaction along with its
 * associated po_details in a single request.
 */
@Schema(
    description = "Request to create ImportVendorTemTransactions with PoDetails"
)
public class ImportVendorTemTransactionsWithPoDetailsRequestDTO
    implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private ImportVendorTemTransactionsDTO transaction;

    @NotNull
    private List<PoDetailDTO> poDetails;

    public ImportVendorTemTransactionsWithPoDetailsRequestDTO() {}

    public ImportVendorTemTransactionsWithPoDetailsRequestDTO(
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

    @Override
    public String toString() {
        return (
            "ImportVendorTemTransactionsWithPoDetailsRequestDTO{" +
            "transaction=" +
            transaction +
            ", poDetails=" +
            (poDetails != null ? poDetails.size() : 0) +
            '}'
        );
    }
}
