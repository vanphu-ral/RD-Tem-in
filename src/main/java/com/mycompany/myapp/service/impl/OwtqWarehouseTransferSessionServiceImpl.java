package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.OwtqWarehouseTransferSession;
import com.mycompany.myapp.repository.OwtqWarehouseTransferSessionRepository;
import com.mycompany.myapp.service.OwtqWarehouseTransferSessionService;
import com.mycompany.myapp.service.dto.OwtqWarehouseTransferSessionDTO;
import com.mycompany.myapp.service.mapper.OwtqWarehouseTransferSessionMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.OwtqWarehouseTransferSession}.
 */
@Service
@Transactional
public class OwtqWarehouseTransferSessionServiceImpl
    implements OwtqWarehouseTransferSessionService {

    private final Logger log = LoggerFactory.getLogger(
        OwtqWarehouseTransferSessionServiceImpl.class
    );

    private final OwtqWarehouseTransferSessionRepository owtqWarehouseTransferSessionRepository;

    private final OwtqWarehouseTransferSessionMapper owtqWarehouseTransferSessionMapper;

    public OwtqWarehouseTransferSessionServiceImpl(
        OwtqWarehouseTransferSessionRepository owtqWarehouseTransferSessionRepository,
        OwtqWarehouseTransferSessionMapper owtqWarehouseTransferSessionMapper
    ) {
        this.owtqWarehouseTransferSessionRepository =
            owtqWarehouseTransferSessionRepository;
        this.owtqWarehouseTransferSessionMapper =
            owtqWarehouseTransferSessionMapper;
    }

    @Override
    public OwtqWarehouseTransferSessionDTO save(
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO
    ) {
        log.debug(
            "Request to save OwtqWarehouseTransferSession : {}",
            owtqWarehouseTransferSessionDTO
        );
        OwtqWarehouseTransferSession owtqWarehouseTransferSession =
            owtqWarehouseTransferSessionMapper.toEntity(
                owtqWarehouseTransferSessionDTO
            );
        owtqWarehouseTransferSession =
            owtqWarehouseTransferSessionRepository.save(
                owtqWarehouseTransferSession
            );
        return owtqWarehouseTransferSessionMapper.toDto(
            owtqWarehouseTransferSession
        );
    }

    @Override
    public OwtqWarehouseTransferSessionDTO update(
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO
    ) {
        log.debug(
            "Request to update OwtqWarehouseTransferSession : {}",
            owtqWarehouseTransferSessionDTO
        );
        OwtqWarehouseTransferSession owtqWarehouseTransferSession =
            owtqWarehouseTransferSessionMapper.toEntity(
                owtqWarehouseTransferSessionDTO
            );
        owtqWarehouseTransferSession =
            owtqWarehouseTransferSessionRepository.save(
                owtqWarehouseTransferSession
            );
        return owtqWarehouseTransferSessionMapper.toDto(
            owtqWarehouseTransferSession
        );
    }

    @Override
    public Optional<OwtqWarehouseTransferSessionDTO> partialUpdate(
        OwtqWarehouseTransferSessionDTO owtqWarehouseTransferSessionDTO
    ) {
        log.debug(
            "Request to partially update OwtqWarehouseTransferSession : {}",
            owtqWarehouseTransferSessionDTO
        );

        return owtqWarehouseTransferSessionRepository
            .findById(owtqWarehouseTransferSessionDTO.getId())
            .map(existingOwtqWarehouseTransferSession -> {
                owtqWarehouseTransferSessionMapper.partialUpdate(
                    existingOwtqWarehouseTransferSession,
                    owtqWarehouseTransferSessionDTO
                );

                return existingOwtqWarehouseTransferSession;
            })
            .map(owtqWarehouseTransferSessionRepository::save)
            .map(owtqWarehouseTransferSessionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OwtqWarehouseTransferSessionDTO> findAll() {
        log.debug("Request to get all OwtqWarehouseTransferSessions");
        return owtqWarehouseTransferSessionRepository
            .findAll()
            .stream()
            .map(owtqWarehouseTransferSessionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OwtqWarehouseTransferSessionDTO> findOne(Long id) {
        log.debug("Request to get OwtqWarehouseTransferSession : {}", id);
        return owtqWarehouseTransferSessionRepository
            .findById(id)
            .map(owtqWarehouseTransferSessionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete OwtqWarehouseTransferSession : {}", id);
        owtqWarehouseTransferSessionRepository.deleteById(id);
    }
}
