package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.WarehouseNoteInfo;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WarehouseNoteInfo entity using partner3
 * datasource.
 */
@SuppressWarnings("unused")
@Repository
public interface Partner3WarehouseStampInfoRepository
    extends JpaRepository<WarehouseNoteInfo, Long> {
    @Query("SELECT w FROM WarehouseNoteInfo w WHERE w.createBy = :createBy")
    List<WarehouseNoteInfo> findBycreateBy(String createBy);

    @Query("SELECT w FROM WarehouseNoteInfo w WHERE w.approverBy = :approverBy")
    List<WarehouseNoteInfo> findByApproverBy(String approverBy);

    @Query(
        "SELECT w FROM WarehouseNoteInfo w WHERE w.trangThai != 'Bản nháp' AND w.productType = 'Bán thành phẩm'"
    )
    List<WarehouseNoteInfo> findByTrangThaiNotDraft();

    @Query("SELECT w FROM WarehouseNoteInfo w WHERE w.deletedBy IS NULL")
    Page<WarehouseNoteInfo> findAll(Pageable pageable);
}
