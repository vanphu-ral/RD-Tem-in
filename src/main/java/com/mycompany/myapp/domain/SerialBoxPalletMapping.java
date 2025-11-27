package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A SerialBoxPalletMapping.
 */
@Entity
@Table(name = "serial_box_pallet_mapping")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SerialBoxPalletMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 50)
    @Column(name = "serial_box", length = 50)
    private String serialBox;

    @Size(max = 50)
    @Column(name = "serial_pallet", length = 50)
    private String serialPallet;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Size(max = 50)
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_lenh_san_xuat_id")
    @JsonIgnoreProperties(
        value = { "genTemConfigs", "serialMappings", "details" },
        allowSetters = true
    )
    private WarehouseNoteInfo maLenhSanXuat;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SerialBoxPalletMapping id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialBox() {
        return this.serialBox;
    }

    public SerialBoxPalletMapping serialBox(String serialBox) {
        this.setSerialBox(serialBox);
        return this;
    }

    public void setSerialBox(String serialBox) {
        this.serialBox = serialBox;
    }

    public String getSerialPallet() {
        return this.serialPallet;
    }

    public SerialBoxPalletMapping serialPallet(String serialPallet) {
        this.setSerialPallet(serialPallet);
        return this;
    }

    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public SerialBoxPalletMapping updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public SerialBoxPalletMapping updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public WarehouseNoteInfo getMaLenhSanXuat() {
        return this.maLenhSanXuat;
    }

    public void setMaLenhSanXuat(WarehouseNoteInfo warehouseStampInfo) {
        this.maLenhSanXuat = warehouseStampInfo;
    }

    public SerialBoxPalletMapping maLenhSanXuat(
        WarehouseNoteInfo warehouseStampInfo
    ) {
        this.setMaLenhSanXuat(warehouseStampInfo);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SerialBoxPalletMapping)) {
            return false;
        }
        return (
            getId() != null &&
            getId().equals(((SerialBoxPalletMapping) o).getId())
        );
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
        return "SerialBoxPalletMapping{" +
                "id=" + getId() +
                ", serialBox='" + getSerialBox() + "'" +
                ", serialPallet='" + getSerialPallet() + "'" +
                ", updatedAt='" + getUpdatedAt() + "'" +
                ", updatedBy='" + getUpdatedBy() + "'" +
                "}";
    }
}
