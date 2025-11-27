package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the workshop hierarchy.
 */
public class WorkshopHierarchyDTO implements Serializable {

    private Long id;

    private String workshopCode;

    private String workShopName;

    private String description;

    private List<branchHierarchyDTO> branchs;

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

    public String getWorkShopName() {
        return workShopName;
    }

    public void setWorkShopName(String workShopName) {
        this.workShopName = workShopName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<branchHierarchyDTO> getbranchs() {
        return branchs;
    }

    public void setbranchs(List<branchHierarchyDTO> branchs) {
        this.branchs = branchs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkshopHierarchyDTO)) {
            return false;
        }

        WorkshopHierarchyDTO workshopHierarchyDTO = (WorkshopHierarchyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, workshopHierarchyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkshopHierarchyDTO{" +
                "id=" + getId() +
                ", workshopCode='" + getWorkshopCode() + "'" +
                ", workShopName='" + getWorkShopName() + "'" +
                ", description='" + getDescription() + "'" +
                ", branchs=" + getbranchs() +
                "}";
    }
}
