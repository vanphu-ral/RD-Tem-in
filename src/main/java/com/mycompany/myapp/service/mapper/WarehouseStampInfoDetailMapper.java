package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.WarehouseStampInfo;
import com.mycompany.myapp.domain.WarehouseStampInfoDetail;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDetailDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WarehouseStampInfoDetail} and its DTO {@link WarehouseStampInfoDetailDTO}.
 */
@Mapper(componentModel = "spring")
public interface WarehouseStampInfoDetailMapper
    extends
        EntityMapper<WarehouseStampInfoDetailDTO, WarehouseStampInfoDetail> {
    @Mapping(
        target = "maLenhSanXuat",
        source = "maLenhSanXuat",
        qualifiedByName = "warehouseStampInfoId"
    )
    WarehouseStampInfoDetailDTO toDto(WarehouseStampInfoDetail s);

    @Named("warehouseStampInfoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WarehouseStampInfoDTO toDtoWarehouseStampInfoId(
        WarehouseStampInfo warehouseStampInfo
    );
}
