package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.GenTemConfig;
import com.mycompany.myapp.domain.WarehouseNoteInfo;
import com.mycompany.myapp.service.dto.GenTemConfigDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GenTemConfig} and its DTO
 * {@link GenTemConfigDTO}.
 */
@Mapper(componentModel = "spring")
public interface GenTemConfigMapper
    extends EntityMapper<GenTemConfigDTO, GenTemConfig> {
    @Mapping(source = "maLenhSanXuat.id", target = "maLenhSanXuatId")
    GenTemConfigDTO toDto(GenTemConfig genTemConfig);

    @Mapping(source = "maLenhSanXuatId", target = "maLenhSanXuat")
    GenTemConfig toEntity(GenTemConfigDTO genTemConfigDTO);

    default WarehouseNoteInfo mapIdToWarehouseNoteInfo(Long id) {
        if (id == null) {
            return null;
        }
        WarehouseNoteInfo warehouseNoteInfo = new WarehouseNoteInfo();
        warehouseNoteInfo.setId(id);
        return warehouseNoteInfo;
    }

    default Long mapWarehouseNoteInfoToId(WarehouseNoteInfo warehouseNoteInfo) {
        if (warehouseNoteInfo == null) {
            return null;
        }
        return warehouseNoteInfo.getId();
    }
}
