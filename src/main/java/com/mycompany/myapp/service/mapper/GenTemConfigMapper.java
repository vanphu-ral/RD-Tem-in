package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.GenTemConfig;
import com.mycompany.myapp.domain.WarehouseNoteInfo;
import com.mycompany.myapp.service.dto.GenTemConfigDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GenTemConfig} and its DTO
 * {@link GenTemConfigDTO}.
 */
@Mapper(componentModel = "spring")
public interface GenTemConfigMapper
    extends EntityMapper<GenTemConfigDTO, GenTemConfig> {
    @Mapping(
        target = "maLenhSanXuat",
        source = "maLenhSanXuat",
        qualifiedByName = "warehouseStampInfoId"
    )
    GenTemConfigDTO toDto(GenTemConfig s);

    @Named("warehouseStampInfoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WarehouseStampInfoDTO toDtoWarehouseStampInfoId(
        WarehouseNoteInfo warehouseStampInfo
    );
}
