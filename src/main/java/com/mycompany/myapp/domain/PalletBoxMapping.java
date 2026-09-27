package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A PalletBoxMapping.
 */
@Entity
@Table(name = "pallet_box_mapping")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PalletBoxMapping implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Size(max = 30)
  @Column(name = "serial_pallet", length = 30)
  private String serialPallet;

   @Size(max = 50)
   @Column(name = "reel_id_box", length = 50)
   private String reelIdBox;

  @Column(name = "create_at")
  private Instant createAt;

  @Size(max = 50)
  @Column(name = "create_by", length = 50)
  private String createBy;

   @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "reel_id_box", referencedColumnName = "reel_id", insertable = false, updatable = false)
   @JsonIgnoreProperties(value = { "deliveryNotification", "sapPor1", "palletBoxMapping" }, allowSetters = true)
   private VendorLabelInfo vendorLabelInfo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "serial_pallet",
    referencedColumnName = "serial_pallet",
    insertable = false,
    updatable = false
  )
  private PalletMngt palletMngt;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public PalletBoxMapping id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSerialPallet() {
    return this.serialPallet;
  }

  public PalletBoxMapping serialPallet(String serialPallet) {
    this.setSerialPallet(serialPallet);
    return this;
  }

  public void setSerialPallet(String serialPallet) {
    this.serialPallet = serialPallet;
  }

  public String getReelIdBox() {
    return this.reelIdBox;
  }

  public PalletBoxMapping reelIdBox(String reelIdBox) {
    this.setReelIdBox(reelIdBox);
    return this;
  }

  public void setReelIdBox(String reelIdBox) {
    this.reelIdBox = reelIdBox;
  }

  public Instant getCreateAt() {
    return this.createAt;
  }

  public PalletBoxMapping createAt(Instant createAt) {
    this.setCreateAt(createAt);
    return this;
  }

  public void setCreateAt(Instant createAt) {
    this.createAt = createAt;
  }

  public String getCreateBy() {
    return this.createBy;
  }

  public PalletBoxMapping createBy(String createBy) {
    this.setCreateBy(createBy);
    return this;
  }

  public void setCreateBy(String createBy) {
    this.createBy = createBy;
  }

  public VendorLabelInfo getVendorLabelInfo() {
    return this.vendorLabelInfo;
  }

  public void setVendorLabelInfo(VendorLabelInfo vendorLabelInfo) {
    this.vendorLabelInfo = vendorLabelInfo;
  }

  public PalletBoxMapping vendorLabelInfo(VendorLabelInfo vendorLabelInfo) {
    this.setVendorLabelInfo(vendorLabelInfo);
    return this;
  }

  public PalletMngt getPalletMngt() {
    return this.palletMngt;
  }

  public void setPalletMngt(PalletMngt palletMngt) {
    this.palletMngt = palletMngt;
  }

  public PalletBoxMapping palletMngt(PalletMngt palletMngt) {
    this.setPalletMngt(palletMngt);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PalletBoxMapping)) {
      return false;
    }
    return getId() != null && getId().equals(((PalletBoxMapping) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "PalletBoxMapping{" +
            "id=" + getId() +
            ", serialPallet='" + getSerialPallet() + "'" +
            ", reelIdBox='" + getReelIdBox() + "'" +
            ", createAt='" + getCreateAt() + "'" +
            ", createBy='" + getCreateBy() + "'" +
            "}";
    }
}
