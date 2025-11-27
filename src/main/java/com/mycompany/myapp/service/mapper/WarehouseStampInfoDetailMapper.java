package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.WarehouseNoteInfo;
import com.mycompany.myapp.domain.WarehouseNoteInfoDetail;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDetailDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WarehouseNoteInfoDetail} and its DTO
 * {@link WarehouseStampInfoDetailDTO}.
 */
@Mapper(componentModel = "spring")
public interface WarehouseStampInfoDetailMapper
    extends
        EntityMapper<WarehouseStampInfoDetailDTO, WarehouseNoteInfoDetail> {}
