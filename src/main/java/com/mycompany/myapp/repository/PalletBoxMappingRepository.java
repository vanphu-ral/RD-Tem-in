package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.PalletBoxMapping;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PalletBoxMapping entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PalletBoxMappingRepository
  extends JpaRepository<PalletBoxMapping, Long> {}
