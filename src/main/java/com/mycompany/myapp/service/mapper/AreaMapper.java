package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Area;
import com.mycompany.myapp.service.dto.AreaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity Area and its DTO AreaDTO.
 */
@Mapper(componentModel = "spring")
public interface AreaMapper extends EntityMapper<AreaDTO, Area> {}
