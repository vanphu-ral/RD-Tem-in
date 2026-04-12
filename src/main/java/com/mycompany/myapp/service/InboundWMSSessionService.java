package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.partner3.InboundWMSPalletRepository;
import com.mycompany.myapp.service.criteria.InboundWMSSessionCriteria;
import com.mycompany.myapp.service.dto.InboundWMSPalletDTO;
import com.mycompany.myapp.service.dto.InboundWMSSessionDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Interface for managing
 * {@link com.mycompany.myapp.domain.InboundWMSSession}.
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
    Page<InboundWMSSessionDTO> findAll(
        InboundWMSSessionCriteria criteria,
        Pageable pageable
    );
    // Page<InboundWMSSessionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" inboundWMSSession.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<InboundWMSSessionDTO> findOne(Long id);

    /**
     * Get the "id" inboundWMSSession with its pallets and boxes.
     *
     * @param id the id of the entity.
     * @return the entity with pallets and boxes.
     */
    Optional<InboundWMSSessionDTO> findOneWithPallets(Long id);

    /**
     * Delete the "id" inboundWMSSession.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get all the inboundWMSPallets by inboundWMSSessionId.
     *
     * @param sessionId the id of the inboundWMSSession.
     * @return the list of entities.
     */
    List<InboundWMSPalletDTO> findPalletsBySessionId(Long sessionId);

    /**
     * Submit warehouse entry approval for the session.
     *
     * @param sessionId the id of the inboundWMSSession.
     */

    void submitWarehouseEntryApproval(Long sessionId);
    /**
     * Get grouped inbound WMS sessions by work order code.
     *
     * @param workOrderCode the work order code.
     * @return the list of grouped inbound WMS sessions.
     */
    List<InboundWMSSessionDTO> findGroupedSessionsByWorkOrderCode(
        String workOrderCode
    );
}
