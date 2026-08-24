package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.ImportVendorTemTransactions;
import com.mycompany.myapp.domain.PoDetail;
import com.mycompany.myapp.domain.PoImportTem;
import com.mycompany.myapp.domain.partner4.SapOitm;
import com.mycompany.myapp.repository.partner4.SapOitmRepository;
import com.mycompany.myapp.repository.partner5.ImportVendorTemTransactionsRepository;
import com.mycompany.myapp.repository.partner5.PoDetailRepository;
import com.mycompany.myapp.repository.partner5.PoImportTemRepository;
import com.mycompany.myapp.service.PoImportTemService;
import com.mycompany.myapp.service.SapPoInfoAggregateService;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDTO;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDetailDTO;
import com.mycompany.myapp.service.dto.PoDetailDTO;
import com.mycompany.myapp.service.dto.PoImportRequestDTO;
import com.mycompany.myapp.service.dto.PoImportResponseDTO;
import com.mycompany.myapp.service.dto.PoImportTemDTO;
import com.mycompany.myapp.service.dto.PoImportTemDetailDTO;
import com.mycompany.myapp.service.dto.PoInfoResponseDTO;
import com.mycompany.myapp.service.mapper.ImportVendorTemTransactionsMapper;
import com.mycompany.myapp.service.mapper.PoDetailMapper;
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

    private final PoDetailMapper poDetailMapper;

    private final SapOitmRepository sapOitmRepository;

    public PoImportTemServiceImpl(
        PoImportTemRepository poImportTemRepository,
        PoImportTemMapper poImportTemMapper,
        ImportVendorTemTransactionsRepository importVendorTemTransactionsRepository,
        PoDetailRepository poDetailRepository,
        ImportVendorTemTransactionsMapper importVendorTemTransactionsMapper,
        SapPoInfoAggregateService sapPoInfoAggregateService,
        PoDetailMapper poDetailMapper,
        SapOitmRepository sapOitmRepository
    ) {
        this.poImportTemRepository = poImportTemRepository;
        this.poImportTemMapper = poImportTemMapper;
        this.importVendorTemTransactionsRepository =
            importVendorTemTransactionsRepository;
        this.poDetailRepository = poDetailRepository;
        this.importVendorTemTransactionsMapper =
            importVendorTemTransactionsMapper;
        this.sapPoInfoAggregateService = sapPoInfoAggregateService;
        this.poDetailMapper = poDetailMapper;
        this.sapOitmRepository = sapOitmRepository;
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
            return createNewPoImport(request, "CASE_1");
        }

        String poNumber = request.getPoNumber();
        String storageUnit = request.getStorageUnit();
        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        List<PoImportTem> parents = poImportTemRepository.findByPoNumber(
            poNumber
        );
        List<ImportVendorTemTransactions> transactions =
            importVendorTemTransactionsRepository.findByPoNumber(poNumber);

        Optional<ImportVendorTemTransactions> matchingTransactionOpt =
            transactions
                .stream()
                .filter(t -> {
                    if (t.getCreatedAt() == null) return false;
                    LocalDate createdDate = t.getCreatedAt().toLocalDate();
                    return (
                        createdDate.equals(today) &&
                        storageUnit != null &&
                        storageUnit.equals(t.getStorageUnit())
                    );
                })
                .findFirst();

        if (matchingTransactionOpt.isPresent()) {
            ImportVendorTemTransactions matchingTransaction =
                matchingTransactionOpt.get();
            PoImportTem parent = findParentForTransaction(
                matchingTransaction,
                parents
            );
            PoImportTemDTO poImportTemDTO = parent != null
                ? poImportTemMapper.toDto(parent)
                : null;
            ImportVendorTemTransactionsDTO transactionDTO =
                importVendorTemTransactionsMapper.toDto(matchingTransaction);

            java.util.List<PoDetailDTO> poDetailDTOs =
                java.util.Collections.emptyList();
            if (
                matchingTransaction.getPoDetails() != null &&
                !matchingTransaction.getPoDetails().isEmpty()
            ) {
                poDetailDTOs = matchingTransaction
                    .getPoDetails()
                    .stream()
                    .map(poDetailMapper::toDto)
                    .collect(java.util.stream.Collectors.toList());
            }

            return new PoImportResponseDTO(
                poImportTemDTO,
                new ImportVendorTemTransactionsDetailDTO(
                    transactionDTO,
                    poDetailDTOs
                ),
                "CASE_2_EXISTING"
            );
        }

        if (parents.isEmpty() && transactions.isEmpty()) {
            return createNewFromSap(request, "CASE_2_CALL_SAP_CREATE_FULL");
        }

        if (!parents.isEmpty()) {
            PoImportTem parent = parents
                .stream()
                .filter(p -> {
                    if (p.getCreatedAt() == null) return false;
                    return p.getCreatedAt().toLocalDate().equals(today);
                })
                .findFirst()
                .orElse(parents.get(0));

            return createChildTransaction(request, parent, "CASE_2_REUSE_PO");
        }

        return createNewFromSap(request, "CASE_2_CALL_SAP_CREATE_FULL");
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

        final ImportVendorTemTransactions savedTransaction = transaction;

        java.util.List<PoDetailDTO> poDetailDTOs =
            java.util.Collections.emptyList();
        if (savedTransaction.getPoNumber() != null) {
            java.util.List<PoDetail> savedDetails = sapPoInfoAggregateService
                .getPoInfoByOporDocEntry(savedTransaction.getPoNumber())
                .getPoDetails()
                .stream()
                .map(poDetail -> {
                    PoDetail detail = new PoDetail();
                    detail.setImportVendorTemTransactions(savedTransaction);
                    detail.setImportVendorTemTransactionsId(
                        savedTransaction.getId()
                    );
                    detail.setSapCode(poDetail.getPor1ItemCode());
                    detail.setSapName(poDetail.getPor1Dscription());

                    String partNumber = null;
                    String itemCode = poDetail.getPor1ItemCode();
                    if (itemCode != null && !itemCode.isEmpty()) {
                        List<SapOitm> oitmList =
                            sapOitmRepository.findByItemCode(itemCode);
                        if (!oitmList.isEmpty()) {
                            partNumber = oitmList.get(0).getuPartNumber();
                        }
                    }
                    detail.setPartNumber(partNumber);

                    try {
                        detail.setTotalQuantity(
                            poDetail.getPor1Quantity() != null
                                ? Integer.parseInt(poDetail.getPor1Quantity())
                                : null
                        );
                    } catch (NumberFormatException e) {
                        detail.setTotalQuantity(null);
                    }
                    return poDetailRepository.save(detail);
                })
                .collect(java.util.stream.Collectors.toList());
            poDetailDTOs = savedDetails
                .stream()
                .map(poDetailMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
        }

        ImportVendorTemTransactionsDTO updatedTransactionDTO =
            importVendorTemTransactionsMapper.toDto(savedTransaction);

        PoImportTemDTO poImportTemDTO = poImportTemRepository
            .findById(savedTransaction.getPoImportTemId())
            .map(poImportTemMapper::toDto)
            .orElse(null);

        return new PoImportResponseDTO(
            poImportTemDTO,
            new ImportVendorTemTransactionsDetailDTO(
                updatedTransactionDTO,
                poDetailDTOs
            ),
            "UPDATE"
        );
    }

    private PoImportResponseDTO createNewPoImport(
        PoImportRequestDTO request,
        String caseType
    ) {
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
            new ImportVendorTemTransactionsDetailDTO(
                transactionDTO,
                java.util.Collections.emptyList()
            ),
            caseType
        );
    }

    private PoImportTem findParentForTransaction(
        ImportVendorTemTransactions transaction,
        List<PoImportTem> parents
    ) {
        Long parentId = transaction.getPoImportTemId();
        if (parentId != null) {
            Optional<PoImportTem> parentOpt = parents
                .stream()
                .filter(p -> parentId.equals(p.getId()))
                .findFirst();
            if (parentOpt.isPresent()) {
                return parentOpt.get();
            }
            return poImportTemRepository.findById(parentId).orElse(null);
        }

        if (!parents.isEmpty()) {
            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            return parents
                .stream()
                .filter(p -> {
                    if (p.getCreatedAt() == null) return false;
                    return p.getCreatedAt().toLocalDate().equals(today);
                })
                .findFirst()
                .orElse(parents.get(0));
        }

        return null;
    }

    private PoImportResponseDTO createNewFromSap(
        PoImportRequestDTO request,
        String caseType
    ) {
        PoInfoResponseDTO sapInfo =
            sapPoInfoAggregateService.getPoInfoByOporDocEntry(
                request.getPoNumber()
            );

        if (
            sapInfo.getPoInfo() == null &&
            (sapInfo.getPoDetails() == null || sapInfo.getPoDetails().isEmpty())
        ) {
            throw new com.mycompany.myapp.web.rest.errors.BadRequestAlertException(
                "PO not found in SAP",
                "poImportTem",
                "ponotfound"
            );
        }

        PoImportTem poImportTem = new PoImportTem();
        poImportTem.setPoNumber(request.getPoNumber());

        if (sapInfo.getPoInfo() != null) {
            PoInfoResponseDTO.PoInfoDTO poInfo = sapInfo.getPoInfo();
            if (poInfo.getOporCardCode() != null) {
                poImportTem.setVendorCode(poInfo.getOporCardCode());
            }
            if (poInfo.getOporCardName() != null) {
                poImportTem.setVendorName(poInfo.getOporCardName());
            }
        }

        if (request.getVendorCode() != null) {
            poImportTem.setVendorCode(request.getVendorCode());
        }
        if (request.getVendorName() != null) {
            poImportTem.setVendorName(request.getVendorName());
        }
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
        transaction.setPoImportTemId(poImportTem.getId());
        transaction.setPoNumber(request.getPoNumber());
        transaction.setVendorCode(poImportTem.getVendorCode());
        transaction.setVendorName(poImportTem.getVendorName());
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

        final ImportVendorTemTransactions savedTransaction = transaction;

        java.util.List<PoDetailDTO> poDetailDTOs =
            java.util.Collections.emptyList();
        if (
            sapInfo.getPoDetails() != null && !sapInfo.getPoDetails().isEmpty()
        ) {
            java.util.List<PoDetail> savedDetails = new java.util.ArrayList<>();
            for (PoInfoResponseDTO.PoDetailDTO poDetail : sapInfo.getPoDetails()) {
                PoDetail detail = new PoDetail();
                detail.setImportVendorTemTransactions(savedTransaction);
                detail.setImportVendorTemTransactionsId(
                    savedTransaction.getId()
                );
                detail.setSapCode(poDetail.getPor1ItemCode());
                detail.setSapName(poDetail.getPor1Dscription());

                String partNumber = null;
                String itemCode = poDetail.getPor1ItemCode();
                if (itemCode != null && !itemCode.isEmpty()) {
                    List<SapOitm> oitmList = sapOitmRepository.findByItemCode(
                        itemCode
                    );
                    if (!oitmList.isEmpty()) {
                        partNumber = oitmList.get(0).getuPartNumber();
                    }
                }
                detail.setPartNumber(partNumber);

                try {
                    detail.setTotalQuantity(
                        poDetail.getPor1Quantity() != null
                            ? Integer.parseInt(poDetail.getPor1Quantity())
                            : null
                    );
                } catch (NumberFormatException e) {
                    detail.setTotalQuantity(null);
                }
                savedDetails.add(poDetailRepository.save(detail));
            }
            poDetailDTOs = savedDetails
                .stream()
                .map(poDetailMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
        }

        PoImportTemDTO poImportTemDTO = poImportTemMapper.toDto(poImportTem);
        ImportVendorTemTransactionsDTO transactionDTO =
            importVendorTemTransactionsMapper.toDto(transaction);

        return new PoImportResponseDTO(
            poImportTemDTO,
            new ImportVendorTemTransactionsDetailDTO(
                transactionDTO,
                poDetailDTOs
            ),
            caseType
        );
    }

    private PoImportResponseDTO createChildTransaction(
        PoImportRequestDTO request,
        PoImportTem parent,
        String caseType
    ) {
        ImportVendorTemTransactions transaction =
            new ImportVendorTemTransactions();
        transaction.setPoImportTem(parent);
        transaction.setPoImportTemId(parent.getId());
        transaction.setPoNumber(request.getPoNumber());
        transaction.setVendorCode(parent.getVendorCode());
        transaction.setVendorName(parent.getVendorName());
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

        final ImportVendorTemTransactions savedTransaction = transaction;

        java.util.List<PoDetailDTO> poDetailDTOs =
            java.util.Collections.emptyList();
        PoInfoResponseDTO sapInfo =
            sapPoInfoAggregateService.getPoInfoByOporDocEntry(
                request.getPoNumber()
            );
        if (
            sapInfo.getPoDetails() != null && !sapInfo.getPoDetails().isEmpty()
        ) {
            java.util.List<PoDetail> savedDetails = new java.util.ArrayList<>();
            for (PoInfoResponseDTO.PoDetailDTO poDetail : sapInfo.getPoDetails()) {
                PoDetail detail = new PoDetail();
                detail.setImportVendorTemTransactions(savedTransaction);
                detail.setImportVendorTemTransactionsId(
                    savedTransaction.getId()
                );
                detail.setSapCode(poDetail.getPor1ItemCode());
                detail.setSapName(poDetail.getPor1Dscription());

                String partNumber = null;
                String itemCode = poDetail.getPor1ItemCode();
                if (itemCode != null && !itemCode.isEmpty()) {
                    List<SapOitm> oitmList = sapOitmRepository.findByItemCode(
                        itemCode
                    );
                    if (!oitmList.isEmpty()) {
                        partNumber = oitmList.get(0).getuPartNumber();
                    }
                }
                detail.setPartNumber(partNumber);

                try {
                    detail.setTotalQuantity(
                        poDetail.getPor1Quantity() != null
                            ? Integer.parseInt(poDetail.getPor1Quantity())
                            : null
                    );
                } catch (NumberFormatException e) {
                    detail.setTotalQuantity(null);
                }
                savedDetails.add(poDetailRepository.save(detail));
            }
            poDetailDTOs = savedDetails
                .stream()
                .map(poDetailMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
        }

        PoImportTemDTO poImportTemDTO = poImportTemMapper.toDto(parent);
        ImportVendorTemTransactionsDTO transactionDTO =
            importVendorTemTransactionsMapper.toDto(transaction);

        return new PoImportResponseDTO(
            poImportTemDTO,
            new ImportVendorTemTransactionsDetailDTO(
                transactionDTO,
                poDetailDTOs
            ),
            caseType
        );
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
