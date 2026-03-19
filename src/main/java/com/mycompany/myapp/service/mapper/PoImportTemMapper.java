package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.PoImportTem;
import com.mycompany.myapp.service.dto.PoImportTemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PoImportTem} and its DTO {@link PoImportTemDTO}.
 */
@Mapper(componentModel = "spring")
public interface PoImportTemMapper
    extends EntityMapper<PoImportTemDTO, PoImportTem> {}
