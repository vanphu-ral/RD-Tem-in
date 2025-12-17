package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.ReelIdInWarehouseNoteInfoApproval;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReelIdInWarehouseNoteInfoApproval entity
 * using partner3
 * datasource.
 */
@SuppressWarnings("unused")
@Repository
public interface Partner3ReelIdInWarehouseNoteInfoApprovalRepository
    extends JpaRepository<ReelIdInWarehouseNoteInfoApproval, Long> {
    /**
     * Find all ReelIdInWarehouseNoteInfoApproval by warehouseNoteInfoApprovalId.
     *
     * @param warehouseNoteInfoApprovalId the warehouse note info approval ID
     * @return list of ReelIdInWarehouseNoteInfoApproval
     */
    List<ReelIdInWarehouseNoteInfoApproval> findByWarehouseNoteInfoApprovalId(
        Long warehouseNoteInfoApprovalId
    );

    /**
     * Delete all ReelIdInWarehouseNoteInfoApproval by warehouseNoteInfoApprovalId.
     *
     * @param warehouseNoteInfoApprovalId the warehouse note info approval ID
     */
    @Modifying
    @Query(
        "DELETE FROM ReelIdInWarehouseNoteInfoApproval r WHERE r.warehouseNoteInfoApproval.id = :warehouseNoteInfoApprovalId"
    )
    void deleteByWarehouseNoteInfoApprovalId(
        @Param("warehouseNoteInfoApprovalId") Long warehouseNoteInfoApprovalId
    );
}
