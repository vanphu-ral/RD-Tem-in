package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.InboundWMSSession;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InboundWMSSession entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InboundWMSSessionRepository
    extends
        JpaRepository<InboundWMSSession, Long>,
        JpaSpecificationExecutor<InboundWMSSession> {
    @EntityGraph(attributePaths = "inboundWMSPallets")
    Optional<InboundWMSSession> findOneWithEagerRelationshipsById(Long id);

    @Override
    Page<InboundWMSSession> findAll(
        org.springframework.data.jpa.domain.Specification<
            InboundWMSSession
        > spec,
        Pageable pageable
    );
}
