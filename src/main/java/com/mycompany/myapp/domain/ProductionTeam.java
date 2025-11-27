package com.mycompany.myapp.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A ProductionTeam.
 */
@Entity
@Table(name = "production_team")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductionTeam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 10)
    @Column(name = "branch_code", length = 10)
    private String branchCode;

    @NotNull
    @Size(max = 15)
    @Column(
        name = "production_team_code",
        length = 15,
        nullable = false,
        unique = true
    )
    private String productionTeamCode;

    @Size(max = 50)
    @Column(name = "production_team_name", length = 50)
    private String productionTeamName;

    @ManyToOne
    @JoinColumn(
        name = "branch_code",
        referencedColumnName = "branch_code",
        insertable = false,
        updatable = false
    )
    private branch branch;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductionTeam id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBranchCode() {
        return this.branchCode;
    }

    public ProductionTeam branchCode(String branchCode) {
        this.setBranchCode(branchCode);
        return this;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getProductionTeamCode() {
        return this.productionTeamCode;
    }

    public ProductionTeam productionTeamCode(String productionTeamCode) {
        this.setProductionTeamCode(productionTeamCode);
        return this;
    }

    public void setProductionTeamCode(String productionTeamCode) {
        this.productionTeamCode = productionTeamCode;
    }

    public String getProductionTeamName() {
        return this.productionTeamName;
    }

    public ProductionTeam productionTeamName(String productionTeamName) {
        this.setProductionTeamName(productionTeamName);
        return this;
    }

    public void setProductionTeamName(String productionTeamName) {
        this.productionTeamName = productionTeamName;
    }

    public branch getbranch() {
        return this.branch;
    }

    public ProductionTeam branch(branch branch) {
        this.setbranch(branch);
        return this;
    }

    public void setbranch(branch branch) {
        this.branch = branch;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductionTeam)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductionTeam) o).getId());
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductionTeam{" +
                "id=" + getId() +
                ", branchCode='" + getBranchCode() + "'" +
                ", productionTeamCode='" + getProductionTeamCode() + "'" +
                ", productionTeamName='" + getProductionTeamName() + "'" +
                "}";
    }
}
