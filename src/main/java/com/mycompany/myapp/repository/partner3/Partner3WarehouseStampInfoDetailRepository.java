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
     * @param maLenhSanXuatId the id of the WarehouseNoteInfo
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
}
