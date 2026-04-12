package com.mycompany.myapp.repository.partner3;

import com.mycompany.myapp.domain.InboundWMSBox;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InboundWMSBox entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InboundWMSBoxRepository
    extends JpaRepository<InboundWMSBox, Long> {
    List<InboundWMSBox> findByInboundWMSSessionId(Integer sessionId);
}
