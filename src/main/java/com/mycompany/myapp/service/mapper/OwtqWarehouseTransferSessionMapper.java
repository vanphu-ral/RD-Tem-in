package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.OwtqWarehouseTransferSession;
import com.mycompany.myapp.domain.WarehouseTransferSession;
import com.mycompany.myapp.service.dto.OwtqWarehouseTransferSessionDTO;
import com.mycompany.myapp.service.dto.WarehouseTransferSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OwtqWarehouseTransferSession} and its DTO {@link OwtqWarehouseTransferSessionDTO}.
 */
@Mapper(componentModel = "spring")
public interface OwtqWarehouseTransferSessionMapper
    extends
        EntityMapper<
            OwtqWarehouseTransferSessionDTO,
            OwtqWarehouseTransferSession
        > {
    @Mapping(
        target = "warehouseTransferSession",
        source = "warehouseTransferSession",
        qualifiedByName = "warehouseTransferSessionId"
    )
    OwtqWarehouseTransferSessionDTO toDto(OwtqWarehouseTransferSession s);

    @Named("warehouseTransferSessionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WarehouseTransferSessionDTO toDtoWarehouseTransferSessionId(
        WarehouseTransferSession warehouseTransferSession
    );
}
