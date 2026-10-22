package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.WarehouseTransferDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WarehouseTransferDetail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WarehouseTransferDetailRepository
    extends JpaRepository<WarehouseTransferDetail, Long> {
    long countByOwtqDocEntry(Long owtqDocEntry);

    @Query(
        "SELECT SUM(w.initialQuantity) FROM WarehouseTransferDetail w WHERE w.owtqDocEntry = :owtqDocEntry"
    )
    Long sumInitialQuantityByOwtqDocEntry(
        @Param("owtqDocEntry") Long owtqDocEntry
    );
}
