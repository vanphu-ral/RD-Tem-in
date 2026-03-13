package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.AttributesTypeDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.AttributesType}.
 */
public interface AttributesTypeService {
    /**
     * Save a attributesType.
     *
     * @param attributesTypeDTO the entity to save.
     * @return the persisted entity.
     */
    AttributesTypeDTO save(AttributesTypeDTO attributesTypeDTO);

    /**
     * Updates a attributesType.
     *
     * @param attributesTypeDTO the entity to update.
     * @return the persisted entity.
     */
    AttributesTypeDTO update(AttributesTypeDTO attributesTypeDTO);

    /**
     * Partially updates a attributesType.
     *
     * @param attributesTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AttributesTypeDTO> partialUpdate(
        AttributesTypeDTO attributesTypeDTO
    );

    /**
     * Get the "id" attributesType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AttributesTypeDTO> findOne(Long id);

    /**
     * Delete the "id" attributesType.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
