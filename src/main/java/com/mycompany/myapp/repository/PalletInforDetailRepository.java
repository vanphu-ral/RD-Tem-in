package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.PalletInforDetail;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PalletInforDetail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PalletInforDetailRepository
    extends JpaRepository<PalletInforDetail, String> {}
