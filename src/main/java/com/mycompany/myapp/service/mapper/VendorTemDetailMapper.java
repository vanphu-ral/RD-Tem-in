package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.PoDetail;
import com.mycompany.myapp.domain.VendorTemDetail;
import com.mycompany.myapp.service.dto.PoDetailDTO;
import com.mycompany.myapp.service.dto.VendorTemDetailDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VendorTemDetail} and its DTO {@link VendorTemDetailDTO}.
 */
@Mapper(componentModel = "spring")
public interface VendorTemDetailMapper
    extends EntityMapper<VendorTemDetailDTO, VendorTemDetail> {
    @Mapping(
        target = "poDetail",
        source = "poDetail",
        qualifiedByName = "poDetailId"
    )
    VendorTemDetailDTO toDto(VendorTemDetail s);

    @Named("poDetailId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PoDetailDTO toDtoPoDetailId(PoDetail poDetail);
}
