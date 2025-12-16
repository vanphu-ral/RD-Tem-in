package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.GenTemConfig;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GenTemConfig entity using partner3
 * datasource.
 */
@SuppressWarnings("unused")
@Repository
public interface Partner3GenTemConfigRepository
    extends JpaRepository<GenTemConfig, Long> {
    /**
     * Find all GenTemConfig by ma_lenh_san_xuat_id.
     *
     * @param maLenhSanXuatId the maLenhSanXuat of the WarehouseNoteInfo
     * @return the list of GenTemConfig
     */
    List<GenTemConfig> findByMaLenhSanXuatId(Long maLenhSanXuatId);
}
