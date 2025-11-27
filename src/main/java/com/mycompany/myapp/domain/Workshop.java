package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Workshop.
 */
@Entity
@Table(name = "workshop")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Workshop implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 15)
    @Column(
        name = "workshop_code",
        length = 15,
        nullable = false,
        unique = true
    )
    private String workshopCode;

    @Size(max = 50)
    @Column(name = "work_shop_name", length = 50)
    private String workShopName;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workshop")
    @JsonIgnoreProperties(value = { "workshop" }, allowSetters = true)
    private Set<branch> branchs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Workshop id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkshopCode() {
        return this.workshopCode;
    }

    public Workshop workshopCode(String workshopCode) {
        this.setWorkshopCode(workshopCode);
        return this;
    }

    public void setWorkshopCode(String workshopCode) {
        this.workshopCode = workshopCode;
    }

    public String getWorkShopName() {
        return this.workShopName;
    }

    public Workshop workShopName(String workShopName) {
        this.setWorkShopName(workShopName);
        return this;
    }

    public void setWorkShopName(String workShopName) {
        this.workShopName = workShopName;
    }

    public String getDescription() {
        return this.description;
    }

    public Workshop description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<branch> getbranchs() {
        return this.branchs;
    }

    public void setbranchs(Set<branch> branchs) {
        if (this.branchs != null) {
            this.branchs.forEach(i -> i.setWorkshop(null));
        }
        if (branchs != null) {
            branchs.forEach(i -> i.setWorkshop(this));
        }
        this.branchs = branchs;
    }

    public Workshop branchs(Set<branch> branchs) {
        this.setbranchs(branchs);
        return this;
    }

    public Workshop addbranchs(branch branch) {
        this.branchs.add(branch);
        branch.setWorkshop(this);
        return this;
    }

    public Workshop removebranchs(branch branch) {
        this.branchs.remove(branch);
        branch.setWorkshop(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Workshop)) {
            return false;
        }
        return getId() != null && getId().equals(((Workshop) o).getId());
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
        return "Workshop{" +
                "id=" + getId() +
                ", workshopCode='" + getWorkshopCode() + "'" +
                ", workShopName='" + getWorkShopName() + "'" +
                ", description='" + getDescription() + "'" +
                "}";
    }
}
