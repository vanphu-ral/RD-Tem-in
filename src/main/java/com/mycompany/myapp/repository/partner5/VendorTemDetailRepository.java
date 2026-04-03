package com.mycompany.myapp.repository.partner5;

import com.mycompany.myapp.domain.VendorTemDetail;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VendorTemDetail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VendorTemDetailRepository
    extends
        JpaRepository<VendorTemDetail, Long>,
        JpaSpecificationExecutor<VendorTemDetail> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM VendorTemDetail v WHERE v.poDetailId = :poDetailId")
    void deleteByPoDetailId(@Param("poDetailId") Long poDetailId);
}
