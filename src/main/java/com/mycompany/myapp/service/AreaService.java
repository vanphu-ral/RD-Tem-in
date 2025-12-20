package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.AreaDTO;
import java.util.List;

/**
 * Service Interface for managing Area.
 */
public interface AreaService {
    /**
     * Get all the areas.
     *
     * @return the list of entities.
     */
    List<AreaDTO> findAll();
}
