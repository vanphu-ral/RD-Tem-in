package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.WarehouseNoteInfoDetail;
import java.util.List;
import org.springframework.data.jpa.repository.*;
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
}
