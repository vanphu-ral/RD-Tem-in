package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.InboundWMSPalletDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.InboundWMSPallet}.
 */
public interface InboundWMSPalletService {
    /**
     * Save a inboundWMSPallet.
     *
     * @param inboundWMSPalletDTO the entity to save.
     * @return the persisted entity.
     */
    InboundWMSPalletDTO save(InboundWMSPalletDTO inboundWMSPalletDTO);

    /**
     * Updates a inboundWMSPallet.
     *
     * @param inboundWMSPalletDTO the entity to update.
     * @return the persisted entity.
     */
    InboundWMSPalletDTO update(InboundWMSPalletDTO inboundWMSPalletDTO);

    /**
     * Partially updates a inboundWMSPallet.
     *
     * @param inboundWMSPalletDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<InboundWMSPalletDTO> partialUpdate(
        InboundWMSPalletDTO inboundWMSPalletDTO
    );

    /**
     * Get all the inboundWMSPallets.
     *
     * @return the list of entities.
     */
    List<InboundWMSPalletDTO> findAll();

    /**
     * Get the "id" inboundWMSPallet.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<InboundWMSPalletDTO> findOne(Long id);

    /**
     * Delete the "id" inboundWMSPallet.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
