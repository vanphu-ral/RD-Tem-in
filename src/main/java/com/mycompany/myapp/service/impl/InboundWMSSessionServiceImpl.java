package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.InboundWMSSession;
import com.mycompany.myapp.repository.partner3.InboundWMSSessionRepository;
import com.mycompany.myapp.service.InboundWMSSessionService;
import com.mycompany.myapp.service.dto.InboundWMSSessionDTO;
import com.mycompany.myapp.service.mapper.InboundWMSSessionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.InboundWMSSession}.
 */
@Service
@Transactional
public class InboundWMSSessionServiceImpl implements InboundWMSSessionService {

    private static final Logger LOG = LoggerFactory.getLogger(
        InboundWMSSessionServiceImpl.class
    );

    private final InboundWMSSessionRepository inboundWMSSessionRepository;

    private final InboundWMSSessionMapper inboundWMSSessionMapper;

    public InboundWMSSessionServiceImpl(
        InboundWMSSessionRepository inboundWMSSessionRepository,
        InboundWMSSessionMapper inboundWMSSessionMapper
    ) {
        this.inboundWMSSessionRepository = inboundWMSSessionRepository;
        this.inboundWMSSessionMapper = inboundWMSSessionMapper;
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
    public Page<InboundWMSSessionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all InboundWMSSessions");
        return inboundWMSSessionRepository
            .findAll(pageable)
            .map(inboundWMSSessionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InboundWMSSessionDTO> findOne(Long id) {
        LOG.debug("Request to get InboundWMSSession : {}", id);
        return inboundWMSSessionRepository
            .findById(id)
            .map(inboundWMSSessionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete InboundWMSSession : {}", id);
        inboundWMSSessionRepository.deleteById(id);
    }
}
