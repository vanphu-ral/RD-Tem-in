package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.AttributesType;
import com.mycompany.myapp.domain.RdMaterialAttributes;
import com.mycompany.myapp.service.dto.AttributesTypeDTO;
import com.mycompany.myapp.service.dto.RdMaterialAttributesDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RdMaterialAttributes} and its DTO {@link RdMaterialAttributesDTO}.
 */
@Mapper(componentModel = "spring")
public interface RdMaterialAttributesMapper
    extends EntityMapper<RdMaterialAttributesDTO, RdMaterialAttributes> {
    @Mapping(
        target = "attributesType",
        source = "attributesType",
        qualifiedByName = "attributesTypeId"
    )
    RdMaterialAttributesDTO toDto(RdMaterialAttributes s);

    @Named("attributesTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AttributesTypeDTO toDtoAttributesTypeId(AttributesType attributesType);
}
