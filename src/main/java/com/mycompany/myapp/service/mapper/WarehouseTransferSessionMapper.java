package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.WarehouseTransferSession;
import com.mycompany.myapp.service.dto.WarehouseTransferSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WarehouseTransferSession} and its DTO {@link WarehouseTransferSessionDTO}.
 */
@Mapper(componentModel = "spring")
public interface WarehouseTransferSessionMapper
    extends
        EntityMapper<WarehouseTransferSessionDTO, WarehouseTransferSession> {}
