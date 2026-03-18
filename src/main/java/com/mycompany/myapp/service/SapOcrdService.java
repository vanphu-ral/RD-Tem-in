package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.SapOcrdDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.SapOcrd}.
 */
public interface SapOcrdService {
    /**
     * Save a sapOcrd.
     *
     * @param sapOcrdDTO the entity to save.
     * @return the persisted entity.
     */
    SapOcrdDTO save(SapOcrdDTO sapOcrdDTO);

    /**
     * Updates a sapOcrd.
     *
     * @param sapOcrdDTO the entity to update.
     * @return the persisted entity.
     */
    SapOcrdDTO update(SapOcrdDTO sapOcrdDTO);

    /**
     * Partially updates a sapOcrd.
     *
     * @param sapOcrdDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SapOcrdDTO> partialUpdate(SapOcrdDTO sapOcrdDTO);

    /**
     * Get all the sapOcrds.
     *
     * @return the list of entities.
     */
    List<SapOcrdDTO> findAll();

    /**
     * Get the "id" sapOcrd.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SapOcrdDTO> findOne(Long id);

    /**
     * Delete the "id" sapOcrd.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
