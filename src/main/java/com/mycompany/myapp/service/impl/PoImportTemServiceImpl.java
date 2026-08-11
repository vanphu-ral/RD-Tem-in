package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.ImportVendorTemTransactions;
import com.mycompany.myapp.domain.PoDetail;
import com.mycompany.myapp.domain.PoImportTem;
import com.mycompany.myapp.repository.partner5.ImportVendorTemTransactionsRepository;
import com.mycompany.myapp.repository.partner5.PoDetailRepository;
import com.mycompany.myapp.repository.partner5.PoImportTemRepository;
import com.mycompany.myapp.service.PoImportTemService;
import com.mycompany.myapp.service.SapPoInfoAggregateService;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDTO;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDetailDTO;
import com.mycompany.myapp.service.dto.PoImportRequestDTO;
import com.mycompany.myapp.service.dto.PoImportResponseDTO;
import com.mycompany.myapp.service.dto.PoImportTemDTO;
import com.mycompany.myapp.service.dto.PoImportTemDetailDTO;
import com.mycompany.myapp.service.mapper.ImportVendorTemTransactionsMapper;
import com.mycompany.myapp.service.mapper.PoImportTemMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PoImportTemServiceImpl implements PoImportTemService {

    private static final Logger LOG = LoggerFactory.getLogger(
        PoImportTemServiceImpl.class
    );

    private final PoImportTemRepository poImportTemRepository;

    private final PoImportTemMapper poImportTemMapper;

    private final ImportVendorTemTransactionsRepository importVendorTemTransactionsRepository;

    private final PoDetailRepository poDetailRepository;

    private final ImportVendorTemTransactionsMapper importVendorTemTransactionsMapper;

    private final SapPoInfoAggregateService sapPoInfoAggregateService;

    public PoImportTemServiceImpl(
        PoImportTemRepository poImportTemRepository,
        PoImportTemMapper poImportTemMapper,
        ImportVendorTemTransactionsRepository importVendorTemTransactionsRepository,
        PoDetailRepository poDetailRepository,
        ImportVendorTemTransactionsMapper importVendorTemTransactionsMapper,
        SapPoInfoAggregateService sapPoInfoAggregateService
    ) {
        this.poImportTemRepository = poImportTemRepository;
        this.poImportTemMapper = poImportTemMapper;
        this.importVendorTemTransactionsRepository =
            importVendorTemTransactionsRepository;
        this.poDetailRepository = poDetailRepository;
        this.importVendorTemTransactionsMapper =
            importVendorTemTransactionsMapper;
        this.sapPoInfoAggregateService = sapPoInfoAggregateService;
    }

    @Override
    public PoImportTemDTO save(PoImportTemDTO poImportTemDTO) {
        LOG.debug("Request to save PoImportTem : {}", poImportTemDTO);
        PoImportTem poImportTem = poImportTemMapper.toEntity(poImportTemDTO);
        poImportTem = poImportTemRepository.save(poImportTem);
        return poImportTemMapper.toDto(poImportTem);
    }

    @Override
    public PoImportTemDTO update(PoImportTemDTO poImportTemDTO) {
        LOG.debug("Request to update PoImportTem : {}", poImportTemDTO);
        PoImportTem poImportTem = poImportTemMapper.toEntity(poImportTemDTO);
        poImportTem = poImportTemRepository.save(poImportTem);
        return poImportTemMapper.toDto(poImportTem);
    }

    @Override
    public Optional<PoImportTemDTO> partialUpdate(
        PoImportTemDTO poImportTemDTO
    ) {
        LOG.debug(
            "Request to partially update PoImportTem : {}",
            poImportTemDTO
        );

        return poImportTemRepository
            .findById(poImportTemDTO.getId())
            .map(existingPoImportTem -> {
                poImportTemMapper.partialUpdate(
                    existingPoImportTem,
                    poImportTemDTO
                );

                return existingPoImportTem;
            })
            .map(poImportTemRepository::save)
            .map(poImportTemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PoImportTemDTO> findOne(Long id) {
        LOG.debug("Request to get PoImportTem : {}", id);
        return poImportTemRepository.findById(id).map(poImportTemMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PoImportTem : {}", id);
        poImportTemRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PoImportTemDetailDTO> findDetailById(Long id) {
        LOG.debug("Request to get PoImportTem detail : {}", id);
        return poImportTemRepository
            .findDetailById(id)
            .map(this::mapToDetailDTO);
    }

    @Override
    public PoImportResponseDTO processPoImport(PoImportRequestDTO request) {
        LOG.debug("Request to process PO Import : {}", request);

        if (request.getPoNumber() == null || request.getPoNumber().isEmpty()) {
            return createNewPoImport(request);
        }

        return findExistingPoImport(request.getPoNumber());
    }

    @Override
    public PoImportResponseDTO processImportVendorTemTransactionUpdate(
        ImportVendorTemTransactionsDTO transactionDTO
    ) {
        LOG.debug(
            "Request to update ImportVendorTemTransaction : {}",
            transactionDTO
        );

        Optional<ImportVendorTemTransactions> transactionOpt =
            importVendorTemTransactionsRepository.findById(
                transactionDTO.getId()
            );

        if (transactionOpt.isEmpty()) {
            LOG.error(
                "ImportVendorTemTransaction not found with id: {}",
                transactionDTO.getId()
            );
            return null;
        }

        ImportVendorTemTransactions transaction = transactionOpt.get();
        transaction.setPoNumber(transactionDTO.getPoNumber());
        transaction.setVendorCode(transactionDTO.getVendorCode());
        transaction.setVendorName(transactionDTO.getVendorName());
        transaction.setStatus(transactionDTO.getStatus());
        transaction.setUpdatedBy(transactionDTO.getUpdatedBy());
        transaction.setUpdatedAt(transactionDTO.getUpdatedAt());

        importVendorTemTransactionsRepository.save(transaction);

        if (transaction.getPoNumber() != null) {
            sapPoInfoAggregateService
                .getPoInfoByOporDocEntry(transaction.getPoNumber())
                .getPoDetails()
                .forEach(poDetail -> {
                    PoDetail detail = new PoDetail();
                    detail.setImportVendorTemTransactions(transaction);
                    detail.setImportVendorTemTransactionsId(
                        transaction.getId()
                    );
                    poDetailRepository.save(detail);
                });
        }

        ImportVendorTemTransactionsDTO updatedTransactionDTO =
            importVendorTemTransactionsMapper.toDto(transaction);

        PoImportTemDTO poImportTemDTO = poImportTemRepository
            .findById(transaction.getPoImportTemId())
            .map(poImportTemMapper::toDto)
            .orElse(null);

        return new PoImportResponseDTO(
            poImportTemDTO,
            new ImportVendorTemTransactionsDetailDTO(updatedTransactionDTO),
            "UPDATE"
        );
    }

    private PoImportResponseDTO createNewPoImport(PoImportRequestDTO request) {
        PoImportTem poImportTem = new PoImportTem();
        poImportTem.setVendorCode(request.getVendorCode());
        poImportTem.setVendorName(request.getVendorName());
        poImportTem.setEntryDate(request.getEntryDate());
        poImportTem.setStatus(request.getStatus());
        poImportTem.setPoComments(request.getNote());
        poImportTem.setCreatedBy(request.getCreatedBy());
        poImportTem.setCreatedAt(request.getCreatedAt());
        poImportTem.setUpdatedBy(request.getUpdatedBy());
        poImportTem.setUpdatedAt(request.getUpdatedAt());

        poImportTem = poImportTemRepository.save(poImportTem);

        ImportVendorTemTransactions transaction =
            new ImportVendorTemTransactions();
        transaction.setPoImportTem(poImportTem);
        transaction.setPoNumber(request.getPoNumber());
        transaction.setVendorCode(request.getVendorCode());
        transaction.setVendorName(request.getVendorName());
        transaction.setEntryDate(request.getEntryDate());
        transaction.setStorageUnit(request.getStorageUnit());
        transaction.setTemIdentificationScenarioId(
            request.getTemIdentificationScenarioId()
        );
        transaction.setMappingConfig(request.getMappingConfig());
        transaction.setStatus(request.getStatus());
        transaction.setNote(request.getNote());
        transaction.setCreatedBy(request.getCreatedBy());
        transaction.setCreatedAt(request.getCreatedAt());
        transaction.setUpdatedBy(request.getUpdatedBy());
        transaction.setUpdatedAt(request.getUpdatedAt());

        transaction = importVendorTemTransactionsRepository.save(transaction);

        PoImportTemDTO poImportTemDTO = poImportTemMapper.toDto(poImportTem);
        ImportVendorTemTransactionsDTO transactionDTO =
            importVendorTemTransactionsMapper.toDto(transaction);

        return new PoImportResponseDTO(
            poImportTemDTO,
            new ImportVendorTemTransactionsDetailDTO(transactionDTO),
            "CASE_1"
        );
    }

    private PoImportResponseDTO findExistingPoImport(String poNumber) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        List<PoImportTem> existingRecords =
            poImportTemRepository.findByPoNumber(poNumber);

        Optional<PoImportTem> todayRecordOpt = existingRecords
            .stream()
            .filter(record -> {
                if (record.getCreatedAt() == null) return false;
                LocalDate createdDate = record.getCreatedAt().toLocalDate();
                return createdDate.equals(today);
            })
            .findFirst();

        if (todayRecordOpt.isPresent()) {
            PoImportTem poImportTem = todayRecordOpt.get();
            PoImportTemDTO poImportTemDTO = poImportTemMapper.toDto(
                poImportTem
            );

            List<ImportVendorTemTransactions> transactions =
                importVendorTemTransactionsRepository.findByPoNumber(poNumber);

            Optional<ImportVendorTemTransactions> todayTransactionOpt =
                transactions
                    .stream()
                    .filter(t -> {
                        if (t.getCreatedAt() == null) return false;
                        LocalDate createdDate = t.getCreatedAt().toLocalDate();
                        return createdDate.equals(today);
                    })
                    .findFirst();

            ImportVendorTemTransactionsDTO transactionDTO = todayTransactionOpt
                .map(importVendorTemTransactionsMapper::toDto)
                .orElse(null);

            return new PoImportResponseDTO(
                poImportTemDTO,
                new ImportVendorTemTransactionsDetailDTO(transactionDTO),
                "CASE_2"
            );
        }

        return null;
    }

    private PoImportTemDetailDTO mapToDetailDTO(PoImportTem poImportTem) {
        PoImportTemDetailDTO detailDTO = new PoImportTemDetailDTO();
        detailDTO.setId(poImportTem.getId());
        detailDTO.setPoNumber(poImportTem.getPoNumber());
        detailDTO.setVendorCode(poImportTem.getVendorCode());
        detailDTO.setVendorName(poImportTem.getVendorName());
        detailDTO.setEntryDate(poImportTem.getEntryDate());
        detailDTO.setQuantityContainer(poImportTem.getQuantityContainer());
        detailDTO.setTotalQuantity(poImportTem.getTotalQuantity());
        detailDTO.setStatus(poImportTem.getStatus());
        detailDTO.setPoComments(poImportTem.getPoComments());
        detailDTO.setCreatedBy(poImportTem.getCreatedBy());
        detailDTO.setCreatedAt(poImportTem.getCreatedAt());
        detailDTO.setUpdatedBy(poImportTem.getUpdatedBy());
        detailDTO.setUpdatedAt(poImportTem.getUpdatedAt());
        detailDTO.setDeletedBy(poImportTem.getDeletedBy());
        detailDTO.setDeletedAt(poImportTem.getDeletedAt());

        if (poImportTem.getImportVendorTemTransactions() != null) {
            Set<ImportVendorTemTransactionsDTO> transactionDTOs =
                new java.util.HashSet<>();
            for (ImportVendorTemTransactions transaction : poImportTem.getImportVendorTemTransactions()) {
                transactionDTOs.add(
                    importVendorTemTransactionsMapper.toDto(transaction)
                );
            }
            detailDTO.setImportVendorTemTransactions(transactionDTOs);
        }

        return detailDTO;
    }
}
