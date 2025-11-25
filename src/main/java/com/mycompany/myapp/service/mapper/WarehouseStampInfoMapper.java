package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.WarehouseStampInfo;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WarehouseStampInfo} and its DTO
 * {@link WarehouseStampInfoDTO}.
 */
@Mapper(componentModel = "spring")
public interface WarehouseStampInfoMapper
    extends EntityMapper<WarehouseStampInfoDTO, WarehouseStampInfo> {}
