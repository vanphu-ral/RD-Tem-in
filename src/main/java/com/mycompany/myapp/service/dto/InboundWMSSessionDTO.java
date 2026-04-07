package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.InboundWMSSession} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InboundWMSSessionDTO implements Serializable {

    private Long id;

    @Size(max = 50)
    private String status;

    @Size(max = 255)
    private String note;

    @Size(max = 15)
    private String createdBy;

    private ZonedDateTime createdAt;

    private ZonedDateTime wmsSentAt;

    private Integer numberOfPallets;

    private Integer numberOfBox;

    private Integer totalQuantity;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<InboundWMSPalletDTO> inboundWMSPallets = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getWmsSentAt() {
        return wmsSentAt;
    }

    public void setWmsSentAt(ZonedDateTime wmsSentAt) {
        this.wmsSentAt = wmsSentAt;
    }

    public Set<InboundWMSPalletDTO> getInboundWMSPallets() {
        return inboundWMSPallets;
    }

    public void setInboundWMSPallets(
        Set<InboundWMSPalletDTO> inboundWMSPallets
    ) {
        this.inboundWMSPallets = inboundWMSPallets;
    }

    public Integer getNumberOfPallets() {
        return numberOfPallets;
    }

    public void setNumberOfPallets(Integer numberOfPallets) {
        this.numberOfPallets = numberOfPallets;
    }

    public Integer getNumberOfBox() {
        return numberOfBox;
    }

    public void setNumberOfBox(Integer numberOfBox) {
        this.numberOfBox = numberOfBox;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InboundWMSSessionDTO)) {
            return false;
        }

        InboundWMSSessionDTO inboundWMSSessionDTO = (InboundWMSSessionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, inboundWMSSessionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InboundWMSSessionDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", numberOfPallets=" + getNumberOfPallets() +
            ", numberOfBox=" + getNumberOfBox() +
            ", totalQuantity=" + getTotalQuantity() +
            ", inboundWMSPallets=" + getInboundWMSPallets() +
            "}";
    }
}
