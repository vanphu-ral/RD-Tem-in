package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.SerialBoxPalletMapping;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SerialBoxPalletMapping entity using
 * partner3
 * datasource.
 */
@SuppressWarnings("unused")
@Repository
public interface Partner3SerialBoxPalletMappingRepository
    extends JpaRepository<SerialBoxPalletMapping, Long> {
    /**
     * Find all SerialBoxPalletMapping by ma_lenh_san_xuat_id.
     *
     * @param maLenhSanXuatId the maLenhSanXuat of the WarehouseNoteInfo
     * @return the list of SerialBoxPalletMapping
     */
    List<SerialBoxPalletMapping> findByMaLenhSanXuatId(Long maLenhSanXuatId);

    /**
     * Find all SerialBoxPalletMapping by serial_pallet.
     *
     * @param serialPallet the serial pallet
     * @return the list of SerialBoxPalletMapping
     */
    List<SerialBoxPalletMapping> findBySerialPallet(String serialPallet);

    /**
     * Find SerialBoxPalletMapping by serial_box.
     *
     * @param serialBox the serial box (reelId)
     * @return the SerialBoxPalletMapping if found
     */
    Optional<SerialBoxPalletMapping> findBySerialBox(String serialBox);

    /**
     * Check if a box code exists with a specific status.
     *
     * @param serialBox the box code to search for
     * @param status    the status to search for
     * @return true if the box code exists with the specified status, false otherwise
     */
    @Query(
        "SELECT COUNT(s) > 0 FROM SerialBoxPalletMapping s WHERE s.serialBox = :serialBox AND s.status = :status"
    )
    boolean existsBySerialBoxAndStatus(
        @Param("serialBox") String serialBox,
        @Param("status") Integer status
    );
}
