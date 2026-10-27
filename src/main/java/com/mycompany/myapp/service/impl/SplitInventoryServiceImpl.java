package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.SplitInventory;
import com.mycompany.myapp.repository.SplitInventoryRepository;
import com.mycompany.myapp.service.SplitInventoryService;
import com.mycompany.myapp.service.dto.SplitInventoryDTO;
import com.mycompany.myapp.service.mapper.SplitInventoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.SplitInventory}.
 */
@Service
@Transactional
public class SplitInventoryServiceImpl implements SplitInventoryService {

    private final Logger log = LoggerFactory.getLogger(
        SplitInventoryServiceImpl.class
    );

    private final SplitInventoryRepository splitInventoryRepository;

    private final SplitInventoryMapper splitInventoryMapper;

    public SplitInventoryServiceImpl(
        SplitInventoryRepository splitInventoryRepository,
        SplitInventoryMapper splitInventoryMapper
    ) {
        this.splitInventoryRepository = splitInventoryRepository;
        this.splitInventoryMapper = splitInventoryMapper;
    }

    @Override
    public SplitInventoryDTO save(SplitInventoryDTO splitInventoryDTO) {
        log.debug("Request to save SplitInventory : {}", splitInventoryDTO);
        SplitInventory splitInventory = splitInventoryMapper.toEntity(
            splitInventoryDTO
        );
        splitInventory = splitInventoryRepository.save(splitInventory);
        return splitInventoryMapper.toDto(splitInventory);
    }

    @Override
    public SplitInventoryDTO update(SplitInventoryDTO splitInventoryDTO) {
        log.debug("Request to update SplitInventory : {}", splitInventoryDTO);
        SplitInventory splitInventory = splitInventoryMapper.toEntity(
            splitInventoryDTO
        );
        splitInventory = splitInventoryRepository.save(splitInventory);
        return splitInventoryMapper.toDto(splitInventory);
    }

    @Override
    public Optional<SplitInventoryDTO> partialUpdate(
        SplitInventoryDTO splitInventoryDTO
    ) {
        log.debug(
            "Request to partially update SplitInventory : {}",
            splitInventoryDTO
        );

        return splitInventoryRepository
            .findById(splitInventoryDTO.getId())
            .map(existingSplitInventory -> {
                splitInventoryMapper.partialUpdate(
                    existingSplitInventory,
                    splitInventoryDTO
                );

                return existingSplitInventory;
            })
            .map(splitInventoryRepository::save)
            .map(splitInventoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SplitInventoryDTO> findOne(Long id) {
        log.debug("Request to get SplitInventory : {}", id);
        return splitInventoryRepository
            .findById(id)
            .map(splitInventoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete SplitInventory : {}", id);
        splitInventoryRepository.deleteById(id);
    }
}
