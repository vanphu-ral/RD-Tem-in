package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.partner4.SapOitm;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.partner4.SapOitm}.
 */
public interface SapOitmService {
    /**
     * Save a sapOitm.
     *
     * @param sapOitm the entity to save.
     * @return the persisted entity.
     */
    SapOitm save(SapOitm sapOitm);

    /**
     * Updates a sapOitm.
     *
     * @param sapOitm the entity to update.
     * @return the persisted entity.
     */
    SapOitm update(SapOitm sapOitm);

    /**
     * Partially updates a sapOitm.
     *
     * @param sapOitm the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SapOitm> partialUpdate(SapOitm sapOitm);

    /**
     * Get all the sapOitms.
     *
     * @return the list of entities.
     */
    List<SapOitm> findAll();

    /**
     * Get the "id" sapOitm.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SapOitm> findOne(Long id);

    /**
     * Delete the "id" sapOitm.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
