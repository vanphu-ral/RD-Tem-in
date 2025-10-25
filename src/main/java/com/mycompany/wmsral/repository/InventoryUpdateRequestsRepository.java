package com.mycompany.wmsral.repository;

import com.mycompany.wmsral.domain.InventoryUpdateRequests;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryUpdateRequestsRepository
    extends JpaRepository<InventoryUpdateRequests, Long> {
    //phe duyet
    @Query(
        value = "SELECT * FROM Inventory_Update_Requests " +
        "WHERE status = 'PENDING' " +
        "ORDER BY created_time DESC " +
        "OFFSET ?1 ROWS FETCH NEXT ?2 ROWS ONLY",
        nativeQuery = true
    )
    List<InventoryUpdateRequests> getPendingRequests(int offset, int pageSize);

    //lich su
    @Query(
        value = "SELECT * FROM Inventory_Update_Requests " +
        "WHERE status IN ('APPROVE', 'REJECT') " +
        "ORDER BY " +
        "  CASE status " +
        "    WHEN 'APPROVE' THEN 1 " +
        "    WHEN 'REJECT' THEN 2 " +
        "    ELSE 3 " +
        "  END, " +
        "created_time DESC " +
        "OFFSET ?1 ROWS FETCH NEXT ?2 ROWS ONLY",
        nativeQuery = true
    )
    List<InventoryUpdateRequests> getHistoryRequests(int offset, int pageSize);

    //query lay tong so luong ban ghi
    @Query(
        value = "SELECT COUNT(*) FROM Inventory_Update_Requests WHERE status = 'PENDING'",
        nativeQuery = true
    )
    long countPendingRequests();

    @Query(
        value = "SELECT COUNT(*) FROM Inventory_Update_Requests WHERE status IN ('APPROVE', 'REJECT')",
        nativeQuery = true
    )
    long countHistoryRequests();

    Page<InventoryUpdateRequests> findAll(Pageable pageable);
}
