package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.WarehouseNoteInfoDetail;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WarehouseNoteInfoDetail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WarehouseStampInfoDetailRepository
    extends JpaRepository<WarehouseNoteInfoDetail, Long> {}
