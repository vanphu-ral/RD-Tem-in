package com.mycompany.myapp.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.PalletMngt} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PalletMngtDTO implements Serializable {

  private Long id;

  @Size(max = 30)
  private String serialPallet;

  @Size(max = 50)
  private String locationName;

  private Integer numberOfBox;

  private Integer totalQuantity;

  @Size(max = 50)
  private String status;

  @Size(max = 255)
  private String note;

  private Instant createAt;

  @Size(max = 50)
  private String createBy;

  private Instant updatedAt;

  @Size(max = 50)
  private String updatedBy;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSerialPallet() {
    return serialPallet;
  }

  public void setSerialPallet(String serialPallet) {
    this.serialPallet = serialPallet;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
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

  public Instant getCreateAt() {
    return createAt;
  }

  public void setCreateAt(Instant createAt) {
    this.createAt = createAt;
  }

  public String getCreateBy() {
    return createBy;
  }

  public void setCreateBy(String createBy) {
    this.createBy = createBy;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PalletMngtDTO)) {
      return false;
    }

    PalletMngtDTO palletMngtDTO = (PalletMngtDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, palletMngtDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "PalletMngtDTO{" +
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
