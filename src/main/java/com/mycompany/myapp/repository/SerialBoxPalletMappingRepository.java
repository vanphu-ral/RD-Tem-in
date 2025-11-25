package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SerialBoxPalletMapping;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SerialBoxPalletMapping entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SerialBoxPalletMappingRepository
    extends JpaRepository<SerialBoxPalletMapping, Long> {}
