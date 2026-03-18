package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.PoDetail} entity.
 */
@Schema(description = "Entity PoDetail")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PoDetailDTO implements Serializable {

    private Long id;

    @Size(max = 20)
    private String sapCode;

    @Size(max = 255)
    private String sapName;

    private Integer quantityContainer;

    private Integer totalQuantity;

    @Size(max = 50)
    private String partNumber;

    private ImportVendorTemTransactionsDTO importVendorTemTransactions;

    private Long importVendorTemTransactionsId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSapCode() {
        return sapCode;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public String getSapName() {
        return sapName;
    }

    public void setSapName(String sapName) {
        this.sapName = sapName;
    }

    public Integer getQuantityContainer() {
        return quantityContainer;
    }

    public void setQuantityContainer(Integer quantityContainer) {
        this.quantityContainer = quantityContainer;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public ImportVendorTemTransactionsDTO getImportVendorTemTransactions() {
        return importVendorTemTransactions;
    }

    public void setImportVendorTemTransactions(
        ImportVendorTemTransactionsDTO importVendorTemTransactions
    ) {
        this.importVendorTemTransactions = importVendorTemTransactions;
    }

    public Long getImportVendorTemTransactionsId() {
        return importVendorTemTransactionsId;
    }

    public void setImportVendorTemTransactionsId(
        Long importVendorTemTransactionsId
    ) {
        this.importVendorTemTransactionsId = importVendorTemTransactionsId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PoDetailDTO)) {
            return false;
        }

        PoDetailDTO poDetailDTO = (PoDetailDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, poDetailDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PoDetailDTO{" +
                "id=" + getId() +
                ", sapCode='" + getSapCode() + "'" +
                ", sapName='" + getSapName() + "'" +
                ", quantityContainer=" + getQuantityContainer() +
                ", totalQuantity=" + getTotalQuantity() +
                ", partNumber='" + getPartNumber() + "'" +
                ", importVendorTemTransactions=" + getImportVendorTemTransactions() +
                "}";
    }
}
