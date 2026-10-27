package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ScanPallet;
import com.mycompany.myapp.service.dto.ScanPalletDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ScanPallet} and its DTO {@link ScanPalletDTO}.
 */
@Mapper(componentModel = "spring")
public interface ScanPalletMapper
    extends EntityMapper<ScanPalletDTO, ScanPallet> {}
