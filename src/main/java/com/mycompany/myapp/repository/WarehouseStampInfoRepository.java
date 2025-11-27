package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.WarehouseNoteInfo;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WarehouseNoteInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WarehouseStampInfoRepository
    extends JpaRepository<WarehouseNoteInfo, Long> {
    @Query("SELECT w FROM WarehouseNoteInfo w WHERE w.createBy = :createBy")
    List<WarehouseNoteInfo> findBycreateBy(String createBy);

    @Query("SELECT w FROM WarehouseNoteInfo w WHERE w.approverBy = :approverBy")
    List<WarehouseNoteInfo> findByApproverBy(String approverBy);
}
