package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.InboundWMSSession;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InboundWMSSession entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InboundWMSSessionRepository
    extends JpaRepository<InboundWMSSession, Long> {
    @EntityGraph(attributePaths = "inboundWMSPallets")
    Optional<InboundWMSSession> findOneWithEagerRelationshipsById(Long id);

    Page<InboundWMSSession> findAll(Pageable pageable);
}
