package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the branch hierarchy.
 */
public class branchHierarchyDTO implements Serializable {

    private Long id;

    private String workshopCode;

    private String branchCode;

    private String branchName;

    private List<ProductionTeamDTO> productionTeams;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkshopCode() {
        return workshopCode;
    }

    public void setWorkshopCode(String workshopCode) {
        this.workshopCode = workshopCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public List<ProductionTeamDTO> getProductionTeams() {
        return productionTeams;
    }

    public void setProductionTeams(List<ProductionTeamDTO> productionTeams) {
        this.productionTeams = productionTeams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof branchHierarchyDTO)) {
            return false;
        }

        branchHierarchyDTO branchHierarchyDTO = (branchHierarchyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, branchHierarchyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "branchHierarchyDTO{" +
                "id=" + getId() +
                ", workshopCode='" + getWorkshopCode() + "'" +
                ", branchCode='" + getBranchCode() + "'" +
                ", branchName='" + getBranchName() + "'" +
                ", productionTeams=" + getProductionTeams() +
                "}";
    }
}
