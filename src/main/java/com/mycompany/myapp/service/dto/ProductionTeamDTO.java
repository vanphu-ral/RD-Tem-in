package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.ProductionTeam} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductionTeamDTO implements Serializable {

    private Long id;

    private String branchCode;

    private String productionTeamCode;

    private String productionTeamName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getProductionTeamCode() {
        return productionTeamCode;
    }

    public void setProductionTeamCode(String productionTeamCode) {
        this.productionTeamCode = productionTeamCode;
    }

    public String getProductionTeamName() {
        return productionTeamName;
    }

    public void setProductionTeamName(String productionTeamName) {
        this.productionTeamName = productionTeamName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductionTeamDTO)) {
            return false;
        }

        ProductionTeamDTO productionTeamDTO = (ProductionTeamDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productionTeamDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductionTeamDTO{" +
                "id=" + getId() +
                ", branchCode='" + getBranchCode() + "'" +
                ", productionTeamCode='" + getProductionTeamCode() + "'" +
                ", productionTeamName='" + getProductionTeamName() + "'" +
                "}";
    }
}
