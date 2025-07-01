package com.mycompany.myapp.domain;

import javax.persistence.*;
import jdk.jfr.Enabled;

@Entity
@Table(name = "nhom_thiet_bi")
public class NhomThietBi {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loai_thiet_bi")
    private String loaiThietBi;

    @Column(name = "ma_thiet_bi")
    private String maThietBi;

    @Column(name = "day_chuyen")
    private String dayChuyen;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "trang_thai")
    private String trangThai;

    public NhomThietBi(Long id, String loaiThietBi, String maThietBi, String dayChuyen) {
        this.id = id;
        this.loaiThietBi = loaiThietBi;
        this.maThietBi = maThietBi;
        this.dayChuyen = dayChuyen;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getDayChuyen() {
        return dayChuyen;
    }

    public void setDayChuyen(String dayChuyen) {
        this.dayChuyen = dayChuyen;
    }

    public NhomThietBi() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoaiThietBi() {
        return loaiThietBi;
    }

    public void setLoaiThietBi(String loaiThietBi) {
        this.loaiThietBi = loaiThietBi;
    }

    public String getMaThietBi() {
        return maThietBi;
    }

    public void setMaThietBi(String maThietBi) {
        this.maThietBi = maThietBi;
    }
}
