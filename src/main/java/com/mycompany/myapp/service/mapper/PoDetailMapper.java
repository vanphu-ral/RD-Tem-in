package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ImportVendorTemTransactions;
import com.mycompany.myapp.domain.PoDetail;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDTO;
import com.mycompany.myapp.service.dto.PoDetailDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PoDetail} and its DTO {@link PoDetailDTO}.
 */
@Mapper(componentModel = "spring")
public interface PoDetailMapper extends EntityMapper<PoDetailDTO, PoDetail> {
    @Mapping(
        target = "importVendorTemTransactions",
        source = "importVendorTemTransactions",
        qualifiedByName = "importVendorTemTransactionsId"
    )
    PoDetailDTO toDto(PoDetail s);

    @Named("importVendorTemTransactionsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ImportVendorTemTransactionsDTO toDtoImportVendorTemTransactionsId(
        ImportVendorTemTransactions importVendorTemTransactions
    );
}
