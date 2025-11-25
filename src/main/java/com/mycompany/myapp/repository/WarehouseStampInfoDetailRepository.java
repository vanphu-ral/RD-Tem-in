package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.WarehouseStampInfoDetail;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WarehouseStampInfoDetail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WarehouseStampInfoDetailRepository
    extends JpaRepository<WarehouseStampInfoDetail, Long> {}
