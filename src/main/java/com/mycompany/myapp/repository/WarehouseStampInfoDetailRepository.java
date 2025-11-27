package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.WarehouseNoteInfoDetail;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WarehouseNoteInfoDetail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WarehouseStampInfoDetailRepository
    extends JpaRepository<WarehouseNoteInfoDetail, Long> {
    /**
     * Find the max reel_id that starts with the given prefix.
     *
     * @param prefix the prefix to search for
     * @return the max reel_id
     */
    @Query(
        "SELECT w.reelId FROM WarehouseNoteInfoDetail w WHERE w.reelId LIKE :prefix% ORDER BY w.reelId DESC"
    )
    String findMaxReelIdStartingWith(@Param("prefix") String prefix);
}
