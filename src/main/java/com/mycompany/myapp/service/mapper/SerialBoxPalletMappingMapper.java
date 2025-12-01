package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.SerialBoxPalletMapping;
import com.mycompany.myapp.domain.WarehouseNoteInfo;
import com.mycompany.myapp.service.dto.SerialBoxPalletMappingDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SerialBoxPalletMapping} and its DTO
 * {@link SerialBoxPalletMappingDTO}.
 */
@Mapper(componentModel = "spring")
public interface SerialBoxPalletMappingMapper
    extends EntityMapper<SerialBoxPalletMappingDTO, SerialBoxPalletMapping> {
    @Mapping(target = "maLenhSanXuatId", source = "maLenhSanXuat.id")
    SerialBoxPalletMappingDTO toDto(SerialBoxPalletMapping s);

    @Mapping(target = "maLenhSanXuat", ignore = true)
    SerialBoxPalletMapping toEntity(
        SerialBoxPalletMappingDTO serialBoxPalletMappingDTO
    );
}
