package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A branch.
 */
@Entity
@Table(name = "branch")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class branch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 15)
    @Column(name = "workshop_code", length = 15)
    private String workshopCode;

    @NotNull
    @Size(max = 15)
    @Column(name = "branch_code", length = 15, nullable = false, unique = true)
    private String branchCode;

    @Size(max = 50)
    @Column(name = "branch_name", length = 50)
    private String branchName;

    @ManyToOne
    @JoinColumn(
        name = "workshop_code",
        referencedColumnName = "workshop_code",
        insertable = false,
        updatable = false
    )
    private Workshop workshop;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "branch")
    @JsonIgnoreProperties(value = { "branch" }, allowSetters = true)
    private Set<ProductionTeam> productionTeams = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public branch id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkshopCode() {
        return this.workshopCode;
    }

    public branch workshopCode(String workshopCode) {
        this.setWorkshopCode(workshopCode);
        return this;
    }

    public void setWorkshopCode(String workshopCode) {
        this.workshopCode = workshopCode;
    }

    public String getBranchCode() {
        return this.branchCode;
    }

    public branch branchCode(String branchCode) {
        this.setBranchCode(branchCode);
        return this;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return this.branchName;
    }

    public branch branchName(String branchName) {
        this.setBranchName(branchName);
        return this;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Workshop getWorkshop() {
        return this.workshop;
    }

    public branch workshop(Workshop workshop) {
        this.setWorkshop(workshop);
        return this;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }

    public Set<ProductionTeam> getProductionTeams() {
        return this.productionTeams;
    }

    public void setProductionTeams(Set<ProductionTeam> productionTeams) {
        if (this.productionTeams != null) {
            this.productionTeams.forEach(i -> i.setbranch(null));
        }
        if (productionTeams != null) {
            productionTeams.forEach(i -> i.setbranch(this));
        }
        this.productionTeams = productionTeams;
    }

    public branch productionTeams(Set<ProductionTeam> productionTeams) {
        this.setProductionTeams(productionTeams);
        return this;
    }

    public branch addProductionTeams(ProductionTeam productionTeam) {
        this.productionTeams.add(productionTeam);
        productionTeam.setbranch(this);
        return this;
    }

    public branch removeProductionTeams(ProductionTeam productionTeam) {
        this.productionTeams.remove(productionTeam);
        productionTeam.setbranch(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof branch)) {
            return false;
        }
        return getId() != null && getId().equals(((branch) o).getId());
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
        return "branch{" +
                "id=" + getId() +
                ", workshopCode='" + getWorkshopCode() + "'" +
                ", branchCode='" + getBranchCode() + "'" +
                ", branchName='" + getBranchName() + "'" +
                "}";
    }
}
