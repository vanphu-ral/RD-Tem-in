package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

/**
 * DTO for pallet print data sent from frontend to backend for PDF generation.
 */
public class PrintPalletDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    private String khachHang;

    @NotNull
    private String serialPallet;

    @NotNull
    private String tenSanPham;

    private String poNumber;

    private String itemNoSku;

    private String nganh;

    private String led2;

    private String soQdsx;

    private String to;

    private String lpl2;

    private String ngaySanXuat;

    private String dateCode;

    private Integer soLuongCaiDatPallet;

    private Integer thuTuGiaPallet;

    private String soLuongBaoNgoaiThungGiaPallet;

    private Integer slThung;

    private String note;

    private String nguoiKiemTra;

    private String ketQuaKiemTra;

    @NotNull
    private String productCode;

    @NotNull
    private String serialBox;

    private Integer qty;

    private String lot;

    private String date;

    private Boolean printStatus;

    private String productType;

    private String version;

    private String maSAP;

    private String woId;

    private String erpWo;

    private String maLenhSanXuat;

    private Integer totalProductsOnPallet;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(String khachHang) {
        this.khachHang = khachHang;
    }

    public String getSerialPallet() {
        return serialPallet;
    }

    public void setSerialPallet(String serialPallet) {
        this.serialPallet = serialPallet;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getItemNoSku() {
        return itemNoSku;
    }

    public void setItemNoSku(String itemNoSku) {
        this.itemNoSku = itemNoSku;
    }

    public String getNganh() {
        return nganh;
    }

    public void setNganh(String nganh) {
        this.nganh = nganh;
    }

    public String getLed2() {
        return led2;
    }

    public void setLed2(String led2) {
        this.led2 = led2;
    }

    public String getSoQdsx() {
        return soQdsx;
    }

    public void setSoQdsx(String soQdsx) {
        this.soQdsx = soQdsx;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getLpl2() {
        return lpl2;
    }

    public void setLpl2(String lpl2) {
        this.lpl2 = lpl2;
    }

    public String getNgaySanXuat() {
        return ngaySanXuat;
    }

    public void setNgaySanXuat(String ngaySanXuat) {
        this.ngaySanXuat = ngaySanXuat;
    }

    public String getDateCode() {
        return dateCode;
    }

    public void setDateCode(String dateCode) {
        this.dateCode = dateCode;
    }

    public Integer getSoLuongCaiDatPallet() {
        return soLuongCaiDatPallet;
    }

    public void setSoLuongCaiDatPallet(Integer soLuongCaiDatPallet) {
        this.soLuongCaiDatPallet = soLuongCaiDatPallet;
    }

    public Integer getThuTuGiaPallet() {
        return thuTuGiaPallet;
    }

    public void setThuTuGiaPallet(Integer thuTuGiaPallet) {
        this.thuTuGiaPallet = thuTuGiaPallet;
    }

    public String getSoLuongBaoNgoaiThungGiaPallet() {
        return soLuongBaoNgoaiThungGiaPallet;
    }

    public void setSoLuongBaoNgoaiThungGiaPallet(
        String soLuongBaoNgoaiThungGiaPallet
    ) {
        this.soLuongBaoNgoaiThungGiaPallet = soLuongBaoNgoaiThungGiaPallet;
    }

    public Integer getSlThung() {
        return slThung;
    }

    public void setSlThung(Integer slThung) {
        this.slThung = slThung;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNguoiKiemTra() {
        return nguoiKiemTra;
    }

    public void setNguoiKiemTra(String nguoiKiemTra) {
        this.nguoiKiemTra = nguoiKiemTra;
    }

    public String getKetQuaKiemTra() {
        return ketQuaKiemTra;
    }

    public void setKetQuaKiemTra(String ketQuaKiemTra) {
        this.ketQuaKiemTra = ketQuaKiemTra;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getSerialBox() {
        return serialBox;
    }

    public void setSerialBox(String serialBox) {
        this.serialBox = serialBox;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(Boolean printStatus) {
        this.printStatus = printStatus;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMaSAP() {
        return maSAP;
    }

    public void setMaSAP(String maSAP) {
        this.maSAP = maSAP;
    }

    public String getWoId() {
        return woId;
    }

    public void setWoId(String woId) {
        this.woId = woId;
    }

    public String getErpWo() {
        return erpWo;
    }

    public void setErpWo(String erpWo) {
        this.erpWo = erpWo;
    }

    public String getMaLenhSanXuat() {
        return maLenhSanXuat;
    }

    public void setMaLenhSanXuat(String maLenhSanXuat) {
        this.maLenhSanXuat = maLenhSanXuat;
    }

    public Integer getTotalProductsOnPallet() {
        return totalProductsOnPallet;
    }

    public void setTotalProductsOnPallet(Integer totalProductsOnPallet) {
        this.totalProductsOnPallet = totalProductsOnPallet;
    }
}
