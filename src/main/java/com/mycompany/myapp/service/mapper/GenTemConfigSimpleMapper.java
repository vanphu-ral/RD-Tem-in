package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.GenTemConfig;
import com.mycompany.myapp.domain.WarehouseNoteInfo;
import com.mycompany.myapp.service.dto.GenTemConfigSimpleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GenTemConfig} and its simple DTO
 * {@link GenTemConfigSimpleDTO}.
 */
@Mapper(componentModel = "spring")
public interface GenTemConfigSimpleMapper {
    @Mapping(source = "maLenhSanXuat.id", target = "maLenhSanXuatId")
    GenTemConfigSimpleDTO toDto(GenTemConfig genTemConfig);

    @Mapping(source = "maLenhSanXuatId", target = "maLenhSanXuat")
    GenTemConfig toEntity(GenTemConfigSimpleDTO genTemConfigSimpleDTO);

    default WarehouseNoteInfo mapIdToWarehouseStampInfo(Long id) {
        if (id == null) {
            return null;
        }
        WarehouseNoteInfo warehouseStampInfo = new WarehouseNoteInfo();
        warehouseStampInfo.setId(id);
        return warehouseStampInfo;
    }

    default Long mapWarehouseStampInfoToId(
        WarehouseNoteInfo warehouseStampInfo
    ) {
        if (warehouseStampInfo == null) {
            return null;
        }
        return warehouseStampInfo.getId();
    }
}
