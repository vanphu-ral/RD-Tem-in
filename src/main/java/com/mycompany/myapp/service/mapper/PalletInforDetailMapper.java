package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.PalletInforDetail;
import com.mycompany.myapp.service.dto.PalletInforDetailDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PalletInforDetail} and its DTO {@link PalletInforDetailDTO}.
 */
@Mapper(componentModel = "spring")
public interface PalletInforDetailMapper
    extends EntityMapper<PalletInforDetailDTO, PalletInforDetail> {}
