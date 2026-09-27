package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.PalletMngt;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PalletMngt entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PalletMngtRepository extends JpaRepository<PalletMngt, Long> {}
