package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.PalletMngt;
import com.mycompany.myapp.service.dto.PalletMngtDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PalletMngt} and its DTO {@link PalletMngtDTO}.
 */
@Mapper(componentModel = "spring")
public interface PalletMngtMapper
  extends EntityMapper<PalletMngtDTO, PalletMngt> {}
