package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.OwtqWarehouseTransferSession;
import com.mycompany.myapp.domain.PalletBoxMapping;
import com.mycompany.myapp.domain.ScanPallet;
import com.mycompany.myapp.domain.WarehouseTransferDetail;
import com.mycompany.myapp.domain.WarehouseTransferSession;
import com.mycompany.myapp.repository.OwtqWarehouseTransferSessionRepository;
import com.mycompany.myapp.repository.PalletBoxMappingRepository;
import com.mycompany.myapp.repository.ScanPalletRepository;
import com.mycompany.myapp.repository.WarehouseTransferDetailRepository;
import com.mycompany.myapp.repository.WarehouseTransferSessionRepository;
import com.mycompany.myapp.service.WarehouseTransferSessionService;
import com.mycompany.myapp.service.dto.OwtqWarehouseTransferSessionDTO;
import com.mycompany.myapp.service.dto.ScannedPalletDTO;
import com.mycompany.myapp.service.dto.WarehouseTransferDetailDTO;
import com.mycompany.myapp.service.dto.WarehouseTransferSessionDTO;
import com.mycompany.myapp.service.dto.WarehouseTransferSessionDetailDTO;
import com.mycompany.myapp.service.mapper.WarehouseTransferSessionMapper;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.WarehouseTransferSession}.
 */
@Service
@Transactional
public class WarehouseTransferSessionServiceImpl
    implements WarehouseTransferSessionService {

    private final Logger log = LoggerFactory.getLogger(
        WarehouseTransferSessionServiceImpl.class
    );

    private final WarehouseTransferSessionRepository warehouseTransferSessionRepository;

    private final OwtqWarehouseTransferSessionRepository owtqWarehouseTransferSessionRepository;

    private final ScanPalletRepository scanPalletRepository;

    private final PalletBoxMappingRepository palletBoxMappingRepository;

    private final WarehouseTransferDetailRepository warehouseTransferDetailRepository;

    private final WarehouseTransferSessionMapper warehouseTransferSessionMapper;

    public WarehouseTransferSessionServiceImpl(
        WarehouseTransferSessionRepository warehouseTransferSessionRepository,
        OwtqWarehouseTransferSessionRepository owtqWarehouseTransferSessionRepository,
        ScanPalletRepository scanPalletRepository,
        PalletBoxMappingRepository palletBoxMappingRepository,
        WarehouseTransferDetailRepository warehouseTransferDetailRepository,
        WarehouseTransferSessionMapper warehouseTransferSessionMapper
    ) {
        this.warehouseTransferSessionRepository =
            warehouseTransferSessionRepository;
        this.owtqWarehouseTransferSessionRepository =
            owtqWarehouseTransferSessionRepository;
        this.scanPalletRepository = scanPalletRepository;
        this.palletBoxMappingRepository = palletBoxMappingRepository;
        this.warehouseTransferDetailRepository =
            warehouseTransferDetailRepository;
        this.warehouseTransferSessionMapper = warehouseTransferSessionMapper;
    }

    @Override
    public WarehouseTransferSessionDTO save(
        WarehouseTransferSessionDTO warehouseTransferSessionDTO
    ) {
        log.debug(
            "Request to save WarehouseTransferSession : {}",
            warehouseTransferSessionDTO
        );
        final WarehouseTransferSession warehouseTransferSession =
            warehouseTransferSessionMapper.toEntity(
                warehouseTransferSessionDTO
            );
        final WarehouseTransferSession savedSession =
            warehouseTransferSessionRepository.save(warehouseTransferSession);
        final WarehouseTransferSessionDTO resultDto =
            warehouseTransferSessionMapper.toDto(savedSession);
        if (warehouseTransferSessionDTO.getOwtqList() != null) {
            warehouseTransferSessionDTO
                .getOwtqList()
                .forEach(owtqDocEntry -> {
                    OwtqWarehouseTransferSession owtqSession =
                        new OwtqWarehouseTransferSession();
                    owtqSession.setOwtqDocEntry(owtqDocEntry);
                    owtqSession.setWarehouseTransferSession(savedSession);
                    owtqWarehouseTransferSessionRepository.save(owtqSession);
                });
            resultDto.setOwtqList(warehouseTransferSessionDTO.getOwtqList());
        }

        return resultDto;
    }

    @Override
    public WarehouseTransferSessionDTO update(
        WarehouseTransferSessionDTO warehouseTransferSessionDTO
    ) {
        log.debug(
            "Request to update WarehouseTransferSession : {}",
            warehouseTransferSessionDTO
        );
        WarehouseTransferSession warehouseTransferSession =
            warehouseTransferSessionMapper.toEntity(
                warehouseTransferSessionDTO
            );
        warehouseTransferSession = warehouseTransferSessionRepository.save(
            warehouseTransferSession
        );
        return warehouseTransferSessionMapper.toDto(warehouseTransferSession);
    }

    @Override
    public Optional<WarehouseTransferSessionDTO> partialUpdate(
        WarehouseTransferSessionDTO warehouseTransferSessionDTO
    ) {
        log.debug(
            "Request to partially update WarehouseTransferSession : {}",
            warehouseTransferSessionDTO
        );

        return warehouseTransferSessionRepository
            .findById(warehouseTransferSessionDTO.getId())
            .map(existingWarehouseTransferSession -> {
                warehouseTransferSessionMapper.partialUpdate(
                    existingWarehouseTransferSession,
                    warehouseTransferSessionDTO
                );

                return existingWarehouseTransferSession;
            })
            .map(warehouseTransferSessionRepository::save)
            .map(warehouseTransferSessionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseTransferSessionDTO> findOne(Long id) {
        log.debug("Request to get WarehouseTransferSession : {}", id);
        return warehouseTransferSessionRepository
            .findById(id)
            .map(warehouseTransferSessionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseTransferSessionDetailDTO> findDetailById(Long id) {
        log.debug("Request to get WarehouseTransferSession detail : {}", id);
        return warehouseTransferSessionRepository
            .findByIdWithDetails(id)
            .map(session -> {
                WarehouseTransferSessionDetailDTO detailDTO =
                    new WarehouseTransferSessionDetailDTO();
                detailDTO.setSession(
                    warehouseTransferSessionMapper.toDto(session)
                );

                List<OwtqWarehouseTransferSessionDTO> owtqDtos =
                    owtqWarehouseTransferSessionRepository
                        .findByWarehouseTransferSession_Id(id)
                        .stream()
                        .map(owtq -> {
                            OwtqWarehouseTransferSessionDTO dto =
                                new OwtqWarehouseTransferSessionDTO();
                            dto.setId(owtq.getId());
                            dto.setOwtqWarehouseTransferSessionId(
                                owtq.getOwtqWarehouseTransferSessionId()
                            );
                            dto.setOwtqDocEntry(owtq.getOwtqDocEntry());
                            return dto;
                        })
                        .collect(Collectors.toList());

                detailDTO.setOwtqSessions(owtqDtos);

                List<WarehouseTransferDetailDTO> detailDtos = session
                    .getDetails()
                    .stream()
                    .map(d -> {
                        WarehouseTransferDetailDTO dto =
                            new WarehouseTransferDetailDTO();
                        dto.setId(d.getId());
                        dto.setLineNum(d.getLineNum());
                        dto.setReelId(d.getReelId());
                        dto.setPartNumber(d.getPartNumber());
                        dto.setVendor(d.getVendor());
                        dto.setLot(d.getLot());
                        dto.setInitialQuantity(d.getInitialQuantity());
                        dto.setStatus(d.getStatus());
                        dto.setVendorQrCode(d.getVendorQrCode());
                        dto.setUserData1(d.getUserData1());
                        dto.setUserData2(d.getUserData2());
                        dto.setUserData3(d.getUserData3());
                        dto.setUserData4(d.getUserData4());
                        dto.setUserData5(d.getUserData5());
                        dto.setMsdLevel(d.getMsdLevel());
                        dto.setMsdInitialFloorTime(d.getMsdInitialFloorTime());
                        dto.setMsdBagSealDate(d.getMsdBagSealDate());
                        dto.setMarketUsage(d.getMarketUsage());
                        dto.setQuantityOverride(d.getQuantityOverride());
                        dto.setShelfTime(d.getShelfTime());
                        dto.setSpMaterialName(d.getSpMaterialName());
                        dto.setWarningLimit(d.getWarningLimit());
                        dto.setMaximumLimit(d.getMaximumLimit());
                        dto.setComments(d.getComments());
                        dto.setWarmupTime(d.getWarmupTime());
                        dto.setStorageUnit(d.getStorageUnit());
                        dto.setSubStorageUnit(d.getSubStorageUnit());
                        dto.setLocationOverride(d.getLocationOverride());
                        dto.setExpirationDate(d.getExpirationDate());
                        dto.setManufacturingDate(d.getManufacturingDate());
                        dto.setPartClass(d.getPartClass());
                        dto.setSapCode(d.getSapCode());
                        dto.setCreatedBy(d.getCreatedBy());
                        dto.setCreatedAt(d.getCreatedAt());
                        dto.setUpdatedBy(d.getUpdatedBy());
                        dto.setUpdatedAt(d.getUpdatedAt());
                        dto.setPanaSendStatus(d.getPanaSendStatus());
                        dto.setSapSendStatus(d.getSapSendStatus());
                        dto.setOwtqDocEntry(d.getOwtqDocEntry());
                        dto.setWarehouseTransferSessionId(
                            d.getWarehouseTransferSession() != null
                                ? d.getWarehouseTransferSession().getId()
                                : null
                        );
                        return dto;
                    })
                    .collect(Collectors.toList());

                detailDTO.setDetails(detailDtos);

                // Scanned Pallets logic
                List<ScanPallet> scannedPallets =
                    scanPalletRepository.findByBusinessSourceId(id);
                List<ScannedPalletDTO> scannedPalletDtos = new ArrayList<>();

                for (ScanPallet sp : scannedPallets) {
                    ScannedPalletDTO palletDto = new ScannedPalletDTO();
                    palletDto.setSerialPallet(sp.getSerialPallet());

                    List<PalletBoxMapping> mappings =
                        palletBoxMappingRepository.findBySerialPalletOrderByReelIdBoxAsc(
                            sp.getSerialPallet()
                        );
                    List<String> reelIds = mappings
                        .stream()
                        .map(PalletBoxMapping::getReelIdBox)
                        .collect(Collectors.toList());

                    if (!reelIds.isEmpty()) {
                        List<WarehouseTransferDetail> boxes =
                            warehouseTransferDetailRepository.findByReelIdInAndWarehouseTransferSession_Id(
                                reelIds,
                                id
                            );
                        List<WarehouseTransferDetailDTO> boxDtos = boxes
                            .stream()
                            .filter(b -> reelIds.contains(b.getReelId()))
                            .map(b -> {
                                WarehouseTransferDetailDTO dto =
                                    new WarehouseTransferDetailDTO();
                                dto.setId(b.getId());
                                dto.setLineNum(b.getLineNum());
                                dto.setReelId(b.getReelId());
                                dto.setPartNumber(b.getPartNumber());
                                dto.setVendor(b.getVendor());
                                dto.setLot(b.getLot());
                                dto.setInitialQuantity(b.getInitialQuantity());
                                dto.setStatus(b.getStatus());
                                dto.setVendorQrCode(b.getVendorQrCode());
                                dto.setUserData1(b.getUserData1());
                                dto.setUserData2(b.getUserData2());
                                dto.setUserData3(b.getUserData3());
                                dto.setUserData4(b.getUserData4());
                                dto.setUserData5(b.getUserData5());
                                dto.setMsdLevel(b.getMsdLevel());
                                dto.setMsdInitialFloorTime(
                                    b.getMsdInitialFloorTime()
                                );
                                dto.setMsdBagSealDate(b.getMsdBagSealDate());
                                dto.setMarketUsage(b.getMarketUsage());
                                dto.setQuantityOverride(
                                    b.getQuantityOverride()
                                );
                                dto.setShelfTime(b.getShelfTime());
                                dto.setSpMaterialName(b.getSpMaterialName());
                                dto.setWarningLimit(b.getWarningLimit());
                                dto.setMaximumLimit(b.getMaximumLimit());
                                dto.setComments(b.getComments());
                                dto.setWarmupTime(b.getWarmupTime());
                                dto.setStorageUnit(b.getStorageUnit());
                                dto.setSubStorageUnit(b.getSubStorageUnit());
                                dto.setLocationOverride(
                                    b.getLocationOverride()
                                );
                                dto.setExpirationDate(b.getExpirationDate());
                                dto.setManufacturingDate(
                                    b.getManufacturingDate()
                                );
                                dto.setPartClass(b.getPartClass());
                                dto.setSapCode(b.getSapCode());
                                dto.setCreatedBy(b.getCreatedBy());
                                dto.setCreatedAt(b.getCreatedAt());
                                dto.setUpdatedBy(b.getUpdatedBy());
                                dto.setUpdatedAt(b.getUpdatedAt());
                                dto.setPanaSendStatus(b.getPanaSendStatus());
                                dto.setSapSendStatus(b.getSapSendStatus());
                                dto.setOwtqDocEntry(b.getOwtqDocEntry());
                                dto.setWarehouseTransferSessionId(
                                    b.getWarehouseTransferSession() != null
                                        ? b
                                            .getWarehouseTransferSession()
                                            .getId()
                                        : null
                                );
                                return dto;
                            })
                            .collect(Collectors.toList());
                        palletDto.setBoxes(boxDtos);
                    } else {
                        palletDto.setBoxes(new ArrayList<>());
                    }
                    scannedPalletDtos.add(palletDto);
                }
                detailDTO.setScannedPallets(scannedPalletDtos);

                return detailDTO;
            });
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete WarehouseTransferSession : {}", id);
        warehouseTransferSessionRepository.deleteById(id);
    }
}
