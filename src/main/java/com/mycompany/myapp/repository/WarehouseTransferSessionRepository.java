package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.WarehouseTransferSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WarehouseTransferSession entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WarehouseTransferSessionRepository
    extends
        JpaRepository<WarehouseTransferSession, Long>,
        JpaSpecificationExecutor<WarehouseTransferSession> {
    @Query(
        "select s from WarehouseTransferSession s left join fetch s.details where s.id = :id"
    )
    Optional<WarehouseTransferSession> findByIdWithDetails(
        @Param("id") Long id
    );
}
