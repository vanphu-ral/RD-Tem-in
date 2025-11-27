package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ProductionTeam;
import com.mycompany.myapp.service.dto.ProductionTeamDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductionTeam} and its DTO
 * {@link ProductionTeamDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductionTeamMapper
    extends EntityMapper<ProductionTeamDTO, ProductionTeam> {
    @Mapping(target = "branch", ignore = true)
    ProductionTeam toEntity(ProductionTeamDTO productionTeamDTO);

    default ProductionTeam fromId(Long id) {
        if (id == null) {
            return null;
        }
        ProductionTeam productionTeam = new ProductionTeam();
        productionTeam.setId(id);
        return productionTeam;
    }
}
