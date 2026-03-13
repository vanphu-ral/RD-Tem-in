package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ImportVendorTemTransactions;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ImportVendorTemTransactions} and its DTO {@link ImportVendorTemTransactionsDTO}.
 */
@Mapper(componentModel = "spring")
public interface ImportVendorTemTransactionsMapper
    extends
        EntityMapper<
            ImportVendorTemTransactionsDTO,
            ImportVendorTemTransactions
        > {}
