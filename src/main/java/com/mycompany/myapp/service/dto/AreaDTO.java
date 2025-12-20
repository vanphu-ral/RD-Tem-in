package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for the Area entity.
 */
public class AreaDTO implements Serializable {

    private Long id;

    private String code;

    private String name;

    private String thuKho;

    private String description;

    private String address;

    private Boolean isActive;

    private String updatedBy;

    private Instant updatedDate;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThuKho() {
        return thuKho;
    }

    public void setThuKho(String thuKho) {
        this.thuKho = thuKho;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AreaDTO)) {
            return false;
        }
        return id != null && id.equals(((AreaDTO) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "AreaDTO{" +
            "id=" +
            id +
            ", code='" +
            code +
            "'" +
            ", name='" +
            name +
            "'" +
            ", thuKho='" +
            thuKho +
            "'" +
            ", description='" +
            description +
            "'" +
            ", address='" +
            address +
            "'" +
            ", isActive='" +
            isActive +
            "'" +
            ", updatedBy='" +
            updatedBy +
            "'" +
            ", updatedDate='" +
            updatedDate +
            "'" +
            "}"
        );
    }
}
