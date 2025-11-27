package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.branch} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class branchDTO implements Serializable {

    private Long id;

    private String workshopCode;

    private String branchCode;

    private String branchName;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof branchDTO)) {
            return false;
        }

        branchDTO branchDTO = (branchDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, branchDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "branchDTO{" +
                "id=" + getId() +
                ", workshopCode='" + getWorkshopCode() + "'" +
                ", branchCode='" + getBranchCode() + "'" +
                ", branchName='" + getBranchName() + "'" +
                "}";
    }
}
