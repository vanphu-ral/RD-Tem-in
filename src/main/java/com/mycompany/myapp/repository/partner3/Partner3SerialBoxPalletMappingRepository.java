package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.SerialBoxPalletMapping;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SerialBoxPalletMapping entity using
 * partner3
 * datasource.
 */
@SuppressWarnings("unused")
@Repository
public interface Partner3SerialBoxPalletMappingRepository
    extends JpaRepository<SerialBoxPalletMapping, Long> {}
