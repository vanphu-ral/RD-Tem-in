package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.WarehouseStampInfo;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WarehouseStampInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WarehouseStampInfoRepository
    extends JpaRepository<WarehouseStampInfo, Long> {
    @Query("SELECT w FROM WarehouseStampInfo w WHERE w.createBy = :createBy")
    List<WarehouseStampInfo> findBycreateBy(String createBy);

    @Query(
        "SELECT w FROM WarehouseStampInfo w WHERE w.approverBy = :approverBy"
    )
    List<WarehouseStampInfo> findByApproverBy(String approverBy);
}
