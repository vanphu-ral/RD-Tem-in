package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.*;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
public interface scanDetailCheckRepository extends JpaRepository<scanDetailCheck, Long> {
    public List<scanDetailCheck> findAllByOrderId(Long orderId);

    @Modifying
    @Query(
        value = " select" +
        " wo.record_name as recordName," +
        " wo.record_value as recordValue," +
        " wo.result as result, " +
        " wo.position as position, " +
        " mc.machine_name as machineName " +
        " from scan_detail_check as wo\n" +
        " inner join Scan_machines as mc on wo.machine_id = mc.machine_id\n" +
        " where wo.order_id = ?1 ;",
        nativeQuery = true
    )
    public List<TongHopResponse> listDetailCheckByWorkOrder(Long orderId);

    @Modifying
    @Query(
        value = "insert into scan_detail_check (order_id,record_value,result,position,username,machine_id,record_name,create_at) values(?1,?2,?3,?4,?5,?6,?7,?8) ;",
        nativeQuery = true
    )
    public void insertDetailCheck(
        Long orderId,
        String recordValue,
        String result,
        Integer position,
        String username,
        Long machineId,
        String recordName,
        String createAt
    );

    @Query(
        value = "SELECT \n" +
        "\t\tresult as result\n" +
        "\t  ,count(record_value) as recordValue\n" +
        "  FROM [ProfileProductions].[dbo].[scan_detail_check] where order_id = ?1 group by result ",
        nativeQuery = true
    )
    public List<TongHopResponse> tongHop(Long orderId);

    @Query(
        value = "" +
        "SELECT\n" +
        "\t\torder_id as orderId,\n" +
        "\t\tsum(case when result ='PASS' then 1   end) as pass,\n" +
        "\t\tsum(case when result ='NG' then 1 end) as ng\n" +
        "          FROM [ProfileProductions].[dbo].[scan_detail_check] as a  group by order_id \n" +
        "\t\t  order by order_id desc OFFSET ?1 ROWS FETCH NEXT ?2 ROWS ONLY",
        nativeQuery = true
    )
    public List<DetailCheckResponse> getTotalPassNg(Integer pageNumber, Integer itemPerPage);

    @Query(
        value = "" +
        "select \n" +
        "  mc.ma_thiet_bi as tenThietBi,\n" +
        "  gm.group_name as tenNhomThietBi,\n" +
        "  pd.product_code as maSanPham,\n" +
        "  pd.product_name as tenSanPham,\n" +
        "  vs.version as version,\n" +
        "  dc.record_name as tieuChiKiemTra,\n" +
        "  pc.check_value as noiDungDoiChieu,\n" +
        "  dc.record_value as ketQuaCheck,\n" +
        "  dc.result as ketLuan,\n" +
        "  dc.position as viTri,\n" +
        "  dc.username as nhanVien,\n" +
        "  dc.create_at as thoiGianCheck\n" +
        "  from scan_detail_check as dc\n" +
        "  inner join Scan_workOrder as wo on wo.order_id = dc.order_id\n" +
        "  inner join Scan_profileCheck as pc on pc.product_id = wo.product_id\n" +
        "  inner join Scan_products as pd on pd.product_id = wo.product_id\n" +
        "  inner join scan_product_versions as vs on vs.version_id = pc.version_id\n" +
        "  inner join nhom_thiet_bi as mc on mc.id = pc.machine_id\n" +
        "  inner join Scan_groupMachines as gm on gm.group_id = mc.group_id\n" +
        "  where dc.order_id = ?1 ;",
        nativeQuery = true
    )
    public List<TongHopResponse> getExportInfo(Long OrderId);
}
