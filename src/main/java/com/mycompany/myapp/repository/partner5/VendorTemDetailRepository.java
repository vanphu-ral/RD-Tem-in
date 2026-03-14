package com.mycompany.myapp.repository.partner5;

import com.mycompany.myapp.domain.VendorTemDetail;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VendorTemDetail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VendorTemDetailRepository
    extends
        JpaRepository<VendorTemDetail, Long>,
        JpaSpecificationExecutor<VendorTemDetail> {}
