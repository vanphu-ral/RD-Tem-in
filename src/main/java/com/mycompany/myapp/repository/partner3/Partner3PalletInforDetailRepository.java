package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.PalletInforDetail;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
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
     * @param maLenhSanXuatId the id of the WarehouseNoteInfo
     * @return the list of PalletInforDetail
     */
    List<PalletInforDetail> findByMaLenhSanXuatId(Long maLenhSanXuatId);
}
