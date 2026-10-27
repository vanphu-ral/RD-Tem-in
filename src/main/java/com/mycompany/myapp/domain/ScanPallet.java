package com.mycompany.myapp.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A ScanPallet.
 */
@Entity
@Table(name = "scan_pallet")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScanPallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 30)
    @Column(name = "serial_pallet", length = 30)
    private String serialPallet;

    @Column(name = "business_source_id")
    private Long businessSourceId;

    @Size(max = 50)
    @Column(name = "business_type", length = 50)
    private String businessType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ScanPallet id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialPallet() {
        return this.serialPallet;
    }

    public ScanPallet serialPallet(String serialPallet) {
        this.setSerialPallet(serialPallet);
        return this;
    }

    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

    public Long getBusinessSourceId() {
        return this.businessSourceId;
    }

    public ScanPallet businessSourceId(Long businessSourceId) {
        this.setBusinessSourceId(businessSourceId);
        return this;
    }

    public void setBusinessSourceId(Long businessSourceId) {
        this.businessSourceId = businessSourceId;
    }

    public String getBusinessType() {
        return this.businessType;
    }

    public ScanPallet businessType(String businessType) {
        this.setBusinessType(businessType);
        return this;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScanPallet)) {
            return false;
        }
        return getId() != null && getId().equals(((ScanPallet) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScanPallet{" +
            "id=" + getId() +
            ", serialPallet='" + getSerialPallet() + "'" +
            ", businessSourceId=" + getBusinessSourceId() +
            ", businessType='" + getBusinessType() + "'" +
            "}";
    }
}
