package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.InboundWMSPallet;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InboundWMSPallet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InboundWMSPalletRepository
    extends JpaRepository<InboundWMSPallet, Long> {}
