package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.SerialBoxPalletMapping;
import com.mycompany.myapp.domain.WarehouseStampInfo;
import com.mycompany.myapp.service.dto.SerialBoxPalletMappingDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SerialBoxPalletMapping} and its DTO {@link SerialBoxPalletMappingDTO}.
 */
@Mapper(componentModel = "spring")
public interface SerialBoxPalletMappingMapper
    extends EntityMapper<SerialBoxPalletMappingDTO, SerialBoxPalletMapping> {
    @Mapping(
        target = "maLenhSanXuat",
        source = "maLenhSanXuat",
        qualifiedByName = "warehouseStampInfoId"
    )
    SerialBoxPalletMappingDTO toDto(SerialBoxPalletMapping s);

    @Named("warehouseStampInfoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WarehouseStampInfoDTO toDtoWarehouseStampInfoId(
        WarehouseStampInfo warehouseStampInfo
    );
}
