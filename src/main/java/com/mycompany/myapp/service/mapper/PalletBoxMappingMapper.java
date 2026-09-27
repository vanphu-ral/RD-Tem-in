package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.PalletBoxMapping;
import com.mycompany.myapp.domain.PalletMngt;
import com.mycompany.myapp.service.dto.PalletBoxMappingDTO;
import com.mycompany.myapp.service.dto.PalletMngtDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PalletBoxMapping} and its DTO {@link PalletBoxMappingDTO}.
 */
@Mapper(componentModel = "spring")
public interface PalletBoxMappingMapper
  extends EntityMapper<PalletBoxMappingDTO, PalletBoxMapping> {
  @Mapping(
    target = "palletMngt",
    source = "palletMngt",
    qualifiedByName = "palletMngtId"
  )
  PalletBoxMappingDTO toDto(PalletBoxMapping s);

  @Named("palletMngtId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  PalletMngtDTO toDtoPalletMngtId(PalletMngt palletMngt);
}
