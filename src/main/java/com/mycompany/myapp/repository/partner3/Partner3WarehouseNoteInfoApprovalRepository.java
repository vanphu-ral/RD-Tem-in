package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.WarehouseNoteInfoApproval;
import com.mycompany.myapp.service.WarehouseNoteInfoApprovalService.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WarehouseNoteInfoApproval entity using
 * partner3
 * datasource.
 */
@SuppressWarnings("unused")
@Repository
public interface Partner3WarehouseNoteInfoApprovalRepository
    extends JpaRepository<WarehouseNoteInfoApproval, Long> {
    @Query(
        "SELECT w FROM WarehouseNoteInfoApproval w WHERE " +
        "(:maLenhSanXuat IS NULL OR w.maLenhSanXuat LIKE %:maLenhSanXuat%) AND " +
        "(:sapCode IS NULL OR w.sapCode LIKE %:sapCode%) AND " +
        "(:sapName IS NULL OR w.sapName LIKE %:sapName%) AND " +
        "(:workOrderCode IS NULL OR w.workOrderCode LIKE %:workOrderCode%) AND " +
        "(:version IS NULL OR w.version LIKE %:version%) AND " +
        "(:storageCode IS NULL OR w.storageCode = :storageCode) AND " +
        "(:createBy IS NULL OR w.createBy LIKE %:createBy%) AND " +
        "(:trangThai IS NULL OR w.trangThai = :trangThai)"
    )
    Page<WarehouseNoteInfoApproval> findAllWithFilter(
        Pageable pageable,
        @Param("maLenhSanXuat") String maLenhSanXuat,
        @Param("sapCode") String sapCode,
        @Param("sapName") String sapName,
        @Param("workOrderCode") String workOrderCode,
        @Param("version") String version,
        @Param("storageCode") String storageCode,
        @Param("createBy") String createBy,
        @Param("trangThai") String trangThai
    );
}
