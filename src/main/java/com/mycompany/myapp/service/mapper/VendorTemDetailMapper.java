package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.VendorTemDetail;
import com.mycompany.myapp.service.dto.VendorTemDetailDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VendorTemDetail} and its DTO
 * {@link VendorTemDetailDTO}.
 */
@Mapper(componentModel = "spring")
public interface VendorTemDetailMapper
    extends EntityMapper<VendorTemDetailDTO, VendorTemDetail> {}
