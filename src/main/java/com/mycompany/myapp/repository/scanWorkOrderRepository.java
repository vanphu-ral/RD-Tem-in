package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.scanWorkorder;
import com.mycompany.myapp.domain.workOrderInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface scanWorkOrderRepository extends JpaRepository<scanWorkorder, Long> {
    @Query(
        value = "select " +
        "wo.order_id as orderId," +
        "wo.work_order as workOrder,\n" +
        " wo.lot as lot,\n" +
        " wo.number_of_plan as sanLuong,\n" +
        " wo.working as trangThai,\n" +
        " pd.product_code as productCode,\n" +
        " pd.product_name as productName,\n" +
        " wo.create_at as createAt,\n" +
        " gm.group_name as groupName, " +
        "wo.group_id as groupId," +
        "wo.product_id as productId, " +
        "wo.run_time as runTime " +
        " from Scan_workOrder as wo\n" +
        "  inner join scan_products as pd on pd.product_id = wo.product_id\n" +
        "  inner join Scan_groupMachines as gm on gm.group_id = wo.group_id " +
        "order by order_id desc; ",
        nativeQuery = true
    )
    public List<workOrderInfo> listWorkOrderByGroup();

    @Query(value = "update Scan_workOrder set working=?1 " + "inner join scan " + "where order_id=?2;", nativeQuery = true)
    public void updateWorkingWorkOrder(Integer working, Long orderId);

    @Query(
        value = "select " +
        " wo.order_id as orderId," +
        " wo.work_order as workOrder,\n" +
        " wo.lot as lot,\n" +
        " wo.number_of_plan as sanLuong,\n" +
        " wo.working as trangThai,\n" +
        " pd.product_code as productCode,\n" +
        " pd.product_name as productName,\n" +
        " wo.create_at as createAt,\n" +
        " gm.group_name as groupName," +
        "wo.group_id as groupId, " +
        "wo.product_id as productId," +
        "wo.run_time as runTime," +
        "pd.product_version as version " +
        " from Scan_workOrder as wo\n" +
        "  inner join scan_products as pd on pd.product_id = wo.product_id\n" +
        "  inner join Scan_groupMachines as gm on gm.group_id = wo.group_id " +
        "where wo.order_id =?1 ; ",
        nativeQuery = true
    )
    public workOrderInfo listWorkOrderByGroupById(Long orderId);

    @Modifying
    @Query(value = "update Scan_workOrder set working = ?1, run_time = ?2 where order_id = ?3", nativeQuery = true)
    public void updateWorkOrderWorking(Integer working, Integer runTime, Long orderId);

    @Query(
        value = "" +
        "select  \n" +
        "        wo.order_id as orderId, \n" +
        "        wo.work_order as workOrder, \n" +
        "         wo.lot as lot, \n" +
        "         wo.number_of_plan as sanLuong, \n" +
        "         wo.working as trangThai, \n" +
        "         pd.product_code as productCode, \n" +
        "         pd.product_name as productName, \n" +
        "         wo.create_at as createAt, \n" +
        "         gm.group_name as groupName,  \n" +
        "        wo.group_id as groupId, \n" +
        "        wo.product_id as productId,  \n" +
        "        wo.run_time as runTime,  \n " +
        "        sum(case when dc.result ='PASS' then 1 end) as pass,\n" +
        "\t\t    sum(case when dc.result ='NG' then 1 end) as ng " +
        "         from Scan_workOrder as wo \n" +
        "          inner join scan_products as pd on pd.product_id = wo.product_id \n" +
        "          inner join Scan_groupMachines as gm on gm.group_id = wo.group_id\n " +
        "left join scan_detail_check as dc on dc.order_id = wo.order_id " +
        "\t\t  where wo.work_order like ?1\n" +
        "\t\t  and wo.lot like ?2\n" +
        "\t\t  and pd.product_code like ?3\n" +
        "\t\t  and pd.product_name like ?4\n" +
        "\t\t  and gm.group_name like ?5\n" +
        "\t\t  and wo.create_at between ?6 and ?7\n " +
        "group by " +
        "wo.order_id," +
        "wo.work_order," +
        "wo.lot," +
        "wo.number_of_plan," +
        "wo.working," +
        "wo.create_at," +
        "wo.group_id," +
        "wo.product_id," +
        "wo.run_time," +
        "pd.product_code," +
        "pd.product_name," +
        "gm.group_name " +
        "order by wo.order_id desc  OFFSET ?8 ROWS FETCH NEXT ?9 ROWS ONLY;",
        nativeQuery = true
    )
    public List<workOrderInfo> getListWorkOrders(
        String workOrder,
        String lot,
        String productCode,
        String productName,
        String groupName,
        String entryTime1,
        String entryTime2,
        Integer pageNumber,
        Integer itemPerPage
    );

    @Query(
        value = "" +
        "select  \n" +
        "       count(wo.order_id)\n" +
        "         from Scan_workOrder as wo \n" +
        "          inner join scan_products as pd on pd.product_id = wo.product_id  \n" +
        "          inner join Scan_groupMachines as gm on gm.group_id = wo.group_id \n" +
        "\t\t  where wo.work_order like ?1 \n" +
        "\t\t  and wo.lot like ?2 \n" +
        "\t\t  and pd.product_code like ?3 \n" +
        "\t\t  and pd.product_name like ?4 \n" +
        "\t\t  and gm.group_name like ?5 \n" +
        "\t\t  and wo.create_at between ?6 and ?7 ;",
        nativeQuery = true
    )
    public Integer getTotalData(
        String workOrder,
        String lot,
        String productCode,
        String productName,
        String groupName,
        String entryTime1,
        String entryTime2
    );
}
