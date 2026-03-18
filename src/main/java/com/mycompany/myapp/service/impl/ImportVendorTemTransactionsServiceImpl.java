package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.ImportVendorTemTransactions;
import com.mycompany.myapp.domain.PoDetail;
import com.mycompany.myapp.repository.partner5.ImportVendorTemTransactionsRepository;
import com.mycompany.myapp.repository.partner5.PoDetailRepository;
import com.mycompany.myapp.service.ImportVendorTemTransactionsService;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDTO;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDetailDTO;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsWithPoDetailsRequestDTO;
import com.mycompany.myapp.service.dto.PoDetailDTO;
import com.mycompany.myapp.service.mapper.ImportVendorTemTransactionsMapper;
import com.mycompany.myapp.service.mapper.PoDetailMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link com.mycompany.myapp.domain.ImportVendorTemTransactions}.
 */
@Service
@Transactional
public class ImportVendorTemTransactionsServiceImpl
    implements ImportVendorTemTransactionsService {

    private static final Logger LOG = LoggerFactory.getLogger(
        ImportVendorTemTransactionsServiceImpl.class
    );

    private final ImportVendorTemTransactionsRepository importVendorTemTransactionsRepository;

    private final PoDetailRepository poDetailRepository;

    private final ImportVendorTemTransactionsMapper importVendorTemTransactionsMapper;

    private final PoDetailMapper poDetailMapper;

    public ImportVendorTemTransactionsServiceImpl(
        ImportVendorTemTransactionsRepository importVendorTemTransactionsRepository,
        PoDetailRepository poDetailRepository,
        ImportVendorTemTransactionsMapper importVendorTemTransactionsMapper,
        PoDetailMapper poDetailMapper
    ) {
        this.importVendorTemTransactionsRepository =
            importVendorTemTransactionsRepository;
        this.poDetailRepository = poDetailRepository;
        this.importVendorTemTransactionsMapper =
            importVendorTemTransactionsMapper;
        this.poDetailMapper = poDetailMapper;
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
    @Transactional(readOnly = true)
    public Optional<ImportVendorTemTransactionsDetailDTO> findOneWithDetails(
        Long id
    ) {
        LOG.debug(
            "Request to get ImportVendorTemTransactions with details : {}",
            id
        );
        return importVendorTemTransactionsRepository
            .findById(id)
            .map(importVendorTemTransactions -> {
                ImportVendorTemTransactionsDTO transactionDTO =
                    importVendorTemTransactionsMapper.toDto(
                        importVendorTemTransactions
                    );

                // Fetch poDetails for this transaction
                List<PoDetail> poDetails = new ArrayList<>();
                // Check if poDetails are already loaded
                if (
                    importVendorTemTransactions.getPoDetails() != null &&
                    !importVendorTemTransactions.getPoDetails().isEmpty()
                ) {
                    poDetails = new ArrayList<>(
                        importVendorTemTransactions.getPoDetails()
                    );
                } else {
                    // Fetch from repository if not loaded
                    poDetails =
                        poDetailRepository.findByImportVendorTemTransactionsId(
                            id
                        );
                }
                List<PoDetailDTO> poDetailDTOs = poDetailMapper.toDto(
                    poDetails
                );

                return new ImportVendorTemTransactionsDetailDTO(
                    transactionDTO,
                    poDetailDTOs
                );
            });
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ImportVendorTemTransactions : {}", id);
        importVendorTemTransactionsRepository.deleteById(id);
    }

    @Override
    public ImportVendorTemTransactionsDetailDTO createWithPoDetails(
        ImportVendorTemTransactionsWithPoDetailsRequestDTO requestDTO
    ) {
        LOG.debug(
            "Request to create ImportVendorTemTransactions with PoDetails : {}",
            requestDTO
        );

        // 1. Save the ImportVendorTemTransactions first
        ImportVendorTemTransactionsDTO transactionDTO =
            requestDTO.getTransaction();
        ImportVendorTemTransactions importVendorTemTransactions =
            importVendorTemTransactionsMapper.toEntity(transactionDTO);
        importVendorTemTransactions =
            importVendorTemTransactionsRepository.save(
                importVendorTemTransactions
            );
        ImportVendorTemTransactionsDTO savedTransactionDTO =
            importVendorTemTransactionsMapper.toDto(
                importVendorTemTransactions
            );

        // 2. Save PoDetails and link them to the transaction
        List<PoDetailDTO> poDetailDTOs = requestDTO.getPoDetails();
        List<PoDetail> savedPoDetails = new ArrayList<>();

        if (poDetailDTOs != null && !poDetailDTOs.isEmpty()) {
            for (PoDetailDTO poDetailDTO : poDetailDTOs) {
                // Set the foreign key
                poDetailDTO.setImportVendorTemTransactionsId(
                    importVendorTemTransactions.getId()
                );

                // Convert to entity
                PoDetail poDetail = poDetailMapper.toEntity(poDetailDTO);
                poDetail.setImportVendorTemTransactionsId(
                    importVendorTemTransactions.getId()
                );
                poDetail.setImportVendorTemTransactions(
                    importVendorTemTransactions
                );

                // Save
                poDetail = poDetailRepository.save(poDetail);
                savedPoDetails.add(poDetail);
            }
        }

        // 3. Return the detail DTO with both transaction and poDetails
        List<PoDetailDTO> savedPoDetailDTOs = poDetailMapper.toDto(
            savedPoDetails
        );
        return new ImportVendorTemTransactionsDetailDTO(
            savedTransactionDTO,
            savedPoDetailDTOs
        );
    }
}
