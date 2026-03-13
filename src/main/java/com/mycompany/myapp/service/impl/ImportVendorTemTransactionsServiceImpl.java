package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.ImportVendorTemTransactions;
import com.mycompany.myapp.repository.partner5.ImportVendorTemTransactionsRepository;
import com.mycompany.myapp.service.ImportVendorTemTransactionsService;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDTO;
import com.mycompany.myapp.service.mapper.ImportVendorTemTransactionsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.ImportVendorTemTransactions}.
 */
@Service
@Transactional
public class ImportVendorTemTransactionsServiceImpl
    implements ImportVendorTemTransactionsService {

    private static final Logger LOG = LoggerFactory.getLogger(
        ImportVendorTemTransactionsServiceImpl.class
    );

    private final ImportVendorTemTransactionsRepository importVendorTemTransactionsRepository;

    private final ImportVendorTemTransactionsMapper importVendorTemTransactionsMapper;

    public ImportVendorTemTransactionsServiceImpl(
        ImportVendorTemTransactionsRepository importVendorTemTransactionsRepository,
        ImportVendorTemTransactionsMapper importVendorTemTransactionsMapper
    ) {
        this.importVendorTemTransactionsRepository =
            importVendorTemTransactionsRepository;
        this.importVendorTemTransactionsMapper =
            importVendorTemTransactionsMapper;
    }

    @Override
    public ImportVendorTemTransactionsDTO save(
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO
    ) {
        LOG.debug(
            "Request to save ImportVendorTemTransactions : {}",
            importVendorTemTransactionsDTO
        );
        ImportVendorTemTransactions importVendorTemTransactions =
            importVendorTemTransactionsMapper.toEntity(
                importVendorTemTransactionsDTO
            );
        importVendorTemTransactions =
            importVendorTemTransactionsRepository.save(
                importVendorTemTransactions
            );
        return importVendorTemTransactionsMapper.toDto(
            importVendorTemTransactions
        );
    }

    @Override
    public ImportVendorTemTransactionsDTO update(
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO
    ) {
        LOG.debug(
            "Request to update ImportVendorTemTransactions : {}",
            importVendorTemTransactionsDTO
        );
        ImportVendorTemTransactions importVendorTemTransactions =
            importVendorTemTransactionsMapper.toEntity(
                importVendorTemTransactionsDTO
            );
        importVendorTemTransactions =
            importVendorTemTransactionsRepository.save(
                importVendorTemTransactions
            );
        return importVendorTemTransactionsMapper.toDto(
            importVendorTemTransactions
        );
    }

    @Override
    public Optional<ImportVendorTemTransactionsDTO> partialUpdate(
        ImportVendorTemTransactionsDTO importVendorTemTransactionsDTO
    ) {
        LOG.debug(
            "Request to partially update ImportVendorTemTransactions : {}",
            importVendorTemTransactionsDTO
        );

        return importVendorTemTransactionsRepository
            .findById(importVendorTemTransactionsDTO.getId())
            .map(existingImportVendorTemTransactions -> {
                importVendorTemTransactionsMapper.partialUpdate(
                    existingImportVendorTemTransactions,
                    importVendorTemTransactionsDTO
                );

                return existingImportVendorTemTransactions;
            })
            .map(importVendorTemTransactionsRepository::save)
            .map(importVendorTemTransactionsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ImportVendorTemTransactionsDTO> findOne(Long id) {
        LOG.debug("Request to get ImportVendorTemTransactions : {}", id);
        return importVendorTemTransactionsRepository
            .findById(id)
            .map(importVendorTemTransactionsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ImportVendorTemTransactions : {}", id);
        importVendorTemTransactionsRepository.deleteById(id);
    }
}
