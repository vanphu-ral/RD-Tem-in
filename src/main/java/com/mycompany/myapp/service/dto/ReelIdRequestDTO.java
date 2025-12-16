package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;

/**
 * A DTO for ReelId request in WarehouseNoteInfoApproval.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReelIdRequestDTO implements Serializable {

    private Long id;

    private Instant createAt;

    @Size(max = 50)
    @JsonProperty("create_by")
    private String createBy;

    public ReelIdRequestDTO() {}

    public ReelIdRequestDTO(Long id, Instant createAt, String createBy) {
        this.id = id;
        this.createAt = createAt;
        this.createBy = createBy;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreateAt() {
        return this.createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Override
    public String toString() {
        return (
            "ReelIdRequestDTO{" +
            "id='" +
            getId() +
            "'" +
            ", createAt='" +
            getCreateAt() +
            "'" +
            ", createBy='" +
            getCreateBy() +
            "'" +
            "}"
        );
    }
}
