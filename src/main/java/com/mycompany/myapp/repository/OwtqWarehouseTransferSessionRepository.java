package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.OwtqWarehouseTransferSession;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OwtqWarehouseTransferSession entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OwtqWarehouseTransferSessionRepository
    extends JpaRepository<OwtqWarehouseTransferSession, Long> {
    List<OwtqWarehouseTransferSession> findByWarehouseTransferSession_Id(
        Long id
    );
}
