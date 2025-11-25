package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.WarehouseStampInfoDetail;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WarehouseStampInfoDetail entity using
 * partner3
 * datasource.
 */
@SuppressWarnings("unused")
@Repository
public interface Partner3WarehouseStampInfoDetailRepository
    extends JpaRepository<WarehouseStampInfoDetail, Long> {}
