package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.SerialBoxPalletMapping;
import java.util.List;
import org.springframework.data.jpa.repository.*;
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
     * @param maLenhSanXuatId the id of the WarehouseNoteInfo
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
}
