package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.ImportVendorTemTransactions;
import com.mycompany.myapp.domain.PoDetail;
import com.mycompany.myapp.domain.PoImportTem;
import com.mycompany.myapp.domain.SapPoInfo;
import com.mycompany.myapp.domain.VendorTemDetail;
import com.mycompany.myapp.repository.partner5.ImportVendorTemTransactionsRepository;
import com.mycompany.myapp.repository.partner5.PoDetailRepository;
import com.mycompany.myapp.repository.partner5.PoImportTemRepository;
import com.mycompany.myapp.repository.partner5.VendorTemDetailRepository;
import com.mycompany.myapp.repository.partner6.SapPoInfoRepository;
import com.mycompany.myapp.service.ImportVendorTemTransactionsService;
import com.mycompany.myapp.service.PoImportTemService;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDTO;
import com.mycompany.myapp.service.dto.ImportVendorTemTransactionsDetailDTO;
import com.mycompany.myapp.service.dto.PoDetailDTO;
import com.mycompany.myapp.service.dto.PoImportRequestDTO;
import com.mycompany.myapp.service.dto.PoImportResponseDTO;
import com.mycompany.myapp.service.dto.PoImportTemDTO;
import com.mycompany.myapp.service.dto.PoImportTemDetailDTO;
import com.mycompany.myapp.service.dto.VendorTemDetailDTO;
import com.mycompany.myapp.service.mapper.ImportVendorTemTransactionsMapper;
import com.mycompany.myapp.service.mapper.PoDetailMapper;
import com.mycompany.myapp.service.mapper.PoImportTemMapper;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link com.mycompany.myapp.domain.PoImportTem}.
 */
@Service
@Transactional
public class PoImportTemServiceImpl implements PoImportTemService {

    private static final Logger LOG = LoggerFactory.getLogger(
        PoImportTemServiceImpl.class
    );

    private final PoImportTemRepository poImportTemRepository;

    private final ImportVendorTemTransactionsRepository importVendorTemTransactionsRepository;

    private final PoDetailRepository poDetailRepository;

    private final VendorTemDetailRepository vendorTemDetailRepository;

    private final SapPoInfoRepository sapPoInfoRepository;

    private final PoImportTemMapper poImportTemMapper;

    private final ImportVendorTemTransactionsMapper importVendorTemTransactionsMapper;

    private final PoDetailMapper poDetailMapper;

    public PoImportTemServiceImpl(
        PoImportTemRepository poImportTemRepository,
        ImportVendorTemTransactionsRepository importVendorTemTransactionsRepository,
        PoDetailRepository poDetailRepository,
        VendorTemDetailRepository vendorTemDetailRepository,
        SapPoInfoRepository sapPoInfoRepository,
        PoImportTemMapper poImportTemMapper,
        ImportVendorTemTransactionsMapper importVendorTemTransactionsMapper,
        PoDetailMapper poDetailMapper
    ) {
        this.poImportTemRepository = poImportTemRepository;
        this.importVendorTemTransactionsRepository =
            importVendorTemTransactionsRepository;
        this.poDetailRepository = poDetailRepository;
        this.vendorTemDetailRepository = vendorTemDetailRepository;
        this.sapPoInfoRepository = sapPoInfoRepository;
        this.poImportTemMapper = poImportTemMapper;
        this.importVendorTemTransactionsMapper =
            importVendorTemTransactionsMapper;
        this.poDetailMapper = poDetailMapper;
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
    @Transactional(readOnly = true)
    public Optional<PoImportTemDetailDTO> findDetailById(Long id) {
        LOG.debug("Request to get PoImportTem detail : {}", id);
        return poImportTemRepository
            .findDetailById(id)
            .map(this::mapToDetailDTO);
    }

    /**
     * Map PoImportTem entity to PoImportTemDetailDTO with all nested relationships.
     */
    private PoImportTemDetailDTO mapToDetailDTO(PoImportTem poImportTem) {
        PoImportTemDetailDTO dto = new PoImportTemDetailDTO();
        dto.setId(poImportTem.getId());
        dto.setPoNumber(poImportTem.getPoNumber());
        dto.setVendorCode(poImportTem.getVendorCode());
        dto.setVendorName(poImportTem.getVendorName());
        dto.setEntryDate(poImportTem.getEntryDate());
        dto.setStorageUnit(poImportTem.getStorageUnit());
        dto.setQuantityContainer(poImportTem.getQuantityContainer());
        dto.setTotalQuantity(poImportTem.getTotalQuantity());
        dto.setStatus(poImportTem.getStatus());
        dto.setCreatedBy(poImportTem.getCreatedBy());
        dto.setCreatedAt(poImportTem.getCreatedAt());
        dto.setUpdatedBy(poImportTem.getUpdatedBy());
        dto.setUpdatedAt(poImportTem.getUpdatedAt());
        dto.setDeletedBy(poImportTem.getDeletedBy());
        dto.setDeletedAt(poImportTem.getDeletedAt());

        // Map nested relationships
        if (poImportTem.getImportVendorTemTransactions() != null) {
            Set<ImportVendorTemTransactionsDTO> transactionDTOs =
                new HashSet<>();
            for (ImportVendorTemTransactions transaction : poImportTem.getImportVendorTemTransactions()) {
                ImportVendorTemTransactionsDTO transDTO =
                    importVendorTemTransactionsMapper.toDto(transaction);
                transDTO.setPoImportTemId(transaction.getPoImportTemId());

                // Map poDetails
                if (transaction.getPoDetails() != null) {
                    Set<PoDetailDTO> poDetailDTOs = new HashSet<>();
                    for (PoDetail poDetail : transaction.getPoDetails()) {
                        PoDetailDTO pdDTO = poDetailMapper.toDto(poDetail);

                        // Map vendorTemDetails
                        if (poDetail.getVendorTemDetails() != null) {
                            Set<VendorTemDetailDTO> vendorTemDetailDTOs =
                                new HashSet<>();
                            for (VendorTemDetail vendorTemDetail : poDetail.getVendorTemDetails()) {
                                vendorTemDetailDTOs.add(
                                    mapToVendorTemDetailDTO(vendorTemDetail)
                                );
                            }
                            pdDTO.setVendorTemDetails(vendorTemDetailDTOs);
                        }

                        poDetailDTOs.add(pdDTO);
                    }
                    transDTO.setPoDetails(poDetailDTOs);
                }

                transactionDTOs.add(transDTO);
            }
            dto.setImportVendorTemTransactions(transactionDTOs);
        }

        return dto;
    }

    /**
     * Map VendorTemDetail entity to DTO manually.
     */
    private VendorTemDetailDTO mapToVendorTemDetailDTO(VendorTemDetail entity) {
        VendorTemDetailDTO dto = new VendorTemDetailDTO();
        dto.setId(entity.getId());
        dto.setPoDetailId(entity.getPoDetailId());
        dto.setImportVendorTemTransactionsId(
            entity.getImportVendorTemTransactionsId()
        );
        dto.setReelId(entity.getReelId());
        dto.setPartNumber(entity.getPartNumber());
        dto.setVendor(entity.getVendor());
        dto.setLot(entity.getLot());
        dto.setUserData1(entity.getUserData1());
        dto.setUserData2(entity.getUserData2());
        dto.setUserData3(entity.getUserData3());
        dto.setUserData4(entity.getUserData4());
        dto.setUserData5(entity.getUserData5());
        dto.setInitialQuantity(entity.getInitialQuantity());
        dto.setMsdLevel(entity.getMsdLevel());
        dto.setMsdInitialFloorTime(entity.getMsdInitialFloorTime());
        dto.setMsdBagSealDate(entity.getMsdBagSealDate());
        dto.setMarketUsage(entity.getMarketUsage());
        dto.setQuantityOverride(entity.getQuantityOverride());
        dto.setShelfTime(entity.getShelfTime());
        dto.setSpMaterialName(entity.getSpMaterialName());
        dto.setWarningLimit(entity.getWarningLimit());
        dto.setMaximumLimit(entity.getMaximumLimit());
        dto.setComments(entity.getComments());
        dto.setWarmupTime(entity.getWarmupTime());
        dto.setStorageUnit(entity.getStorageUnit());
        dto.setSubStorageUnit(entity.getSubStorageUnit());
        dto.setLocationOverride(entity.getLocationOverride());
        dto.setExpirationDate(entity.getExpirationDate());
        dto.setManufacturingDate(entity.getManufacturingDate());
        dto.setPartClass(entity.getPartClass());
        dto.setSapCode(entity.getSapCode());
        dto.setVendorQrCode(entity.getVendorQrCode());
        dto.setStatus(entity.getStatus());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PoImportTem : {}", id);

        // Find PoImportTem with all relationships loaded
        PoImportTem poImportTem = poImportTemRepository
            .findWithTransactionsById(id)
            .orElseThrow(() ->
                new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "PoImportTem not found with id: " + id
                )
            );

        // Delete in correct order: children first (VendorTemDetail -> PoDetail ->
        // ImportVendorTemTransactions -> PoImportTem)
        if (poImportTem.getImportVendorTemTransactions() != null) {
            for (ImportVendorTemTransactions transaction : poImportTem.getImportVendorTemTransactions()) {
                if (transaction.getPoDetails() != null) {
                    for (PoDetail poDetail : transaction.getPoDetails()) {
                        // Delete all VendorTemDetail records for this PoDetail
                        if (
                            poDetail.getVendorTemDetails() != null &&
                            !poDetail.getVendorTemDetails().isEmpty()
                        ) {
                            vendorTemDetailRepository.deleteAll(
                                poDetail.getVendorTemDetails()
                            );
                        }
                    }
                    // Delete all PoDetail records for this transaction
                    poDetailRepository.deleteAll(transaction.getPoDetails());
                }
            }
            // Delete all ImportVendorTemTransactions records
            importVendorTemTransactionsRepository.deleteAll(
                poImportTem.getImportVendorTemTransactions()
            );
        }

        // Finally delete the PoImportTem
        poImportTemRepository.delete(poImportTem);
    }

    @Override
    public PoImportResponseDTO processPoImport(PoImportRequestDTO request) {
        LOG.debug("Request to process PO Import : {}", request);

        String poNumber = request.getPoNumber();
        String storageUnit = request.getStorageUnit();
        // Case 1: poNumber is null - create parent and child records
        if (poNumber == null || poNumber.trim().isEmpty()) {
            return handleCase1(request);
        }

        // Case 2: poNumber exists - search for existing records
        return handleCase2(request, poNumber, storageUnit);
    }

    /**
     * Case 1: If poNumber is null, create a parent record in the po_import_tem
     * table
     * and a child record in the import_vendor_tem_transactions table.
     */
    private PoImportResponseDTO handleCase1(PoImportRequestDTO request) {
        LOG.debug("Processing Case 1: poNumber is null");

        // 1. Create parent record in po_import_tem
        PoImportTem poImportTem = new PoImportTem();
        poImportTem.setPoNumber(request.getPoNumber());
        poImportTem.setVendorCode(request.getVendorCode());
        poImportTem.setVendorName(request.getVendorName());
        poImportTem.setEntryDate(request.getEntryDate());
        poImportTem.setStorageUnit(request.getStorageUnit());
        poImportTem.setStatus(request.getStatus());
        poImportTem.setCreatedBy(request.getCreatedBy());
        poImportTem.setCreatedAt(
            request.getCreatedAt() != null
                ? request.getCreatedAt()
                : ZonedDateTime.now()
        );
        poImportTem.setUpdatedBy(request.getUpdatedBy());
        poImportTem.setUpdatedAt(request.getUpdatedAt());
        poImportTem.setDeletedBy(request.getDeletedBy());
        poImportTem.setDeletedAt(request.getDeletedAt());

        poImportTem = poImportTemRepository.save(poImportTem);
        PoImportTemDTO savedPoImportTem = poImportTemMapper.toDto(poImportTem);

        // 2. Create child record in import_vendor_tem_transactions
        ImportVendorTemTransactions vendorTransaction =
            new ImportVendorTemTransactions();
        vendorTransaction.setPoNumber(request.getPoNumber());
        vendorTransaction.setVendorCode(request.getVendorCode());
        vendorTransaction.setVendorName(request.getVendorName());
        vendorTransaction.setEntryDate(request.getEntryDate());
        vendorTransaction.setStorageUnit(request.getStorageUnit());
        vendorTransaction.setTemIdentificationScenarioId(
            request.getTemIdentificationScenarioId()
        );
        vendorTransaction.setMappingConfig(request.getMappingConfig());
        vendorTransaction.setStatus(request.getStatus());
        vendorTransaction.setCreatedBy(request.getCreatedBy());
        vendorTransaction.setCreatedAt(
            request.getCreatedAt() != null
                ? request.getCreatedAt()
                : ZonedDateTime.now()
        );
        vendorTransaction.setUpdatedBy(request.getUpdatedBy());
        vendorTransaction.setUpdatedAt(request.getUpdatedAt());
        vendorTransaction.setDeletedBy(request.getDeletedBy());
        vendorTransaction.setDeletedAt(request.getDeletedAt());
        vendorTransaction.setPoImportTemId(poImportTem.getId());

        vendorTransaction = importVendorTemTransactionsRepository.save(
            vendorTransaction
        );

        // 3. Build response with empty poDetails since this is a new record
        ImportVendorTemTransactionsDTO transactionDTO =
            importVendorTemTransactionsMapper.toDto(vendorTransaction);
        ImportVendorTemTransactionsDetailDTO detailDTO =
            new ImportVendorTemTransactionsDetailDTO(
                transactionDTO,
                new ArrayList<>()
            );

        return new PoImportResponseDTO(savedPoImportTem, detailDTO, "CASE_1");
    }

    /**
     * Case 2: If poNumber exists, search the import_vendor_tem_transactions table
     * for that value in the po_number column. If the returned list contains a
     * record,
     * check if the record's creation date is the same as today's date. If it is
     * today's date, retrieve all information from the
     * import_vendor_tem_transactions
     * class and its subclasses and return it to the user.
     *
     * If no records found or no records created today, search in SAP_PO_Info
     * table using po_number in OPOR_DocEntry column.
     */
    private PoImportResponseDTO handleCase2(
        PoImportRequestDTO request,
        String poNumber,
        String storageUnit
    ) {
        LOG.debug("Processing Case 2: poNumber = {}", poNumber);

        // 1. Search for records with the given poNumber in ImportVendorTemTransactions
        List<ImportVendorTemTransactions> transactions =
            importVendorTemTransactionsRepository.findByPoNumber(poNumber);

        if (transactions == null || transactions.isEmpty()) {
            // No records found - search in SAP_PO_Info
            return handleSapPoInfoLookup(request, poNumber);
        }

        // 2. Check if any record was created today
        LocalDate today = LocalDate.now();
        ImportVendorTemTransactions todaysTransaction = null;

        for (ImportVendorTemTransactions transaction : transactions) {
            // Filter by storageUnit if provided
            if (storageUnit != null && !storageUnit.isEmpty()) {
                String transactionStorageUnit = transaction.getStorageUnit();
                if (
                    transactionStorageUnit == null ||
                    !transactionStorageUnit.equals(storageUnit)
                ) {
                    continue;
                }
            }
            if (transaction.getCreatedAt() != null) {
                LocalDate createdDate = transaction
                    .getCreatedAt()
                    .toLocalDate();
                if (createdDate.equals(today)) {
                    todaysTransaction = transaction;
                    break;
                }
            }
        }

        // 3. If no record created today, search in SAP_PO_Info
        if (todaysTransaction == null) {
            return handleSapPoInfoLookup(request, poNumber);
        }

        // 4. Retrieve all information with subclasses (poDetails)
        ImportVendorTemTransactionsDTO transactionDTO =
            importVendorTemTransactionsMapper.toDto(todaysTransaction);

        // Fetch poDetails for this transaction
        List<PoDetail> poDetails = new ArrayList<>();
        if (
            todaysTransaction.getPoDetails() != null &&
            !todaysTransaction.getPoDetails().isEmpty()
        ) {
            poDetails = new ArrayList<>(todaysTransaction.getPoDetails());
        } else {
            // Fetch from repository if not loaded
            poDetails = poDetailRepository.findByImportVendorTemTransactionsId(
                todaysTransaction.getId()
            );
        }

        List<PoDetailDTO> poDetailDTOs = poDetailMapper.toDto(poDetails);

        ImportVendorTemTransactionsDetailDTO detailDTO =
            new ImportVendorTemTransactionsDetailDTO(
                transactionDTO,
                poDetailDTOs
            );

        // Get the parent poImportTem if exists
        PoImportTemDTO poImportTemDTO = null;
        if (todaysTransaction.getPoImportTemId() != null) {
            Optional<PoImportTem> poImportTem =
                poImportTemRepository.findWithTransactionsById(
                    todaysTransaction.getPoImportTemId()
                );
            poImportTemDTO = poImportTem
                .map(poImportTemMapper::toDto)
                .orElse(null);
        }

        return new PoImportResponseDTO(poImportTemDTO, detailDTO, "CASE_2");
    }

    /**
     * Case 2.1: Search in SAP_PO_Info table when no ImportVendorTemTransactions
     * found or no records created today.
     * Uses po_number to search in OPOR_DocEntry column.
     * Maps: OPOR_CardName -> vendorName, OPOR_CardCode -> vendorCode,
     * POR1_ItemCode -> sapCode, POR1_Dscription -> sapName,
     * POR1_Quantity -> totalQuantity
     */
    private PoImportResponseDTO handleSapPoInfoLookup(
        PoImportRequestDTO request,
        String poNumber
    ) {
        LOG.debug("Searching SAP_PO_Info for poNumber: {}", poNumber);

        // 1. Search in sap_po_info by OPOR_DocEntry
        List<SapPoInfo> sapPoInfoList = sapPoInfoRepository.findByOporDocEntry(
            poNumber
        );
        LOG.info(
            "SAP_PO_Info lookup result for poNumber={}: found {} records",
            poNumber,
            sapPoInfoList != null ? sapPoInfoList.size() : 0
        );

        if (sapPoInfoList == null || sapPoInfoList.isEmpty()) {
            // No SAP PO found - throw exception instead of creating records
            LOG.warn("No sap_po_info found for poNumber: {}", poNumber);
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND,
                "PO not found in SAP: " + poNumber
            );
        }

        // 2. Get first record for header info (assuming unique PO)
        SapPoInfo sapPoInfo = sapPoInfoList.get(0);

        // 3. Create parent record in po_import_tem with SAP data
        PoImportTem poImportTem = new PoImportTem();
        poImportTem.setPoNumber(poNumber);
        poImportTem.setVendorCode(sapPoInfo.getOporCardCode()); // OPOR_CardCode -> vendorCode
        poImportTem.setVendorName(sapPoInfo.getOporCardName()); // OPOR_CardName -> vendorName
        poImportTem.setEntryDate(request.getEntryDate());
        poImportTem.setStorageUnit(request.getStorageUnit());
        poImportTem.setStatus(request.getStatus());
        poImportTem.setCreatedBy(request.getCreatedBy());
        poImportTem.setCreatedAt(
            request.getCreatedAt() != null
                ? request.getCreatedAt()
                : ZonedDateTime.now()
        );
        poImportTem.setUpdatedBy(request.getUpdatedBy());
        poImportTem.setUpdatedAt(request.getUpdatedAt());
        poImportTem.setDeletedBy(request.getDeletedBy());
        poImportTem.setDeletedAt(request.getDeletedAt());

        poImportTem = poImportTemRepository.save(poImportTem);
        PoImportTemDTO savedPoImportTem = poImportTemMapper.toDto(poImportTem);

        // 4. Create child record in import_vendor_tem_transactions
        ImportVendorTemTransactions vendorTransaction =
            new ImportVendorTemTransactions();
        vendorTransaction.setPoNumber(poNumber);
        vendorTransaction.setVendorCode(sapPoInfo.getOporCardCode()); // OPOR_CardCode
        vendorTransaction.setVendorName(sapPoInfo.getOporCardName()); // OPOR_CardName
        vendorTransaction.setEntryDate(request.getEntryDate());
        vendorTransaction.setStorageUnit(request.getStorageUnit());
        vendorTransaction.setTemIdentificationScenarioId(
            request.getTemIdentificationScenarioId()
        );
        vendorTransaction.setMappingConfig(request.getMappingConfig());
        vendorTransaction.setStatus(request.getStatus());
        vendorTransaction.setCreatedBy(request.getCreatedBy());
        vendorTransaction.setCreatedAt(
            request.getCreatedAt() != null
                ? request.getCreatedAt()
                : ZonedDateTime.now()
        );
        vendorTransaction.setUpdatedBy(request.getUpdatedBy());
        vendorTransaction.setUpdatedAt(request.getUpdatedAt());
        vendorTransaction.setDeletedBy(request.getDeletedBy());
        vendorTransaction.setDeletedAt(request.getDeletedAt());
        vendorTransaction.setPoImportTemId(poImportTem.getId());

        vendorTransaction = importVendorTemTransactionsRepository.save(
            vendorTransaction
        );

        // 5. Create poDetails from SAP data
        // POR1_ItemCode -> sapCode, POR1_Dscription -> sapName,
        // POR1_Quantity -> totalQuantity
        List<PoDetailDTO> poDetailDTOs = new ArrayList<>();
        for (SapPoInfo info : sapPoInfoList) {
            PoDetail poDetail = new PoDetail();
            poDetail.setImportVendorTemTransactionsId(
                vendorTransaction.getId()
            );
            poDetail.setSapCode(info.getPor1ItemCode()); // POR1_ItemCode
            poDetail.setSapName(info.getPor1Dscription()); // POR1_Dscription
            // Parse String to Integer for totalQuantity (POR1_Quantity)
            if (
                info.getPor1Quantity() != null &&
                !info.getPor1Quantity().isEmpty()
            ) {
                try {
                    poDetail.setTotalQuantity(
                        Integer.parseInt(info.getPor1Quantity())
                    );
                } catch (NumberFormatException e) {
                    LOG.warn(
                        "Failed to parse quantity: {}",
                        info.getPor1Quantity()
                    );
                    poDetail.setTotalQuantity(0);
                }
            }
            poDetailRepository.save(poDetail);

            PoDetailDTO detailDTO = new PoDetailDTO();
            detailDTO.setSapCode(info.getPor1ItemCode());
            detailDTO.setSapName(info.getPor1Dscription());
            // Parse String to Integer for totalQuantity (POR1_Quantity)
            if (
                info.getPor1Quantity() != null &&
                !info.getPor1Quantity().isEmpty()
            ) {
                try {
                    detailDTO.setTotalQuantity(
                        Integer.parseInt(info.getPor1Quantity())
                    );
                } catch (NumberFormatException e) {
                    detailDTO.setTotalQuantity(0);
                }
            }
            poDetailDTOs.add(detailDTO);
        }

        // 6. Build response
        ImportVendorTemTransactionsDTO transactionDTO =
            importVendorTemTransactionsMapper.toDto(vendorTransaction);
        ImportVendorTemTransactionsDetailDTO detailDTO =
            new ImportVendorTemTransactionsDetailDTO(
                transactionDTO,
                poDetailDTOs
            );

        return new PoImportResponseDTO(
            savedPoImportTem,
            detailDTO,
            "CASE_2_SAP"
        );
    }
}
