package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.VendorLabelInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VendorLabelInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VendorLabelInfoRepository
  extends JpaRepository<VendorLabelInfo, Long> {}
