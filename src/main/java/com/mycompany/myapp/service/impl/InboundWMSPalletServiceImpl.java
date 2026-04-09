package com.mycompany.myapp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.domain.InboundWMSPallet;
import com.mycompany.myapp.domain.InboundWMSSession;
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
import com.mycompany.myapp.service.InboundWMSPalletService;
import com.mycompany.myapp.service.dto.BoxInfoDTO;
import com.mycompany.myapp.service.dto.InboundWMSPalletDTO;
import com.mycompany.myapp.service.dto.InboundWMSPalletScanRequestDTO;
import com.mycompany.myapp.service.dto.InboundWMSPalletScanResponseDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDetailDTO;
import com.mycompany.myapp.service.mapper.InboundWMSPalletMapper;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoDetailMapper;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoMapper;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.text.StyledEditorKit.BoldAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link com.mycompany.myapp.domain.InboundWMSPallet}.
 */
@Service
@Transactional
public class InboundWMSPalletServiceImpl implements InboundWMSPalletService {

    private static final Logger LOG = LoggerFactory.getLogger(
        InboundWMSPalletServiceImpl.class
    );

    private final InboundWMSPalletRepository inboundWMSPalletRepository;

    private final InboundWMSPalletMapper inboundWMSPalletMapper;

    private final InboundWMSSessionRepository inboundWMSSessionRepository;

    private final Partner3SerialBoxPalletMappingRepository serialBoxPalletMappingRepository;

    private final Partner3WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository;

    private final Partner3PalletInforDetailRepository palletInforDetailRepository;

    private final Partner3WarehouseStampInfoRepository warehouseStampInfoRepository;

    private final WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper;

    private final WarehouseStampInfoMapper warehouseStampInfoMapper;

    public InboundWMSPalletServiceImpl(
        InboundWMSPalletRepository inboundWMSPalletRepository,
        InboundWMSPalletMapper inboundWMSPalletMapper,
        InboundWMSSessionRepository inboundWMSSessionRepository,
        Partner3SerialBoxPalletMappingRepository serialBoxPalletMappingRepository,
        Partner3WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository,
        Partner3PalletInforDetailRepository palletInforDetailRepository,
        Partner3WarehouseStampInfoRepository warehouseStampInfoRepository,
        WarehouseStampInfoDetailMapper warehouseStampInfoDetailMapper,
        WarehouseStampInfoMapper warehouseStampInfoMapper
    ) {
        this.inboundWMSPalletRepository = inboundWMSPalletRepository;
        this.inboundWMSPalletMapper = inboundWMSPalletMapper;
        this.inboundWMSSessionRepository = inboundWMSSessionRepository;
        this.serialBoxPalletMappingRepository =
            serialBoxPalletMappingRepository;
        this.warehouseStampInfoDetailRepository =
            warehouseStampInfoDetailRepository;
        this.palletInforDetailRepository = palletInforDetailRepository;
        this.warehouseStampInfoRepository = warehouseStampInfoRepository;
        this.warehouseStampInfoDetailMapper = warehouseStampInfoDetailMapper;
        this.warehouseStampInfoMapper = warehouseStampInfoMapper;
    }

    @Override
    public InboundWMSPalletDTO save(InboundWMSPalletDTO inboundWMSPalletDTO) {
        LOG.debug("Request to save InboundWMSPallet : {}", inboundWMSPalletDTO);
        InboundWMSPallet inboundWMSPallet = inboundWMSPalletMapper.toEntity(
            inboundWMSPalletDTO
        );
        inboundWMSPallet = inboundWMSPalletRepository.save(inboundWMSPallet);
        return inboundWMSPalletMapper.toDto(inboundWMSPallet);
    }

    @Override
    public InboundWMSPalletDTO update(InboundWMSPalletDTO inboundWMSPalletDTO) {
        LOG.debug(
            "Request to update InboundWMSPallet : {}",
            inboundWMSPalletDTO
        );
        InboundWMSPallet inboundWMSPallet = inboundWMSPalletMapper.toEntity(
            inboundWMSPalletDTO
        );
        inboundWMSPallet = inboundWMSPalletRepository.save(inboundWMSPallet);
        return inboundWMSPalletMapper.toDto(inboundWMSPallet);
    }

    @Override
    public Optional<InboundWMSPalletDTO> partialUpdate(
        InboundWMSPalletDTO inboundWMSPalletDTO
    ) {
        LOG.debug(
            "Request to partially update InboundWMSPallet : {}",
            inboundWMSPalletDTO
        );

        return inboundWMSPalletRepository
            .findById(inboundWMSPalletDTO.getId())
            .map(existingInboundWMSPallet -> {
                inboundWMSPalletMapper.partialUpdate(
                    existingInboundWMSPallet,
                    inboundWMSPalletDTO
                );

                return existingInboundWMSPallet;
            })
            .map(inboundWMSPalletRepository::save)
            .map(inboundWMSPalletMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InboundWMSPalletDTO> findAll() {
        LOG.debug("Request to get all InboundWMSPallets");
        return inboundWMSPalletRepository
            .findAll()
            .stream()
            .map(inboundWMSPalletMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InboundWMSPalletDTO> findOne(Long id) {
        LOG.debug("Request to get InboundWMSPallet : {}", id);
        return inboundWMSPalletRepository
            .findById(id)
            .map(inboundWMSPalletMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete InboundWMSPallet : {}", id);
        inboundWMSPalletRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InboundWMSPalletDTO> findByInboundWMSSessionId(
        Long inboundWMSSessionId
    ) {
        LOG.debug(
            "Request to get InboundWMSPallets by InboundWMSSessionId : {}",
            inboundWMSSessionId
        );
        return inboundWMSPalletRepository
            .findByInboundWMSSessionId(inboundWMSSessionId)
            .stream()
            .map(inboundWMSPalletMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InboundWMSPalletScanResponseDTO scanAndSave(
        InboundWMSPalletScanRequestDTO requestDTO
    ) {
        LOG.debug("Request to scan and save InboundWMSPallet : {}", requestDTO);

        // 1. Logic lấy danh sách Box
        List<SerialBoxPalletMapping> mappings =
            serialBoxPalletMappingRepository.findBySerialPallet(
                requestDTO.getSerialPallet()
            );
        List<String> serialBoxes = mappings
            .stream()
            .map(SerialBoxPalletMapping::getSerialBox)
            .collect(Collectors.toList());

        List<WarehouseNoteInfoDetail> details =
            warehouseStampInfoDetailRepository.findByReelIdIn(serialBoxes);
        List<BoxInfoDTO> listBox = details
            .stream()
            .map(detail -> {
                BoxInfoDTO boxInfo = new BoxInfoDTO();
                boxInfo.setNote(detail.getComments());
                boxInfo.setBoxCode(detail.getReelId());
                boxInfo.setQuantity(detail.getInitialQuantity());
                boxInfo.setListSerialItems(detail.getListSerialItems());
                return boxInfo;
            })
            .collect(Collectors.toList());

        // 2. Logic lấy PalletInforDetail và wmsSendStatus
        Optional<PalletInforDetail> palletOpt =
            palletInforDetailRepository.findBySerialPallet(
                requestDTO.getSerialPallet()
            );

        WarehouseStampInfoDTO warehouseNoteInfo = null;
        Boolean wmsSendStatus = null; // Biến này đã được khai báo
        Long maLenhSanXuatId = null;

        if (palletOpt.isPresent()) {
            PalletInforDetail palletDetail = palletOpt.get();
            maLenhSanXuatId = palletDetail.getMaLenhSanXuatId();
            wmsSendStatus = palletDetail.getWmsSendStatus(); // Gán giá trị từ DB vào biến

            Optional<WarehouseNoteInfo> warehouseOpt =
                warehouseStampInfoRepository.findById(maLenhSanXuatId);
            if (warehouseOpt.isPresent()) {
                warehouseNoteInfo = warehouseStampInfoMapper.toDto(
                    warehouseOpt.get()
                );
            }
        }

        // 3. Lưu InboundWMSPallet
        InboundWMSPallet inboundWMSPallet = new InboundWMSPallet();
        Optional<InboundWMSSession> sessionOpt =
            inboundWMSSessionRepository.findById(
                requestDTO.getInboundWmsSessionId().longValue()
            );
        if (sessionOpt.isPresent()) {
            inboundWMSPallet.setInboundWMSSession(sessionOpt.get());
        }
        inboundWMSPallet.setSerialPallet(requestDTO.getSerialPallet());
        inboundWMSPallet.setCreatedBy(requestDTO.getScanedBy());
        inboundWMSPallet.setCreatedAt(ZonedDateTime.now());
        if (maLenhSanXuatId != null) {
            inboundWMSPallet.setWarehouseNoteInfoId(maLenhSanXuatId.intValue());
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String listBoxJson = objectMapper.writeValueAsString(listBox);
            inboundWMSPallet.setListBox(listBoxJson);
        } catch (JsonProcessingException e) {
            LOG.error("Error serializing listBox to JSON", e);
        }
        InboundWMSPallet saved = inboundWMSPalletRepository.save(
            inboundWMSPallet
        );

        // 4. Trả về Response
        InboundWMSPalletScanResponseDTO response =
            new InboundWMSPalletScanResponseDTO();
        response.setWarehouseNoteInfo(warehouseNoteInfo);

        InboundWMSPalletDTO dto = inboundWMSPalletMapper.toDto(saved);
        dto.setListBox(listBox);

        dto.setWmsSendStatus(String.valueOf(wmsSendStatus));
        // -------------------------------

        response.setInboundpalletInfo(dto);
        return response;
    }
}
