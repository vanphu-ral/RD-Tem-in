package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.PalletInforDetail;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PalletInforDetail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PalletInforDetailRepository
    extends JpaRepository<PalletInforDetail, String> {
    /**
     * Find the max serial_pallet that starts with the given prefix.
     *
     * @param prefix the prefix to search for
     * @return the max serial_pallet
     */
    @Query(
        "SELECT p.serialPallet FROM PalletInforDetail p WHERE p.serialPallet LIKE :prefix% ORDER BY p.serialPallet DESC"
    )
    String findMaxSerialPalletStartingWith(@Param("prefix") String prefix);
}
