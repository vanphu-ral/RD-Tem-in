package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.PalletInforDetail;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PalletInforDetail entity using
 * partner3 datasource.
 */
@SuppressWarnings("unused")
@Repository
public interface Partner3PalletInforDetailRepository
    extends JpaRepository<PalletInforDetail, Long> {
    Optional<PalletInforDetail> findBySerialPallet(String serialPallet);

    /**
     * Find all PalletInforDetail by ma_lenh_san_xuat_id.
     *
     * @param maLenhSanXuatId the maLenhSanXuat of the WarehouseNoteInfo
     * @return the list of PalletInforDetail
     */
    List<PalletInforDetail> findByMaLenhSanXuatId(Long maLenhSanXuatId);

    /**
     * Find all PalletInforDetail by work order code.
     * This method finds pallets from warehouse notes that match the work order code
     * and are not deleted (deleted_by IS NULL).
     *
     * @param workOrderCode the work order code
     * @return the list of PalletInforDetail
     */
    @Query(
        "SELECT p FROM PalletInforDetail p " +
        "WHERE p.maLenhSanXuatId IN (" +
        "  SELECT w.id FROM WarehouseNoteInfo w " +
        "  WHERE w.workOrderCode = :workOrderCode AND w.deletedBy IS NULL" +
        ")"
    )
    List<PalletInforDetail> findByWorkOrderCode(
        @Param("workOrderCode") String workOrderCode
    );

    /**
     * Find the serial_pallet with the max ID.
     *
     * @param prefix the prefix (not used, kept for compatibility)
     * @return the serial_pallet with max ID
     */
    @Query(
        value = "SELECT p.serial_pallet FROM pallet_infor_detail p ORDER BY p.id DESC LIMIT 1",
        nativeQuery = true
    )
    String findMaxSerialPalletStartingWith(@Param("prefix") String prefix);

    /**
     * Find all PalletInforDetail by ma_lenh_san_xuat_id and filter by reel IDs.
     * This method finds pallet details that match the ma_lenh_san_xuat_id and
     * whose IDs are in the provided list of reel IDs.
     *
     * @param maLenhSanXuatId the maLenhSanXuat of the WarehouseNoteInfo
     * @param reelIds         the list of reel IDs to filter by
     * @return the list of PalletInforDetail
     */
    @Query(
        "SELECT p FROM PalletInforDetail p " +
        "WHERE p.maLenhSanXuatId = :maLenhSanXuatId " +
        "AND p.id IN :reelIds"
    )
    List<PalletInforDetail> findByMaLenhSanXuatIdAndIdIn(
        @Param("maLenhSanXuatId") Long maLenhSanXuatId,
        @Param("reelIds") List<Long> reelIds
    );
}
