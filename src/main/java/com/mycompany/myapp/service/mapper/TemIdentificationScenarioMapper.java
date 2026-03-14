package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.TemIdentificationScenario;
import com.mycompany.myapp.service.dto.TemIdentificationScenarioDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TemIdentificationScenario} and its DTO {@link TemIdentificationScenarioDTO}.
 */
@Mapper(componentModel = "spring")
public interface TemIdentificationScenarioMapper
    extends
        EntityMapper<TemIdentificationScenarioDTO, TemIdentificationScenario> {}
