package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ScanPallet;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ScanPallet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScanPalletRepository extends JpaRepository<ScanPallet, Long> {}
