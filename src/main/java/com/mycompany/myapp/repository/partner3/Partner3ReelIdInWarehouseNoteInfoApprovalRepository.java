package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.ReelIdInWarehouseNoteInfoApproval;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReelIdInWarehouseNoteInfoApproval entity
 * using partner3
 * datasource.
 */
@SuppressWarnings("unused")
@Repository
public interface Partner3ReelIdInWarehouseNoteInfoApprovalRepository
    extends JpaRepository<ReelIdInWarehouseNoteInfoApproval, String> {}
