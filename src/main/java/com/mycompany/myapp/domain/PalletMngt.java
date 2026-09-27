package com.mycompany.myapp.domain;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A PalletMngt.
 */
@Entity
@Table(name = "pallet_mngt")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PalletMngt implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Size(max = 30)
  @Column(name = "serial_pallet", length = 30)
  private String serialPallet;

  @Size(max = 50)
  @Column(name = "location_name", length = 50)
  private String locationName;

  @Column(name = "number_of_box")
  private Integer numberOfBox;

  @Column(name = "total_quantity")
  private Integer totalQuantity;

  @Size(max = 50)
  @Column(name = "status", length = 50)
  private String status;

  @Size(max = 255)
  @Column(name = "note", length = 255)
  private String note;

  @Column(name = "create_at")
  private Instant createAt;

  @Size(max = 50)
  @Column(name = "create_by", length = 50)
  private String createBy;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Size(max = 50)
  @Column(name = "updated_by", length = 50)
  private String updatedBy;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public PalletMngt id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSerialPallet() {
    return this.serialPallet;
  }

  public PalletMngt serialPallet(String serialPallet) {
    this.setSerialPallet(serialPallet);
    return this;
  }

  public void setSerialPallet(String serialPallet) {
    this.serialPallet = serialPallet;
  }

  public String getLocationName() {
    return this.locationName;
  }

  public PalletMngt locationName(String locationName) {
    this.setLocationName(locationName);
    return this;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public Integer getNumberOfBox() {
    return this.numberOfBox;
  }

  public PalletMngt numberOfBox(Integer numberOfBox) {
    this.setNumberOfBox(numberOfBox);
    return this;
  }

  public void setNumberOfBox(Integer numberOfBox) {
    this.numberOfBox = numberOfBox;
  }

  public Integer getTotalQuantity() {
    return this.totalQuantity;
  }

  public PalletMngt totalQuantity(Integer totalQuantity) {
    this.setTotalQuantity(totalQuantity);
    return this;
  }

  public void setTotalQuantity(Integer totalQuantity) {
    this.totalQuantity = totalQuantity;
  }

  public String getStatus() {
    return this.status;
  }

  public PalletMngt status(String status) {
    this.setStatus(status);
    return this;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getNote() {
    return this.note;
  }

  public PalletMngt note(String note) {
    this.setNote(note);
    return this;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public Instant getCreateAt() {
    return this.createAt;
  }

  public PalletMngt createAt(Instant createAt) {
    this.setCreateAt(createAt);
    return this;
  }

  public void setCreateAt(Instant createAt) {
    this.createAt = createAt;
  }

  public String getCreateBy() {
    return this.createBy;
  }

  public PalletMngt createBy(String createBy) {
    this.setCreateBy(createBy);
    return this;
  }

  public void setCreateBy(String createBy) {
    this.createBy = createBy;
  }

  public Instant getUpdatedAt() {
    return this.updatedAt;
  }

  public PalletMngt updatedAt(Instant updatedAt) {
    this.setUpdatedAt(updatedAt);
    return this;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getUpdatedBy() {
    return this.updatedBy;
  }

  public PalletMngt updatedBy(String updatedBy) {
    this.setUpdatedBy(updatedBy);
    return this;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PalletMngt)) {
      return false;
    }
    return getId() != null && getId().equals(((PalletMngt) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "PalletMngt{" +
            "id=" + getId() +
            ", serialPallet='" + getSerialPallet() + "'" +
            ", locationName='" + getLocationName() + "'" +
            ", numberOfBox=" + getNumberOfBox() +
            ", totalQuantity=" + getTotalQuantity() +
            ", status='" + getStatus() + "'" +
            ", note='" + getNote() + "'" +
            ", createAt='" + getCreateAt() + "'" +
            ", createBy='" + getCreateBy() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            "}";
    }
}
