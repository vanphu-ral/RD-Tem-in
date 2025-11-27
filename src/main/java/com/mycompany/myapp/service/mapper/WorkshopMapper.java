package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Workshop;
import com.mycompany.myapp.service.dto.WorkshopDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Workshop} and its DTO {@link WorkshopDTO}.
 */
@Mapper(componentModel = "spring")
public interface WorkshopMapper extends EntityMapper<WorkshopDTO, Workshop> {}
