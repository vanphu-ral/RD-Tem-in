package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.AttributesType} entity.
 */
@Schema(description = "Entity AttributesType")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AttributesTypeDTO implements Serializable {

    private Long id;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(o instanceof AttributesTypeDTO)) {
            return false;
        }

        AttributesTypeDTO attributesTypeDTO = (AttributesTypeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, attributesTypeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AttributesTypeDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
