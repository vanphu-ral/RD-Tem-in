package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.AttributesType;
import com.mycompany.myapp.service.dto.AttributesTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AttributesType} and its DTO {@link AttributesTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface AttributesTypeMapper
    extends EntityMapper<AttributesTypeDTO, AttributesType> {}
