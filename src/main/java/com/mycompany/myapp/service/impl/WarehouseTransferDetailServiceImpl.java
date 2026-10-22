package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.WarehouseTransferDetail;
import com.mycompany.myapp.repository.WarehouseTransferDetailRepository;
import com.mycompany.myapp.service.WarehouseTransferDetailService;
import com.mycompany.myapp.service.dto.WarehouseTransferDetailDTO;
import com.mycompany.myapp.service.dto.WarehouseTransferSummaryDTO;
import com.mycompany.myapp.service.mapper.WarehouseTransferDetailMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.WarehouseTransferDetail}.
 */
@Service
@Transactional
public class WarehouseTransferDetailServiceImpl
    implements WarehouseTransferDetailService {

    private final Logger log = LoggerFactory.getLogger(
        WarehouseTransferDetailServiceImpl.class
    );

    private final WarehouseTransferDetailRepository warehouseTransferDetailRepository;

    private final WarehouseTransferDetailMapper warehouseTransferDetailMapper;

    public WarehouseTransferDetailServiceImpl(
        WarehouseTransferDetailRepository warehouseTransferDetailRepository,
        WarehouseTransferDetailMapper warehouseTransferDetailMapper
    ) {
        this.warehouseTransferDetailRepository =
            warehouseTransferDetailRepository;
        this.warehouseTransferDetailMapper = warehouseTransferDetailMapper;
    }

    @Override
    public WarehouseTransferDetailDTO save(
        WarehouseTransferDetailDTO warehouseTransferDetailDTO
    ) {
        log.debug(
            "Request to save WarehouseTransferDetail : {}",
            warehouseTransferDetailDTO
        );
        WarehouseTransferDetail warehouseTransferDetail =
            warehouseTransferDetailMapper.toEntity(warehouseTransferDetailDTO);
        warehouseTransferDetail = warehouseTransferDetailRepository.save(
            warehouseTransferDetail
        );
        return warehouseTransferDetailMapper.toDto(warehouseTransferDetail);
    }

    @Override
    public WarehouseTransferDetailDTO update(
        WarehouseTransferDetailDTO warehouseTransferDetailDTO
    ) {
        log.debug(
            "Request to update WarehouseTransferDetail : {}",
            warehouseTransferDetailDTO
        );
        WarehouseTransferDetail warehouseTransferDetail =
            warehouseTransferDetailMapper.toEntity(warehouseTransferDetailDTO);
        warehouseTransferDetail = warehouseTransferDetailRepository.save(
            warehouseTransferDetail
        );
        return warehouseTransferDetailMapper.toDto(warehouseTransferDetail);
    }

    @Override
    public Optional<WarehouseTransferDetailDTO> partialUpdate(
        WarehouseTransferDetailDTO warehouseTransferDetailDTO
    ) {
        log.debug(
            "Request to partially update WarehouseTransferDetail : {}",
            warehouseTransferDetailDTO
        );

        return warehouseTransferDetailRepository
            .findById(warehouseTransferDetailDTO.getId())
            .map(existingWarehouseTransferDetail -> {
                warehouseTransferDetailMapper.partialUpdate(
                    existingWarehouseTransferDetail,
                    warehouseTransferDetailDTO
                );

                return existingWarehouseTransferDetail;
            })
            .map(warehouseTransferDetailRepository::save)
            .map(warehouseTransferDetailMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseTransferDetailDTO> findAll() {
        log.debug("Request to get all WarehouseTransferDetails");
        return warehouseTransferDetailRepository
            .findAll()
            .stream()
            .map(warehouseTransferDetailMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseTransferDetailDTO> findOne(Long id) {
        log.debug("Request to get WarehouseTransferDetail : {}", id);
        return warehouseTransferDetailRepository
            .findById(id)
            .map(warehouseTransferDetailMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete WarehouseTransferDetail : {}", id);
        warehouseTransferDetailRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseTransferSummaryDTO getSummaryByOwtqDocEntry(
        Long owtqDocEntry
    ) {
        log.debug(
            "Request a WarehouseTransfer summary for owtqDocEntry : {}",
            owtqDocEntry
        );
        long count = warehouseTransferDetailRepository.countByOwtqDocEntry(
            owtqDocEntry
        );
        Long sum =
            warehouseTransferDetailRepository.sumInitialQuantityByOwtqDocEntry(
                owtqDocEntry
            );

        return new WarehouseTransferSummaryDTO(
            owtqDocEntry,
            count,
            sum != null ? sum : 0L
        );
    }
}
