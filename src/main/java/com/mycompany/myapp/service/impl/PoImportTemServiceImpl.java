package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.ImportVendorTemTransactions;
import com.mycompany.myapp.domain.PoDetail;
import com.mycompany.myapp.domain.PoImportTem;
import com.mycompany.myapp.domain.SapPoInfo;
import com.mycompany.myapp.domain.VendorTemDetail;
import com.mycompany.myapp.repository.partner4.SapOitmRepository;
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
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final SapOitmRepository sapOitmRepository;

    private final PoImportTemMapper poImportTemMapper;

    private final ImportVendorTemTransactionsMapper importVendorTemTransactionsMapper;

    private final PoDetailMapper poDetailMapper;

    public PoImportTemServiceImpl(
        PoImportTemRepository poImportTemRepository,
        ImportVendorTemTransactionsRepository importVendorTemTransactionsRepository,
        PoDetailRepository poDetailRepository,
        VendorTemDetailRepository vendorTemDetailRepository,
        SapPoInfoRepository sapPoInfoRepository,
        SapOitmRepository sapOitmRepository,
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
        this.sapOitmRepository = sapOitmRepository;
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
        dto.setQuantityContainer(poImportTem.getQuantityContainer());
        dto.setTotalQuantity(poImportTem.getTotalQuantity());
        dto.setStatus(poImportTem.getStatus());
        dto.setPoComments(poImportTem.getPoComments());
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

                // Map loose vendorTemDetails (not associated with poDetails)
                List<VendorTemDetail> noPoVendorTemDetails =
                    vendorTemDetailRepository.findByImportVendorTemTransactionsIdAndPoDetailIdIsNull(
                        transaction.getId()
                    );
                if (
                    noPoVendorTemDetails != null &&
                    !noPoVendorTemDetails.isEmpty()
                ) {
                    Set<VendorTemDetailDTO> looseVendorTemDetailDTOs =
                        new HashSet<>();
                    for (VendorTemDetail vendorTemDetail : noPoVendorTemDetails) {
                        looseVendorTemDetailDTOs.add(
                            mapToVendorTemDetailDTO(vendorTemDetail)
                        );
                    }
                    transDTO.setnoPoVendorTemDetails(looseVendorTemDetailDTOs);
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
        dto.setPanaSendStatus(entity.getPanaSendStatus());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    @Override
    @Transactional
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

        // Delete by IDs to avoid JPA entity state issues
        if (poImportTem.getImportVendorTemTransactions() != null) {
            for (ImportVendorTemTransactions transaction : poImportTem.getImportVendorTemTransactions()) {
                Long transactionId = transaction.getId();

                if (transaction.getPoDetails() != null) {
                    for (PoDetail poDetail : transaction.getPoDetails()) {
                        Long poDetailId = poDetail.getId();

                        // Delete all VendorTemDetail records for this PoDetail by ID
                        vendorTemDetailRepository.deleteByPoDetailId(
                            poDetailId
                        );
                    }
                    // Delete all PoDetail records for this transaction by ID
                    poDetailRepository.deleteByImportVendorTemTransactionsId(
                        transactionId
                    );
                }
            }
            // Delete all ImportVendorTemTransactions records by ID
            importVendorTemTransactionsRepository.deleteByPoImportTemId(id);
        }

        // Finally delete the PoImportTem
        poImportTemRepository.deleteById(id);
    }

    @Override
    public PoImportResponseDTO processPoImport(PoImportRequestDTO request) {
        LOG.debug("Request to process PO Import : {}", request);

        String poNumber = request.getPoNumber();
        // Case 1: poNumber is null - create parent and child records
        if (poNumber == null || poNumber.trim().isEmpty()) {
            return handleCase1(request);
        }

        // Case 2: poNumber exists - search for existing records
        return handleCase2(request, poNumber);
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
        poImportTem.setStatus(request.getStatus());
        poImportTem.setCreatedBy(request.getCreatedBy());
        poImportTem.setCreatedAt(
            request.getCreatedAt() != null
                ? request.getCreatedAt()
                : ZonedDateTime.now()
        );
        // poImportTem.setPoComments(request.getPoComments());
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
        vendorTransaction.setNote(request.getNote());
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

        transactionDTO.setPoDetails(new HashSet<>());

        ImportVendorTemTransactionsDetailDTO detailDTO =
            new ImportVendorTemTransactionsDetailDTO(transactionDTO);

        return new PoImportResponseDTO(savedPoImportTem, detailDTO, "CASE_1");
    }

    /**
     * Case 2: Nếu poNumber tồn tại, kiểm tra xem đã có transaction nào trùng lặp
     * về ngày (createdAt) và storageUnit hay chưa.
     * - Nếu có trùng: trả về dữ liệu đã có.
     * - Nếu chưa trùng (nhưng đã có PO này trước đó): tạo transaction mới gắn với po_import_tem_id cũ.
     * - Nếu hoàn toàn chưa có PO này: tạo mới hoàn toàn (Parent + Child) từ SAP.
     */
    private PoImportResponseDTO handleCase2(
        PoImportRequestDTO request,
        String poNumber
    ) {
        LOG.debug(
            "Processing Case 2: poNumber = {}, storageUnit = {}",
            poNumber,
            request.getStorageUnit()
        );

        // 1. TÌM TRỰC TIẾP BẢNG CHA (po_import_tem) ĐỂ CHỐNG TẠO MỚI TRÙNG LẶP
        Long existingPoImportTemId = null;

        // Truy vấn trực tiếp vào bảng cha xem PO này đã từng tồn tại chưa
        List<PoImportTem> existingParents =
            poImportTemRepository.findByPoNumber(poNumber);
        if (existingParents != null && !existingParents.isEmpty()) {
            // Nếu PO đã có trong hệ thống, lấy ngay ID của nó để tái sử dụng
            existingPoImportTemId = existingParents.get(0).getId();
        }

        // 2. Tìm các bản ghi con (transactions) hiện có
        List<ImportVendorTemTransactions> transactions =
            importVendorTemTransactionsRepository.findByPoNumber(poNumber);

        // Trường hợp 2.a: Hoàn toàn chưa có cả Cha lẫn Con trong hệ thống
        if (
            existingPoImportTemId == null &&
            (transactions == null || transactions.isEmpty())
        ) {
            LOG.debug(
                "PO {} does not exist at all. Creating new Parent and Child.",
                poNumber
            );
            return handleSapPoInfoLookup(request, poNumber, null);
        }

        // Chuẩn bị dữ liệu từ Payload để đối chiếu
        LocalDate requestDate = request.getCreatedAt() != null
            ? request.getCreatedAt().toLocalDate()
            : LocalDate.now();
        String requestStorageUnit = request.getStorageUnit();
        ImportVendorTemTransactions matchedTransaction = null;

        // 3. Duyệt transactions để tìm bản ghi trùng lặp (Ngày & Storage Unit)
        if (transactions != null) {
            for (ImportVendorTemTransactions trans : transactions) {
                if (trans.getCreatedAt() != null) {
                    LocalDate transDate = trans.getCreatedAt().toLocalDate();
                    boolean isSameDay = transDate.equals(requestDate);
                    boolean isSameStorageUnit = Objects.equals(
                        requestStorageUnit,
                        trans.getStorageUnit()
                    );

                    if (isSameDay && isSameStorageUnit) {
                        matchedTransaction = trans;
                        break; // Tìm thấy bản ghi trùng 100%
                    }
                }
            }
        }

        // 4. Quyết định hành động
        if (matchedTransaction != null) {
            // Trường hợp 2.b: CÓ TRÙNG (Ngày & StorageUnit)
            LOG.debug(
                "Found exact matching transaction for PO: {}. Returning existing data.",
                poNumber
            );
            return buildResponseFromTransaction(matchedTransaction);
        } else {
            // Trường hợp 2.c: KHÔNG TRÙNG (Nhưng PO này đã có ID cha)
            // Sẽ gọi SAP để tạo Child mới và móc thẳng vào ID cha đã tìm thấy ở Bước 1
            LOG.debug(
                "No match found. Creating new transaction linking to existing PoImportTem ID: {}",
                existingPoImportTemId
            );
            return handleSapPoInfoLookup(
                request,
                poNumber,
                existingPoImportTemId
            );
        }
    }

    /**
     * Hàm hỗ trợ để build PoImportResponseDTO từ một ImportVendorTemTransactions có sẵn
     */
    private PoImportResponseDTO buildResponseFromTransaction(
        ImportVendorTemTransactions transaction
    ) {
        ImportVendorTemTransactionsDTO transactionDTO =
            importVendorTemTransactionsMapper.toDto(transaction);

        // Lấy poDetails cho transaction này
        List<PoDetail> poDetails = new ArrayList<>();
        if (
            transaction.getPoDetails() != null &&
            !transaction.getPoDetails().isEmpty()
        ) {
            poDetails = new ArrayList<>(transaction.getPoDetails());
        } else {
            poDetails = poDetailRepository.findByImportVendorTemTransactionsId(
                transaction.getId()
            );
        }
        List<PoDetailDTO> poDetailDTOs = poDetailMapper.toDto(poDetails);
        transactionDTO.setPoDetails(new HashSet<>(poDetailDTOs));

        ImportVendorTemTransactionsDetailDTO detailDTO =
            new ImportVendorTemTransactionsDetailDTO(transactionDTO);

        PoImportTemDTO poImportTemDTO = null;
        if (transaction.getPoImportTemId() != null) {
            Optional<PoImportTem> poImportTem =
                poImportTemRepository.findWithTransactionsById(
                    transaction.getPoImportTemId()
                );
            poImportTemDTO = poImportTem
                .map(poImportTemMapper::toDto)
                .orElse(null);
        }

        return new PoImportResponseDTO(
            poImportTemDTO,
            detailDTO,
            "CASE_2_EXISTING"
        );
    }

    /**
     * Case 2.1: Search in SAP_PO_Info table.
     * Nếu existingPoImportTemId có giá trị -> Tái sử dụng Parent record.
     * Nếu existingPoImportTemId là null -> Tạo mới Parent record.
     * Luôn luôn tạo mới Child record (ImportVendorTemTransactions).
     */
    private PoImportResponseDTO handleSapPoInfoLookup(
        PoImportRequestDTO request,
        String poNumber,
        Long existingPoImportTemId
    ) {
        LOG.debug(
            "Searching SAP_PO_Info for poNumber: {}, reuseId: {}",
            poNumber,
            existingPoImportTemId
        );

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
            LOG.warn("No sap_po_info found for poNumber: {}", poNumber);
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND,
                "PO not found in SAP: " + poNumber
            );
        }

        // 2. Get first record for header info (assuming unique PO)
        SapPoInfo sapPoInfo = sapPoInfoList.get(0);

        PoImportTem poImportTem;
        PoImportTemDTO savedPoImportTemDTO;

        // 3. Xử lý Parent Record (PoImportTem)
        if (existingPoImportTemId != null) {
            // Tái sử dụng bản ghi Parent cũ
            poImportTem = poImportTemRepository
                .findById(existingPoImportTemId)
                .orElseThrow(() ->
                    new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "PoImportTem not found with id: " +
                        existingPoImportTemId
                    )
                );
            savedPoImportTemDTO = poImportTemMapper.toDto(poImportTem);
        } else {
            // Tạo mới Parent record
            poImportTem = new PoImportTem();
            poImportTem.setPoNumber(poNumber);
            poImportTem.setVendorCode(sapPoInfo.getOporCardCode()); // OPOR_CardCode -> vendorCode
            poImportTem.setVendorName(sapPoInfo.getOporCardName()); // OPOR_CardName -> vendorName
            poImportTem.setEntryDate(request.getEntryDate());
            poImportTem.setStatus(request.getStatus());
            poImportTem.setCreatedBy(request.getCreatedBy());
            poImportTem.setCreatedAt(
                request.getCreatedAt() != null
                    ? request.getCreatedAt()
                    : ZonedDateTime.now()
            );
            poImportTem.setUpdatedBy(request.getUpdatedBy());
            poImportTem.setUpdatedAt(request.getUpdatedAt());
            poImportTem.setPoComments(sapPoInfo.getOporComments());
            poImportTem.setDeletedBy(request.getDeletedBy());
            poImportTem.setDeletedAt(request.getDeletedAt());

            poImportTem = poImportTemRepository.save(poImportTem);
            savedPoImportTemDTO = poImportTemMapper.toDto(poImportTem);
        }

        // 4. Create NEW child record in import_vendor_tem_transactions
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
        vendorTransaction.setNote(request.getNote());
        vendorTransaction.setCreatedBy(request.getCreatedBy());
        vendorTransaction.setCreatedAt(
            request.getCreatedAt() != null
                ? request.getCreatedAt()
                : ZonedDateTime.now()
        );
        vendorTransaction.setUpdatedBy(request.getUpdatedBy());
        vendorTransaction.setUpdatedAt(request.getUpdatedAt());

        // Liên kết Child với Parent ID (có thể là ID cũ hoặc ID vừa tạo)
        vendorTransaction.setPoImportTemId(savedPoImportTemDTO.getId());

        vendorTransaction = importVendorTemTransactionsRepository.save(
            vendorTransaction
        );

        // 5. Create poDetails from SAP data
        Set<PoDetailDTO> poDetailDTOs = new HashSet<>();
        for (SapPoInfo info : sapPoInfoList) {
            PoDetail poDetail = new PoDetail();
            poDetail.setImportVendorTemTransactionsId(
                vendorTransaction.getId()
            );
            String sapCode = info.getPor1ItemCode();
            poDetail.setSapCode(sapCode); // POR1_ItemCode
            poDetail.setSapName(info.getPor1Dscription()); // POR1_Dscription

            // Fetch partNumber from SAP_OITM
            Optional<com.mycompany.myapp.domain.partner4.SapOitm> sapOitm =
                sapOitmRepository.findByItemCode(sapCode);
            if (sapOitm.isPresent()) {
                poDetail.setPartNumber(sapOitm.get().getuPartNumber());
            }

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
            detailDTO.setSapCode(sapCode);
            detailDTO.setSapName(info.getPor1Dscription());

            // Set partNumber in DTO
            if (sapOitm.isPresent()) {
                detailDTO.setPartNumber(sapOitm.get().getuPartNumber());
            }

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

        transactionDTO.setPoDetails(poDetailDTOs);

        ImportVendorTemTransactionsDetailDTO detailDTO =
            new ImportVendorTemTransactionsDetailDTO(transactionDTO);

        return new PoImportResponseDTO(
            savedPoImportTemDTO,
            detailDTO,
            existingPoImportTemId != null
                ? "CASE_2_REUSE_PO"
                : "CASE_2_CALL_SAP_CREATE_FULL"
        );
    }

    @Override
    public PoImportResponseDTO processImportVendorTemTransactionUpdate(
        ImportVendorTemTransactionsDTO transactionDTO
    ) {
        LOG.debug(
            "Request to process ImportVendorTemTransaction update : {}",
            transactionDTO
        );

        if (transactionDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Transaction ID is required",
                "ImportVendorTemTransactions",
                "idnull"
            );
        }

        // Find existing transaction
        ImportVendorTemTransactions existingTransaction =
            importVendorTemTransactionsRepository
                .findById(transactionDTO.getId())
                .orElseThrow(() ->
                    new BadRequestAlertException(
                        "Transaction not found",
                        "ImportVendorTemTransactions",
                        "notfound"
                    )
                );

        String poNumber = transactionDTO.getPoNumber();
        if (poNumber == null || poNumber.trim().isEmpty()) {
            throw new BadRequestAlertException(
                "PO Number is required for update",
                "ImportVendorTemTransactions",
                "ponumberrequired"
            );
        }

        // Update PO info from SAP if poNumber is provided
        List<SapPoInfo> sapPoInfoList = sapPoInfoRepository.findByOporDocEntry(
            poNumber
        );
        if (sapPoInfoList == null || sapPoInfoList.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND,
                "PO not found in SAP: " + poNumber
            );
        }

        SapPoInfo sapPoInfo = sapPoInfoList.get(0);

        // Update transaction with SAP data
        existingTransaction.setPoNumber(poNumber); // Update with the provided poNumber
        existingTransaction.setVendorCode(sapPoInfo.getOporCardCode());
        existingTransaction.setVendorName(sapPoInfo.getOporCardName());
        existingTransaction.setUpdatedAt(ZonedDateTime.now());
        existingTransaction.setUpdatedBy(transactionDTO.getUpdatedBy());

        // Also update parent PoImportTem if exists
        PoImportTem updatedParent = null;
        if (existingTransaction.getPoImportTemId() != null) {
            Optional<PoImportTem> parentOpt = poImportTemRepository.findById(
                existingTransaction.getPoImportTemId()
            );
            if (parentOpt.isPresent()) {
                PoImportTem parent = parentOpt.get();
                parent.setPoNumber(poNumber); // Update with the provided poNumber
                parent.setVendorCode(sapPoInfo.getOporCardCode());
                parent.setVendorName(sapPoInfo.getOporCardName());
                parent.setPoComments(sapPoInfo.getOporComments());
                parent.setUpdatedAt(ZonedDateTime.now());
                parent.setUpdatedBy(transactionDTO.getUpdatedBy());
                updatedParent = poImportTemRepository.save(parent);
            }
        }

        // Save updated transaction
        existingTransaction = importVendorTemTransactionsRepository.save(
            existingTransaction
        );

        // Delete existing poDetails for this transaction
        poDetailRepository.deleteByImportVendorTemTransactionsId(
            existingTransaction.getId()
        );

        // Create new poDetails from SAP data
        Set<PoDetailDTO> poDetailDTOs = new HashSet<>();
        for (SapPoInfo info : sapPoInfoList) {
            PoDetail poDetail = new PoDetail();
            poDetail.setImportVendorTemTransactionsId(
                existingTransaction.getId()
            );
            String sapCode = info.getPor1ItemCode();
            poDetail.setSapCode(sapCode);
            poDetail.setSapName(info.getPor1Dscription());

            // Fetch partNumber from SAP_OITM
            Optional<com.mycompany.myapp.domain.partner4.SapOitm> sapOitm =
                sapOitmRepository.findByItemCode(sapCode);
            if (sapOitm.isPresent()) {
                poDetail.setPartNumber(sapOitm.get().getuPartNumber());
            }

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
            PoDetail savedPoDetail = poDetailRepository.save(poDetail);

            PoDetailDTO detailDTO = new PoDetailDTO();
            detailDTO.setId(savedPoDetail.getId());
            detailDTO.setSapCode(sapCode);
            detailDTO.setSapName(info.getPor1Dscription());
            if (sapOitm.isPresent()) {
                detailDTO.setPartNumber(sapOitm.get().getuPartNumber());
            }
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
            detailDTO.setImportVendorTemTransactionsId(
                existingTransaction.getId()
            );
            poDetailDTOs.add(detailDTO);
        }

        // Build response
        ImportVendorTemTransactionsDTO updatedTransactionDTO =
            new ImportVendorTemTransactionsDTO();
        updatedTransactionDTO.setId(existingTransaction.getId());
        updatedTransactionDTO.setPoNumber(existingTransaction.getPoNumber());
        updatedTransactionDTO.setVendorCode(
            existingTransaction.getVendorCode()
        );
        updatedTransactionDTO.setVendorName(
            existingTransaction.getVendorName()
        );
        updatedTransactionDTO.setEntryDate(existingTransaction.getEntryDate());
        updatedTransactionDTO.setStorageUnit(
            existingTransaction.getStorageUnit()
        );
        updatedTransactionDTO.setTemIdentificationScenarioId(
            existingTransaction.getTemIdentificationScenarioId()
        );
        updatedTransactionDTO.setMappingConfig(
            existingTransaction.getMappingConfig()
        );
        updatedTransactionDTO.setStatus(existingTransaction.getStatus());
        updatedTransactionDTO.setNote(existingTransaction.getNote());
        updatedTransactionDTO.setApprover(existingTransaction.getApprover());
        updatedTransactionDTO.setCreatedBy(existingTransaction.getCreatedBy());
        updatedTransactionDTO.setCreatedAt(existingTransaction.getCreatedAt());
        updatedTransactionDTO.setUpdatedBy(existingTransaction.getUpdatedBy());
        updatedTransactionDTO.setUpdatedAt(existingTransaction.getUpdatedAt());
        updatedTransactionDTO.setDeletedBy(existingTransaction.getDeletedBy());
        updatedTransactionDTO.setDeletedAt(existingTransaction.getDeletedAt());
        updatedTransactionDTO.setPoImportTemId(
            existingTransaction.getPoImportTemId()
        );
        updatedTransactionDTO.setPoDetails(poDetailDTOs);

        ImportVendorTemTransactionsDetailDTO detailDTO =
            new ImportVendorTemTransactionsDetailDTO(updatedTransactionDTO);

        // Get parent PoImportTem DTO
        PoImportTemDTO parentDTO = null;
        if (updatedParent != null) {
            parentDTO = poImportTemMapper.toDto(updatedParent);
        }

        return new PoImportResponseDTO(
            parentDTO,
            detailDTO,
            "UPDATE_TRANSACTION"
        );
    }
}
