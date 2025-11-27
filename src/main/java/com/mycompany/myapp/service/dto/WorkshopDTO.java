package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Workshop} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkshopDTO implements Serializable {

    private Long id;

    private String workshopCode;

    private String workShopName;

    private String description;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkshopDTO)) {
            return false;
        }

        WorkshopDTO workshopDTO = (WorkshopDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, workshopDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkshopDTO{" +
                "id=" + getId() +
                ", workshopCode='" + getWorkshopCode() + "'" +
                ", workShopName='" + getWorkShopName() + "'" +
                ", description='" + getDescription() + "'" +
                "}";
    }
}
