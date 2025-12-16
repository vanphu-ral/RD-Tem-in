package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.WarehouseNoteInfoApproval;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WarehouseNoteInfoApproval entity using
 * partner3
 * datasource.
 */
@SuppressWarnings("unused")
@Repository
public interface Partner3WarehouseNoteInfoApprovalRepository
    extends JpaRepository<WarehouseNoteInfoApproval, Long> {}
