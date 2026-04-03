package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.InboundWMSSession;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InboundWMSSession entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InboundWMSSessionRepository
    extends JpaRepository<InboundWMSSession, Long> {}
