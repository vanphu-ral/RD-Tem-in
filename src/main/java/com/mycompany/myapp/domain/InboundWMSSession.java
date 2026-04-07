package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.validation.constraints.*;

/**
 * A InboundWMSSession.
 */
@Entity
@Table(name = "inbound_wms_session")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InboundWMSSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @Size(max = 255)
    @Column(name = "note", length = 255)
    private String note;

    @Size(max = 15)
    @Column(name = "created_by", length = 15)
    private String createdBy;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "wms_sent_at")
    private ZonedDateTime wmsSentAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "inboundWMSSession")
    @JsonIgnoreProperties(value = { "inboundWMSSession" }, allowSetters = true)
    private Set<InboundWMSPallet> inboundWMSPallets = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InboundWMSSession id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return this.status;
    }

    public InboundWMSSession status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public InboundWMSSession note(String note) {
        this.setNote(note);
        return this;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public InboundWMSSession createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public InboundWMSSession createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getWmsSentAt() {
        return this.wmsSentAt;
    }

    public void setWmsSentAt(ZonedDateTime wmsSentAt) {
        this.wmsSentAt = wmsSentAt;
    }

    public InboundWMSSession wmsSentAt(ZonedDateTime wmsSentAt) {
        this.setWmsSentAt(wmsSentAt);
        return this;
    }

    public Set<InboundWMSPallet> getInboundWMSPallets() {
        return this.inboundWMSPallets;
    }

    public void setInboundWMSPallets(Set<InboundWMSPallet> inboundWMSPallets) {
        if (this.inboundWMSPallets != null) {
            this.inboundWMSPallets.forEach(i -> i.setInboundWMSSession(null));
        }
        if (inboundWMSPallets != null) {
            inboundWMSPallets.forEach(i -> i.setInboundWMSSession(this));
        }
        this.inboundWMSPallets = inboundWMSPallets;
    }

    public InboundWMSSession inboundWMSPallets(
        Set<InboundWMSPallet> inboundWMSPallets
    ) {
        this.setInboundWMSPallets(inboundWMSPallets);
        return this;
    }

    public InboundWMSSession addInboundWMSPallet(
        InboundWMSPallet inboundWMSPallet
    ) {
        this.inboundWMSPallets.add(inboundWMSPallet);
        inboundWMSPallet.setInboundWMSSession(this);
        return this;
    }

    public InboundWMSSession removeInboundWMSPallet(
        InboundWMSPallet inboundWMSPallet
    ) {
        this.inboundWMSPallets.remove(inboundWMSPallet);
        inboundWMSPallet.setInboundWMSSession(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InboundWMSSession)) {
            return false;
        }
        return (
            getId() != null && getId().equals(((InboundWMSSession) o).getId())
        );
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InboundWMSSession{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", note= '" + getNote() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", wmsSentAt='" + getWmsSentAt() + "'" +
            "}";
    }
}
