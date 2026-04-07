package com.mycompany.myapp.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.domain.InboundWMSPallet;
import com.mycompany.myapp.domain.InboundWMSSession;
import com.mycompany.myapp.domain.InboundWMSSession_;
import com.mycompany.myapp.domain.PalletInforDetail;
import com.mycompany.myapp.domain.SerialBoxPalletMapping;
import com.mycompany.myapp.domain.WarehouseNoteInfo;
import com.mycompany.myapp.domain.WarehouseNoteInfoDetail;
import com.mycompany.myapp.repository.partner3.InboundWMSPalletRepository;
import com.mycompany.myapp.repository.partner3.InboundWMSSessionRepository;
import com.mycompany.myapp.repository.partner3.Partner3PalletInforDetailRepository;
import com.mycompany.myapp.repository.partner3.Partner3SerialBoxPalletMappingRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoDetailRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoRepository;
import com.mycompany.myapp.service.InboundWMSSessionService;
import com.mycompany.myapp.service.criteria.InboundWMSSessionCriteria;
import com.mycompany.myapp.service.dto.BoxDTO;
import com.mycompany.myapp.service.dto.BoxInfoDTO;
import com.mycompany.myapp.service.dto.GeneralInfoDTO;
import com.mycompany.myapp.service.dto.InboundWMSPalletDTO;
import com.mycompany.myapp.service.dto.InboundWMSSessionDTO;
import com.mycompany.myapp.service.dto.PalletDTO;
import com.mycompany.myapp.service.mapper.InboundWMSPalletMapper;
import com.mycompany.myapp.service.mapper.InboundWMSSessionMapper;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoDetailMapper;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import liquibase.pro.packaged.in;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import tech.jhipster.service.QueryService;

/**
 * Service Implementation for managing
 * {@link com.mycompany.myapp.domain.InboundWMSSession}.
 */
@Service
@Transactional
public class InboundWMSSessionServiceImpl
    extends QueryService<InboundWMSSession>
    implements InboundWMSSessionService {

    private static final Logger LOG = LoggerFactory.getLogger(
        InboundWMSSessionServiceImpl.class
    );

    private final InboundWMSSessionRepository inboundWMSSessionRepository;

    private final InboundWMSSessionMapper inboundWMSSessionMapper;

    private final InboundWMSPalletRepository inboundWMSPalletRepository;

    private final InboundWMSPalletMapper inboundWMSPalletMapper;

    private final Partner3PalletInforDetailRepository palletInforDetailRepository;

    private final Partner3SerialBoxPalletMappingRepository serialBoxPalletMappingRepository;

    private final Partner3WarehouseStampInfoRepository warehouseStampInfoRepository;

    private final WarehouseStampInfoMapper warehouseStampInfoMapper;

    private final Partner3WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository;

    private final WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public static class QmsInboundDTO {

        public Integer numberOfPallet;
        public Integer numberOfBox;
        public Integer quantity;
        public String workOrder;
        public String createdBy;

        public int getNumberOfPallet() {
            return numberOfPallet;
        }

        public void setNumberOfPallet(Integer numberOfPallet) {
            this.numberOfPallet = numberOfPallet;
        }

        public int getNumberOfBox() {
            return numberOfBox;
        }

        public void setNumberOfBox(Integer numberOfBox) {
            this.numberOfBox = numberOfBox;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getWorkOrder() {
            return workOrder;
        }

        public void setWorkOrder(String workOrder) {
            this.workOrder = workOrder;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }
    }

    public InboundWMSSessionServiceImpl(
        InboundWMSSessionRepository inboundWMSSessionRepository,
        InboundWMSSessionMapper inboundWMSSessionMapper,
        InboundWMSPalletRepository inboundWMSPalletRepository,
        InboundWMSPalletMapper inboundWMSPalletMapper,
        Partner3PalletInforDetailRepository palletInforDetailRepository,
        Partner3SerialBoxPalletMappingRepository serialBoxPalletMappingRepository,
        Partner3WarehouseStampInfoRepository warehouseStampInfoRepository,
        WarehouseStampInfoMapper warehouseStampInfoMapper,
        Partner3WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository,
        WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper
    ) {
        this.inboundWMSSessionRepository = inboundWMSSessionRepository;
        this.inboundWMSSessionMapper = inboundWMSSessionMapper;
        this.inboundWMSPalletRepository = inboundWMSPalletRepository;
        this.inboundWMSPalletMapper = inboundWMSPalletMapper;
        this.palletInforDetailRepository = palletInforDetailRepository;
        this.serialBoxPalletMappingRepository =
            serialBoxPalletMappingRepository;
        this.warehouseStampInfoRepository = warehouseStampInfoRepository;
        this.warehouseStampInfoMapper = warehouseStampInfoMapper;
        this.warehouseStampInfoDetailRepository =
            warehouseStampInfoDetailRepository;
        this.warehouseStampInfoDetailMapper = warehouseStampInfoDetailMapper;
    }

    @Override
    public InboundWMSSessionDTO save(
        InboundWMSSessionDTO inboundWMSSessionDTO
    ) {
        LOG.debug(
            "Request to save InboundWMSSession : {}",
            inboundWMSSessionDTO
        );
        InboundWMSSession inboundWMSSession = inboundWMSSessionMapper.toEntity(
            inboundWMSSessionDTO
        );
        inboundWMSSession = inboundWMSSessionRepository.save(inboundWMSSession);
        return inboundWMSSessionMapper.toDto(inboundWMSSession);
    }

    @Override
    public InboundWMSSessionDTO update(
        InboundWMSSessionDTO inboundWMSSessionDTO
    ) {
        LOG.debug(
            "Request to update InboundWMSSession : {}",
            inboundWMSSessionDTO
        );
        InboundWMSSession inboundWMSSession = inboundWMSSessionMapper.toEntity(
            inboundWMSSessionDTO
        );
        inboundWMSSession = inboundWMSSessionRepository.save(inboundWMSSession);
        return inboundWMSSessionMapper.toDto(inboundWMSSession);
    }

    @Override
    public Optional<InboundWMSSessionDTO> partialUpdate(
        InboundWMSSessionDTO inboundWMSSessionDTO
    ) {
        LOG.debug(
            "Request to partially update InboundWMSSession : {}",
            inboundWMSSessionDTO
        );

        return inboundWMSSessionRepository
            .findById(inboundWMSSessionDTO.getId())
            .map(existingInboundWMSSession -> {
                inboundWMSSessionMapper.partialUpdate(
                    existingInboundWMSSession,
                    inboundWMSSessionDTO
                );

                return existingInboundWMSSession;
            })
            .map(inboundWMSSessionRepository::save)
            .map(inboundWMSSessionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InboundWMSSessionDTO> findAll(
        InboundWMSSessionCriteria criteria,
        Pageable pageable
    ) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<InboundWMSSession> specification =
            createSpecification(criteria);
        return inboundWMSSessionRepository
            .findAll(specification, pageable)
            .map(inboundWMSSessionMapper::toDto);
    }

    protected Specification<InboundWMSSession> createSpecification(
        InboundWMSSessionCriteria criteria
    ) {
        Specification<InboundWMSSession> specification = Specification.where(
            null
        );
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getId(),
                        InboundWMSSession_.id
                    )
                );
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getStatus(),
                        InboundWMSSession_.status
                    )
                );
            }
            if (criteria.getNote() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getNote(),
                        InboundWMSSession_.note
                    )
                );
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(
                    buildStringSpecification(
                        criteria.getCreatedBy(),
                        InboundWMSSession_.createdBy
                    )
                );
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getCreatedAt(),
                        InboundWMSSession_.createdAt
                    )
                );
            }
            if (criteria.getWmsSentAt() != null) {
                specification = specification.and(
                    buildRangeSpecification(
                        criteria.getWmsSentAt(),
                        InboundWMSSession_.wmsSentAt
                    )
                );
            }
        }
        return specification;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InboundWMSSessionDTO> findOne(Long id) {
        LOG.debug("Request to get InboundWMSSession : {}", id);
        return inboundWMSSessionRepository
            .findById(id)
            .map(session -> {
                InboundWMSSessionDTO dto = inboundWMSSessionMapper.toDto(
                    session
                );
                computeAggregates(dto, session.getId());
                return dto;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InboundWMSSessionDTO> findOneWithPallets(Long id) {
        LOG.debug("Request to get InboundWMSSession with pallets : {}", id);
        return inboundWMSSessionRepository
            .findOneWithEagerRelationshipsById(id)
            .map(session -> {
                InboundWMSSessionDTO dto = inboundWMSSessionMapper.toDto(
                    session
                );
                Set<InboundWMSPalletDTO> palletDTOs = session
                    .getInboundWMSPallets()
                    .stream()
                    .map(pallet -> {
                        InboundWMSPalletDTO palletDTO =
                            inboundWMSPalletMapper.toDto(pallet);
                        if (palletDTO.getWarehouseNoteInfoId() != null) {
                            warehouseStampInfoRepository
                                .findById(
                                    Long.valueOf(
                                        palletDTO.getWarehouseNoteInfoId()
                                    )
                                )
                                .ifPresent(warehouseNoteInfo -> {
                                    palletDTO.setWarehouseNoteInfo(
                                        warehouseStampInfoMapper.toDto(
                                            warehouseNoteInfo
                                        )
                                    );
                                });
                        }
                        // Populate listBox only if not already set from stored JSON
                        if (
                            palletDTO.getListBox() == null ||
                            palletDTO.getListBox().isEmpty()
                        ) {
                            List<SerialBoxPalletMapping> mappings =
                                serialBoxPalletMappingRepository.findBySerialPallet(
                                    palletDTO.getSerialPallet()
                                );
                            List<String> serialBoxes = mappings
                                .stream()
                                .map(SerialBoxPalletMapping::getSerialBox)
                                .collect(Collectors.toList());
                            List<WarehouseNoteInfoDetail> details =
                                warehouseStampInfoDetailRepository.findByReelIdIn(
                                    serialBoxes
                                );
                            List<BoxInfoDTO> listBox = details
                                .stream()
                                .map(detail -> {
                                    BoxInfoDTO boxInfo = new BoxInfoDTO();
                                    boxInfo.setNote(detail.getComments());
                                    boxInfo.setBoxCode(detail.getReelId());
                                    boxInfo.setQuantity(
                                        detail.getInitialQuantity()
                                    );
                                    boxInfo.setListSerialItems(
                                        detail.getListSerialItems()
                                    );
                                    return boxInfo;
                                })
                                .collect(Collectors.toList());
                            palletDTO.setListBox(listBox);
                        }
                        return palletDTO;
                    })
                    .collect(Collectors.toSet());
                dto.setInboundWMSPallets(palletDTOs);

                // Compute aggregated values from loaded pallets
                int numPallets = palletDTOs.size();
                dto.setNumberOfPallets(numPallets);
                int numBoxes = palletDTOs
                    .stream()
                    .mapToInt(p ->
                        p.getListBox() != null ? p.getListBox().size() : 0
                    )
                    .sum();
                dto.setNumberOfBox(numBoxes);
                int totalQty = palletDTOs
                    .stream()
                    .filter(p -> p.getListBox() != null)
                    .flatMap(p -> p.getListBox().stream())
                    .mapToInt(b ->
                        b.getQuantity() != null ? b.getQuantity() : 0
                    )
                    .sum();
                dto.setTotalQuantity(totalQty);

                return dto;
            });
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete InboundWMSSession : {}", id);
        inboundWMSSessionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InboundWMSPalletDTO> findPalletsBySessionId(Long sessionId) {
        LOG.debug(
            "Request to get InboundWMSPallets by SessionId : {}",
            sessionId
        );
        return inboundWMSPalletRepository
            .findByInboundWMSSessionId(sessionId)
            .stream()
            .map(inboundWMSPalletMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public void submitWarehouseEntryApproval(Long sessionId) {
        LOG.debug(
            "Request to submit warehouse entry approval for session : {}",
            sessionId
        );

        // Fetch session with pallets
        Optional<InboundWMSSession> sessionOpt =
            inboundWMSSessionRepository.findOneWithEagerRelationshipsById(
                sessionId
            );
        if (!sessionOpt.isPresent()) {
            throw new RuntimeException("Session not found");
        }
        InboundWMSSession session = sessionOpt.get();
        Set<InboundWMSPallet> pallets = session.getInboundWMSPallets();

        // Group pallets by warehouseNoteInfoId
        Map<Integer, List<InboundWMSPallet>> groupedPallets = new HashMap<>();
        for (InboundWMSPallet pallet : pallets) {
            Integer warehouseNoteInfoId = pallet.getWarehouseNoteInfoId();
            groupedPallets
                .computeIfAbsent(warehouseNoteInfoId, k -> new ArrayList<>())
                .add(pallet);
        }
        List<QmsInboundDTO> QmsInboundBody = new ArrayList<>();

        for (Map.Entry<
            Integer,
            List<InboundWMSPallet>
        > entry : groupedPallets.entrySet()) {
            Integer warehouseNoteInfoId = entry.getKey();
            List<InboundWMSPallet> groupPallets = entry.getValue();

            Optional<WarehouseNoteInfo> noteInfoOpt =
                warehouseStampInfoRepository.findById(
                    Long.valueOf(warehouseNoteInfoId)
                );
            if (!noteInfoOpt.isPresent()) {
                LOG.warn(
                    "WarehouseNoteInfo not found for id: {}",
                    warehouseNoteInfoId
                );
                continue;
            }
            WarehouseNoteInfo noteInfo = noteInfoOpt.get();

            // Build general_info
            GeneralInfoDTO generalInfo = new GeneralInfoDTO();
            generalInfo.setClientId(groupPallets.get(0).getSerialPallet()); // first pallet's serial as client_id
            generalInfo.setInventoryCode(noteInfo.getSapCode());
            generalInfo.setInventoryName(noteInfo.getSapName());
            generalInfo.setWoCode(noteInfo.getWorkOrderCode());
            generalInfo.setLotNumber(noteInfo.getLotNumber());
            generalInfo.setNote(noteInfo.getComment());
            generalInfo.setCreatedBy(noteInfo.getCreateBy());
            generalInfo.setBranch(noteInfo.getBranch());
            generalInfo.setProductionTeam(noteInfo.getGroupName());
            generalInfo.setNumberOfPallets(groupPallets.size());
            generalInfo.setDestinationWarehouse(
                noteInfo.getDestinationWarehouse()
            );
            generalInfo.setPalletNoteCreationId(warehouseNoteInfoId);

            // Calculate numberOfBox and quantity
            int totalBoxes = 0;
            int totalQuantity = 0;
            List<PalletDTO> palletDTOs = new ArrayList<>();
            for (InboundWMSPallet pallet : groupPallets) {
                // Fetch PalletInforDetail
                Optional<PalletInforDetail> detailOpt =
                    palletInforDetailRepository.findBySerialPallet(
                        pallet.getSerialPallet()
                    );
                if (!detailOpt.isPresent()) {
                    LOG.warn(
                        "PalletInforDetail not found for serial: {}",
                        pallet.getSerialPallet()
                    );
                    continue;
                }
                PalletInforDetail detail = detailOpt.get();

                PalletDTO palletDTO = new PalletDTO();
                palletDTO.setSerialPallet(detail.getSerialPallet());
                palletDTO.setQuantityPerBox(
                    detail.getQuantityPerBox() != null
                        ? detail.getQuantityPerBox()
                        : 0
                );
                palletDTO.setNumBoxPerPallet(
                    detail.getNumBoxPerPallet() != null
                        ? detail.getNumBoxPerPallet()
                        : 0
                );
                palletDTO.setTotalQuantity(
                    (detail.getQuantityPerBox() != null
                            ? detail.getQuantityPerBox()
                            : 0) *
                    (detail.getNumBoxPerPallet() != null
                            ? detail.getNumBoxPerPallet()
                            : 0)
                );
                palletDTO.setPoNumber(detail.getPoNumber());
                palletDTO.setCustomerName(detail.getCustomerName());
                palletDTO.setProductionDecisionNumber(detail.getQdsxNo());
                palletDTO.setItemNoSku(detail.getItemNoSku());
                palletDTO.setDateCode(detail.getDateCode());
                palletDTO.setNote(detail.getNote());
                palletDTO.setProductionDate(
                    detail.getProductionDate() != null
                        ? detail.getProductionDate().toString()
                        : null
                );

                // 2. PARSE CHUỖI JSON TỪ getListBox()
                List<BoxDTO> boxDTOs = new ArrayList<>();
                int palletQuantity = 0;

                String listBoxJson = pallet.getListBox(); // Chuỗi: [{"note": "...", "box_code": "...", "quantity": 2, ...}]

                if (listBoxJson != null && !listBoxJson.isEmpty()) {
                    try {
                        // Chuyển chuỗi JSON thành List<BoxDTO>
                        boxDTOs = objectMapper.readValue(
                            listBoxJson,
                            new TypeReference<List<BoxDTO>>() {}
                        );

                        // Tính tổng số lượng của riêng Pallet này từ danh sách box vừa parse
                        for (BoxDTO box : boxDTOs) {
                            palletQuantity += (box.getQuantity() != null
                                    ? box.getQuantity()
                                    : 0);
                        }
                    } catch (Exception e) {
                        LOG.error(
                            "Lỗi parse JSON list_box cho pallet {}: {}",
                            pallet.getSerialPallet(),
                            e.getMessage()
                        );
                    }
                }

                // 3. GÁN DỮ LIỆU VÀO DTO
                palletDTO.setListBox(boxDTOs);
                palletDTO.setTotalQuantity(palletQuantity);
                palletDTOs.add(palletDTO);

                // 4. CẬP NHẬT TỔNG CHO TOÀN BỘ WORK ORDER (General Info)
                totalBoxes += boxDTOs.size();
                totalQuantity += palletQuantity;
            }

            generalInfo.setNumberOfBox(totalBoxes);
            generalInfo.setQuantity(totalQuantity);
            generalInfo.setListPallet(palletDTOs);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("general_info", generalInfo);

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(
                    requestBody,
                    headers
                );
                restTemplate.postForObject(
                    "http://192.168.10.99:9030/api/import-requirements/wms",
                    entity,
                    String.class
                );
                LOG.info(
                    "Successfully sent approval request: {}",
                    warehouseNoteInfoId
                );
                LOG.info("payload send to WMS: {}", requestBody);
            } catch (Exception e) {
                LOG.error(
                    "Failed to send approval request: {}",
                    warehouseNoteInfoId,
                    e
                );
            }
            QmsInboundDTO qmPaloadBody = new QmsInboundDTO();
            qmPaloadBody.setNumberOfPallet(groupPallets.size());
            qmPaloadBody.setNumberOfBox(totalBoxes);
            qmPaloadBody.setQuantity(totalQuantity);
            qmPaloadBody.setWorkOrder(noteInfo.getWorkOrderCode());
            qmPaloadBody.setCreatedBy(generalInfo.getCreatedBy());

            QmsInboundBody.add(qmPaloadBody);
        }
        try {
            String qmsEndpoint =
                "http://192.168.10.99:3000/api/pqc-sap-item-details/batch-update";
            restTemplate.postForObject(
                qmsEndpoint,
                QmsInboundBody,
                String.class
            );
            LOG.info("Successfully sent info to QMS");
        } catch (Exception e) {
            LOG.error("Failed to send to QMS", e);
        }
    }

    private void computeAggregates(InboundWMSSessionDTO dto, Long sessionId) {
        List<InboundWMSPallet> pallets =
            inboundWMSPalletRepository.findByInboundWMSSessionId(sessionId);
        dto.setNumberOfPallets(pallets.size());
        int numBoxes = 0;
        int totalQty = 0;
        for (InboundWMSPallet pallet : pallets) {
            String listBoxJson = pallet.getListBox();
            LOG.debug(
                "listBoxJson for pallet {}: {}",
                pallet.getSerialPallet(),
                listBoxJson
            );
            if (listBoxJson != null && !listBoxJson.isEmpty()) {
                try {
                    List<BoxDTO> boxes = objectMapper.readValue(
                        listBoxJson,
                        new TypeReference<List<BoxDTO>>() {}
                    );
                    numBoxes += boxes.size();
                    for (BoxDTO box : boxes) {
                        totalQty += box.getQuantity() != null
                            ? box.getQuantity()
                            : 0;
                    }
                } catch (Exception e) {
                    LOG.error(
                        "Error parsing list_box for pallet {}",
                        pallet.getSerialPallet(),
                        e
                    );
                }
            }
        }
        dto.setNumberOfBox(numBoxes);
        dto.setTotalQuantity(totalQty);
    }
}
