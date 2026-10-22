package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.WarehouseTransferDetail;
import com.mycompany.myapp.domain.WarehouseTransferSession;
import com.mycompany.myapp.service.dto.WarehouseTransferDetailDTO;
import com.mycompany.myapp.service.dto.WarehouseTransferSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WarehouseTransferDetail} and its DTO {@link WarehouseTransferDetailDTO}.
 */
@Mapper(componentModel = "spring")
public interface WarehouseTransferDetailMapper
    extends EntityMapper<WarehouseTransferDetailDTO, WarehouseTransferDetail> {
    @Mapping(
        target = "warehouseTransferSession",
        source = "warehouseTransferSession",
        qualifiedByName = "warehouseTransferSessionId"
    )
    WarehouseTransferDetailDTO toDto(WarehouseTransferDetail s);

    @Named("warehouseTransferSessionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WarehouseTransferSessionDTO toDtoWarehouseTransferSessionId(
        WarehouseTransferSession warehouseTransferSession
    );
}
