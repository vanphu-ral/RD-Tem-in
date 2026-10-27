package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.ScanPallet;
import com.mycompany.myapp.repository.ScanPalletRepository;
import com.mycompany.myapp.service.ScanPalletService;
import com.mycompany.myapp.service.dto.ScanPalletDTO;
import com.mycompany.myapp.service.mapper.ScanPalletMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.ScanPallet}.
 */
@Service
@Transactional
public class ScanPalletServiceImpl implements ScanPalletService {

    private final Logger log = LoggerFactory.getLogger(
        ScanPalletServiceImpl.class
    );

    private final ScanPalletRepository scanPalletRepository;

    private final ScanPalletMapper scanPalletMapper;

    public ScanPalletServiceImpl(
        ScanPalletRepository scanPalletRepository,
        ScanPalletMapper scanPalletMapper
    ) {
        this.scanPalletRepository = scanPalletRepository;
        this.scanPalletMapper = scanPalletMapper;
    }

    @Override
    public ScanPalletDTO save(ScanPalletDTO scanPalletDTO) {
        log.debug("Request to save ScanPallet : {}", scanPalletDTO);
        ScanPallet scanPallet = scanPalletMapper.toEntity(scanPalletDTO);
        scanPallet = scanPalletRepository.save(scanPallet);
        return scanPalletMapper.toDto(scanPallet);
    }

    @Override
    public ScanPalletDTO update(ScanPalletDTO scanPalletDTO) {
        log.debug("Request to update ScanPallet : {}", scanPalletDTO);
        ScanPallet scanPallet = scanPalletMapper.toEntity(scanPalletDTO);
        scanPallet = scanPalletRepository.save(scanPallet);
        return scanPalletMapper.toDto(scanPallet);
    }

    @Override
    public Optional<ScanPalletDTO> partialUpdate(ScanPalletDTO scanPalletDTO) {
        log.debug("Request to partially update ScanPallet : {}", scanPalletDTO);

        return scanPalletRepository
            .findById(scanPalletDTO.getId())
            .map(existingScanPallet -> {
                scanPalletMapper.partialUpdate(
                    existingScanPallet,
                    scanPalletDTO
                );

                return existingScanPallet;
            })
            .map(scanPalletRepository::save)
            .map(scanPalletMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScanPalletDTO> findAll() {
        log.debug("Request to get all ScanPallets");
        return scanPalletRepository
            .findAll()
            .stream()
            .map(scanPalletMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScanPalletDTO> findOne(Long id) {
        log.debug("Request to get ScanPallet : {}", id);
        return scanPalletRepository.findById(id).map(scanPalletMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ScanPallet : {}", id);
        scanPalletRepository.deleteById(id);
    }
}
