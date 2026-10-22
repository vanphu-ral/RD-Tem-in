package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.OwtqWarehouseTransferSession;
import com.mycompany.myapp.domain.WarehouseTransferDetail;
import com.mycompany.myapp.domain.WarehouseTransferSession;
import com.mycompany.myapp.repository.OwtqWarehouseTransferSessionRepository;
import com.mycompany.myapp.repository.WarehouseTransferSessionRepository;
import com.mycompany.myapp.service.WarehouseTransferSessionService;
import com.mycompany.myapp.service.dto.OwtqWarehouseTransferSessionDTO;
import com.mycompany.myapp.service.dto.WarehouseTransferDetailDTO;
import com.mycompany.myapp.service.dto.WarehouseTransferSessionDTO;
import com.mycompany.myapp.service.dto.WarehouseTransferSessionDetailDTO;
import com.mycompany.myapp.service.mapper.WarehouseTransferSessionMapper;
import java.util.List;
import java.util.Optional;
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

    private final WarehouseTransferSessionMapper warehouseTransferSessionMapper;

    public WarehouseTransferSessionServiceImpl(
        WarehouseTransferSessionRepository warehouseTransferSessionRepository,
        OwtqWarehouseTransferSessionRepository owtqWarehouseTransferSessionRepository,
        WarehouseTransferSessionMapper warehouseTransferSessionMapper
    ) {
        this.warehouseTransferSessionRepository =
            warehouseTransferSessionRepository;
        this.owtqWarehouseTransferSessionRepository =
            owtqWarehouseTransferSessionRepository;
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
                        return dto;
                    })
                    .collect(Collectors.toList());

                detailDTO.setDetails(detailDtos);

                return detailDTO;
            });
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete WarehouseTransferSession : {}", id);
        warehouseTransferSessionRepository.deleteById(id);
    }
}
