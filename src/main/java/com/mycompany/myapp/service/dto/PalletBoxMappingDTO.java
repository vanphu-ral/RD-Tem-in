package com.mycompany.myapp.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.PalletBoxMapping} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PalletBoxMappingDTO implements Serializable {

  private Long id;

  @Size(max = 30)
  private String serialPallet;

  @Size(max = 50)
  private String reelIdBox;

  private Instant createAt;

  @Size(max = 50)
  private String createBy;

  private PalletMngtDTO palletMngt;

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

  public String getReelIdBox() {
    return reelIdBox;
  }

  public void setReelIdBox(String reelIdBox) {
    this.reelIdBox = reelIdBox;
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

  public PalletMngtDTO getPalletMngt() {
    return palletMngt;
  }

  public void setPalletMngt(PalletMngtDTO palletMngt) {
    this.palletMngt = palletMngt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PalletBoxMappingDTO)) {
      return false;
    }

    PalletBoxMappingDTO palletBoxMappingDTO = (PalletBoxMappingDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, palletBoxMappingDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "PalletBoxMappingDTO{" +
            "id=" + getId() +
            ", serialPallet='" + getSerialPallet() + "'" +
            ", reelIdBox='" + getReelIdBox() + "'" +
            ", createAt='" + getCreateAt() + "'" +
            ", createBy='" + getCreateBy() + "'" +
            ", palletMngt=" + getPalletMngt() +
            "}";
    }
}
