package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.LenhSanXuat;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LenhSanXuat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LenhSanXuatRepository
    extends JpaRepository<LenhSanXuat, Long> {
    @Query(
        "select lenh_san_xuat from LenhSanXuat lenh_san_xuat where " +
        "lenh_san_xuat.maLenhSanXuat like %:b% and lenh_san_xuat.sapCode like %:c% " +
        "and lenh_san_xuat.sapName like %:d% and lenh_san_xuat.workOrderCode like %:e% " +
        "and lenh_san_xuat.version like %:f% and lenh_san_xuat.storageCode like %:g% " +
        "and lenh_san_xuat.createBy like %:h% and lenh_san_xuat.trangThai like %:i%"
    )
    public List<LenhSanXuat> timKiemLenhSanXuat(
        @Param("b") String maLenhSanXuat,
        @Param("c") String sapCode,
        @Param("d") String sapName,
        @Param("e") String workOrderCode,
        @Param("f") String version,
        @Param("g") String storageCode,
        @Param("h") String createBy,
        @Param("i") String trangThai
    );

    //    @Query(
    //        value = "select top(20) * from lenh_san_xuat LenhSanXuat where" +
    //        " trang_thai = N'Chờ duyệt' or trang_thai = N'Đã phê duyệt' " +
    //        "or trang_thai = N'Từ chối' or trang_thai = N'Kho hủy'",
    //    nativeQuery = true
    //        )
    @Query(
        value = "select  * from  lenh_san_xuat as a where \n" +
        "a.trang_thai != N'Bản nháp' " +
        "and a.ma_lenh_san_xuat like ?1 " +
        "and a.sap_code like ?2 " +
        "and a.sap_name like ?3 " +
        "and a.work_order_code like ?4 " +
        "and a.version like ?5 " +
        "and a.storage_code like ?6 " +
        "and a.create_by like ?7 " +
        "and a.entry_time between ?8 and ?9 " +
        "and a.time_update between ?10 and ?11  " +
        "and a.trang_thai like ?12 " +
        "ORDER BY a.trang_thai asc, a.entry_time desc " +
        "OFFSET ?13 ROWS FETCH NEXT ?14 ROWS ONLY;",
        nativeQuery = true
    )
    public List<LenhSanXuat> timKiemQuanLyPheDuyet(
        String maLenhSanXuat,
        String sapCode,
        String sapName,
        String workOrderCode,
        String version,
        String storageCode,
        String createBy,
        String entryTime1,
        String entryTime2,
        String timeUpdate1,
        String timeUpdate2,
        String trangThai,
        Integer pageNumber,
        Integer itemPerPage
    );

    @Query(
        value = "select  COUNT(trang_thai) from lenh_san_xuat where \n" +
        "trang_thai != N'Bản nháp' " +
        "and ma_lenh_san_xuat like ?1 " +
        "and sap_code like ?2 " +
        "and sap_name like ?3 " +
        "and work_order_code like ?4 " +
        "and version like ?5 " +
        "and storage_code like ?6 " +
        "and create_by like ?7 " +
        "and entry_time between ?8 and ?9 " +
        "and time_update between ?10 and ?11  " +
        "and trang_thai like ?12 ;",
        nativeQuery = true
    )
    public Integer totalData(
        String maLenhSanXuat,
        String sapCode,
        String sapName,
        String workOrderCode,
        String version,
        String storageCode,
        String createBy,
        String entryTime1,
        String entryTime2,
        String timeUpdate1,
        String timeUpdate2,
        String trangThai
    );

    @Query(
        value = "select  * from  lenh_san_xuat as a where \n" +
        " a.ma_lenh_san_xuat like ?1 " +
        "and a.sap_code like ?2 " +
        "and a.sap_name like ?3 " +
        "and a.work_order_code like ?4 " +
        "and a.version like ?5 " +
        "and a.storage_code like ?6 " +
        "and a.create_by like ?7 " +
        "and a.entry_time between ?8 and ?9 " +
        "and a.trang_thai like ?10 " +
        "ORDER BY a.trang_thai asc, a.entry_time desc " +
        "OFFSET ?11 ROWS FETCH NEXT ?12 ROWS ONLY;",
        nativeQuery = true
    )
    public List<LenhSanXuat> timKiemThongTinTemSanXuat(
        String maLenhSanXuat,
        String sapCode,
        String sapName,
        String workOrderCode,
        String version,
        String storageCode,
        String createBy,
        String entryTime1,
        String entryTime2,
        String trangThai,
        Integer pageNumber,
        Integer itemPerPage
    );

    @Query(
        value = "select  COUNT(trang_thai) from lenh_san_xuat where \n" +
        "ma_lenh_san_xuat like ?1 " +
        "and sap_code like ?2 " +
        "and sap_name like ?3 " +
        "and work_order_code like ?4 " +
        "and version like ?5 " +
        "and storage_code like ?6 " +
        "and create_by like ?7 " +
        "and entry_time between ?8 and ?9 " +
        "and trang_thai like ?10 ;",
        nativeQuery = true
    )
    public Integer totalDataThongTinTemSanXuat(
        String maLenhSanXuat,
        String sapCode,
        String sapName,
        String workOrderCode,
        String version,
        String storageCode,
        String createBy,
        String entryTime1,
        String entryTime2,
        String trangThai
    );

    @Query(
        value = "select DISTINCT ma_lenh_san_xuat from lenh_san_xuat LenhSanXuat ",
        nativeQuery = true
    )
    public List<String> getListMaLenhSanXuat();

    @Query(
        value = "select DISTINCT sap_code from lenh_san_xuat LenhSanXuat ",
        nativeQuery = true
    )
    public List<String> getListSapCode();

    @Query(
        value = "select DISTINCT sap_name from lenh_san_xuat LenhSanXuat ",
        nativeQuery = true
    )
    public List<String> getListSapName();

    @Query(
        value = "select DISTINCT work_order_code from lenh_san_xuat LenhSanXuat ",
        nativeQuery = true
    )
    public List<String> getListWorkOrderCode();

    @Query(
        value = "select DISTINCT version from lenh_san_xuat LenhSanXuat ",
        nativeQuery = true
    )
    public List<String> getListVersion();

    @Modifying
    @Transactional
    @Query(
        value = "update lenh_san_xuat set trang_thai = ?1 where id = ?2 ;",
        nativeQuery = true
    )
    public void exportCsvStatus(String trangThai, Long id);
}
