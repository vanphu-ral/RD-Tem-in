package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.WarehouseStampInfo;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoRepository;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link com.mycompany.myapp.domain.WarehouseStampInfo} using partner3
 * datasource.
 */
@Service
@Transactional("partner3TransactionManager")
public class Partner3WarehouseStampInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(
        Partner3WarehouseStampInfoService.class
    );

    private final Partner3WarehouseStampInfoRepository partner3WarehouseStampInfoRepository;

    private final WarehouseStampInfoMapper warehouseStampInfoMapper;

    public Partner3WarehouseStampInfoService(
        Partner3WarehouseStampInfoRepository partner3WarehouseStampInfoRepository,
        WarehouseStampInfoMapper warehouseStampInfoMapper
    ) {
        this.partner3WarehouseStampInfoRepository =
            partner3WarehouseStampInfoRepository;
        this.warehouseStampInfoMapper = warehouseStampInfoMapper;
    }

    /**
     * Get all warehouseStampInfos by createBy.
     *
     * @param createBy the createBy to filter by.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WarehouseStampInfoDTO> findByCreateBy(String createBy) {
        LOG.debug(
            "Request to get WarehouseStampInfos by createBy : {}",
            createBy
        );
        return partner3WarehouseStampInfoRepository
            .findBycreateBy(createBy)
            .stream()
            .map(warehouseStampInfoMapper::toDto)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get all warehouseStampInfos by approverBy.
     *
     * @param approverBy the approverBy to filter by.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WarehouseStampInfoDTO> findByApproverBy(String approverBy) {
        LOG.debug(
            "Request to get WarehouseStampInfos by approverBy : {}",
            approverBy
        );
        return partner3WarehouseStampInfoRepository
            .findByApproverBy(approverBy)
            .stream()
            .map(warehouseStampInfoMapper::toDto)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get one warehouseStampInfo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WarehouseStampInfoDTO> findOne(Long id) {
        LOG.debug("Request to get WarehouseStampInfo : {}", id);
        return partner3WarehouseStampInfoRepository
            .findById(id)
            .map(warehouseStampInfoMapper::toDto);
    }
}
