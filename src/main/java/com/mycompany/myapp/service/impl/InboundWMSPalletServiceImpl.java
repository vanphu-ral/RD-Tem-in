package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.InboundWMSPallet;
import com.mycompany.myapp.repository.partner3.InboundWMSPalletRepository;
import com.mycompany.myapp.service.InboundWMSPalletService;
import com.mycompany.myapp.service.dto.InboundWMSPalletDTO;
import com.mycompany.myapp.service.mapper.InboundWMSPalletMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.InboundWMSPallet}.
 */
@Service
@Transactional
public class InboundWMSPalletServiceImpl implements InboundWMSPalletService {

    private static final Logger LOG = LoggerFactory.getLogger(
        InboundWMSPalletServiceImpl.class
    );

    private final InboundWMSPalletRepository inboundWMSPalletRepository;

    private final InboundWMSPalletMapper inboundWMSPalletMapper;

    public InboundWMSPalletServiceImpl(
        InboundWMSPalletRepository inboundWMSPalletRepository,
        InboundWMSPalletMapper inboundWMSPalletMapper
    ) {
        this.inboundWMSPalletRepository = inboundWMSPalletRepository;
        this.inboundWMSPalletMapper = inboundWMSPalletMapper;
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
}
