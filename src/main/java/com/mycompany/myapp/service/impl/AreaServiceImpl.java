package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.partner3.AreaRepository;
import com.mycompany.myapp.service.AreaService;
import com.mycompany.myapp.service.dto.AreaDTO;
import com.mycompany.myapp.service.mapper.AreaMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Area.
 */
@Service
@Transactional("partner3TransactionManager")
public class AreaServiceImpl implements AreaService {

    private final Logger log = LoggerFactory.getLogger(AreaServiceImpl.class);

    private final AreaRepository areaRepository;

    private final AreaMapper areaMapper;

    public AreaServiceImpl(
        AreaRepository areaRepository,
        AreaMapper areaMapper
    ) {
        this.areaRepository = areaRepository;
        this.areaMapper = areaMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AreaDTO> findAll() {
        log.debug("Request to get all Areas");
        return areaMapper.toDto(areaRepository.findAll());
    }
}
