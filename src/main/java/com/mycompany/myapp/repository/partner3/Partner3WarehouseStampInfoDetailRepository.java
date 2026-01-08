package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.WarehouseNoteInfoDetail;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WarehouseNoteInfoDetail entity using
 * partner3 datasource.
 */
@SuppressWarnings("unused")
@Repository
public interface Partner3WarehouseStampInfoDetailRepository
    extends JpaRepository<WarehouseNoteInfoDetail, Long> {
    /**
     * Find all WarehouseNoteInfoDetail by ma_lenh_san_xuat_id.
     *
     * @param maLenhSanXuatId the maLenhSanXuat of the WarehouseNoteInfo
     * @return the list of WarehouseNoteInfoDetail
     */
    List<WarehouseNoteInfoDetail> findByMaLenhSanXuatId(Long maLenhSanXuatId);

    /**
     * Find WarehouseNoteInfoDetail by reelId.
     *
     * @param reelId the reel ID to search for
     * @return the WarehouseNoteInfoDetail if found
     */
    Optional<WarehouseNoteInfoDetail> findByReelId(String reelId);

    /**
     * Find WarehouseNoteInfoDetail by reel IDs.
     *
     * @param reelIds the list of reel IDs to search for
     * @return the list of WarehouseNoteInfoDetail matching the reel IDs
     */
    List<WarehouseNoteInfoDetail> findByReelIdIn(List<String> reelIds);

    /**
     * Find WarehouseNoteInfoDetail by IDs.
     *
     * @param ids the list of IDs to search for
     * @return the list of WarehouseNoteInfoDetail matching the IDs
     */
    List<WarehouseNoteInfoDetail> findByIdIn(List<Long> ids);

    /**
     * Find the reel_id with max ID where reel_id starts with the given prefix.
     *
     * @param prefix the prefix to search for
     * @return the reel_id with max ID
     */
    @Query(
        value = "SELECT w.reel_id FROM warehouse_note_info_detail w WHERE w.reel_id LIKE CONCAT(:prefix, '%') ORDER BY w.id DESC LIMIT 1",
        nativeQuery = true
    )
    String findMaxReelIdStartingWith(@Param("prefix") String prefix);

    /**
     * Check if a pallet code exists in a work order.
     *
     * @param palletCode    the pallet code to search for
     * @param workOrderCode the work order code to search for
     * @return true if the pallet code exists in the work order, false otherwise
     */
    @Query(
        "SELECT COUNT(p) > 0 FROM PalletInforDetail p JOIN WarehouseNoteInfo w ON p.maLenhSanXuatId = w.id WHERE p.serialPallet = :palletCode AND w.workOrderCode = :workOrderCode"
    )
    boolean existsByPalletCodeAndWorkOrderCode(
        @Param("palletCode") String palletCode,
        @Param("workOrderCode") String workOrderCode
    );

    /**
     * Check if a box code exists in a work order.
     *
     * @param boxCode       the box code to search for
     * @param workOrderCode the work order code to search for
     * @return true if the box code exists in the work order, false otherwise
     */
    @Query(
        "SELECT COUNT(w) > 0 FROM WarehouseNoteInfoDetail w WHERE w.reelId = :boxCode AND w.maLenhSanXuat.workOrderCode = :workOrderCode"
    )
    boolean existsByBoxCodeAndWorkOrderCode(
        @Param("boxCode") String boxCode,
        @Param("workOrderCode") String workOrderCode
    );
}
