package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.InboundWMSSessionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.InboundWMSSession}.
 */
public interface InboundWMSSessionService {
    /**
     * Save a inboundWMSSession.
     *
     * @param inboundWMSSessionDTO the entity to save.
     * @return the persisted entity.
     */
    InboundWMSSessionDTO save(InboundWMSSessionDTO inboundWMSSessionDTO);

    /**
     * Updates a inboundWMSSession.
     *
     * @param inboundWMSSessionDTO the entity to update.
     * @return the persisted entity.
     */
    InboundWMSSessionDTO update(InboundWMSSessionDTO inboundWMSSessionDTO);

    /**
     * Partially updates a inboundWMSSession.
     *
     * @param inboundWMSSessionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<InboundWMSSessionDTO> partialUpdate(
        InboundWMSSessionDTO inboundWMSSessionDTO
    );

    /**
     * Get all the inboundWMSSessions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InboundWMSSessionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" inboundWMSSession.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<InboundWMSSessionDTO> findOne(Long id);

    /**
     * Delete the "id" inboundWMSSession.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
