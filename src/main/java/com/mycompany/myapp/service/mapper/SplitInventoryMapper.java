package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.SplitInventory;
import com.mycompany.myapp.service.dto.SplitInventoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SplitInventory} and its DTO {@link SplitInventoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface SplitInventoryMapper
    extends EntityMapper<SplitInventoryDTO, SplitInventory> {}
