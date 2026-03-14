package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.RdMaterialAttributesDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.RdMaterialAttributes}.
 */
public interface RdMaterialAttributesService {
    /**
     * Save a rdMaterialAttributes.
     *
     * @param rdMaterialAttributesDTO the entity to save.
     * @return the persisted entity.
     */
    RdMaterialAttributesDTO save(
        RdMaterialAttributesDTO rdMaterialAttributesDTO
    );

    /**
     * Updates a rdMaterialAttributes.
     *
     * @param rdMaterialAttributesDTO the entity to update.
     * @return the persisted entity.
     */
    RdMaterialAttributesDTO update(
        RdMaterialAttributesDTO rdMaterialAttributesDTO
    );

    /**
     * Partially updates a rdMaterialAttributes.
     *
     * @param rdMaterialAttributesDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RdMaterialAttributesDTO> partialUpdate(
        RdMaterialAttributesDTO rdMaterialAttributesDTO
    );

    /**
     * Get the "id" rdMaterialAttributes.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RdMaterialAttributesDTO> findOne(Long id);

    /**
     * Delete the "id" rdMaterialAttributes.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
