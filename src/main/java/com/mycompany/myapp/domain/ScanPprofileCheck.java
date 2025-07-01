package com.mycompany.myapp.domain;

import javax.persistence.*;
import org.apache.commons.math3.analysis.function.Identity;

@Entity
@Table(name = "Scan_profileCheck")
public class ScanPprofileCheck {

    @Id
    @Column(name = "profile_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "check_name")
    private String checkName;

    @Column(name = "check_value")
    private String checkValue;

    @Column(name = "check_status")
    private String checkStatus;

    @Column(name = "position")
    private Integer position;

    @Column(name = "version_id")
    private Long versionId;

    @Column(name = "machine_id")
    private Integer machineId;

    public ScanPprofileCheck() {}

    public ScanPprofileCheck(
        Long profileId,
        Long productId,
        String checkName,
        String checkValue,
        String checkStatus,
        Integer position,
        Long versionId
    ) {
        this.profileId = profileId;
        this.productId = productId;
        this.checkName = checkName;
        this.checkValue = checkValue;
        this.checkStatus = checkStatus;
        this.position = position;
        this.versionId = versionId;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getCheckName() {
        return checkName;
    }

    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }

    public String getCheckValue() {
        return checkValue;
    }

    public void setCheckValue(String checkValue) {
        this.checkValue = checkValue;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }
}
