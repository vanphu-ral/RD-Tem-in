package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.InboundWMSBox;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InboundWMSBox entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InboundWMSBoxRepository
    extends JpaRepository<InboundWMSBox, Long> {}
