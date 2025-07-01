package com.mycompany.myapp.domain;

import java.time.ZonedDateTime;
import javax.persistence.*;

@Entity
@Table(name = "scan_product_versions")
public class scanProductVersions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version_id")
    private Long versionId;

    @Column(name = "version")
    private String version;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "create_at")
    private String create;

    @Column(name = "update_at")
    private String updateAt;

    @Column(name = "username")
    private String username;

    @Column(name = "version_status")
    private Integer versionStatus;

    @Column(name = "group_id")
    private Integer groupId;

    public scanProductVersions() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getVersionStatus() {
        return versionStatus;
    }

    public void setVersionSratus(Integer versionStatus) {
        this.versionStatus = versionStatus;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getCreate() {
        return create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }
}
