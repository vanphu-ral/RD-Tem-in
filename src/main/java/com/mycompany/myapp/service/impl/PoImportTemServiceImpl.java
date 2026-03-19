package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.PoImportTem;
import com.mycompany.myapp.repository.partner5.PoImportTemRepository;
import com.mycompany.myapp.service.PoImportTemService;
import com.mycompany.myapp.service.dto.PoImportTemDTO;
import com.mycompany.myapp.service.mapper.PoImportTemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.PoImportTem}.
 */
@Service
@Transactional
public class PoImportTemServiceImpl implements PoImportTemService {

    private static final Logger LOG = LoggerFactory.getLogger(
        PoImportTemServiceImpl.class
    );

    private final PoImportTemRepository poImportTemRepository;

    private final PoImportTemMapper poImportTemMapper;

    public PoImportTemServiceImpl(
        PoImportTemRepository poImportTemRepository,
        PoImportTemMapper poImportTemMapper
    ) {
        this.poImportTemRepository = poImportTemRepository;
        this.poImportTemMapper = poImportTemMapper;
    }

    @Override
    public PoImportTemDTO save(PoImportTemDTO poImportTemDTO) {
        LOG.debug("Request to save PoImportTem : {}", poImportTemDTO);
        PoImportTem poImportTem = poImportTemMapper.toEntity(poImportTemDTO);
        poImportTem = poImportTemRepository.save(poImportTem);
        return poImportTemMapper.toDto(poImportTem);
    }

    @Override
    public PoImportTemDTO update(PoImportTemDTO poImportTemDTO) {
        LOG.debug("Request to update PoImportTem : {}", poImportTemDTO);
        PoImportTem poImportTem = poImportTemMapper.toEntity(poImportTemDTO);
        poImportTem = poImportTemRepository.save(poImportTem);
        return poImportTemMapper.toDto(poImportTem);
    }

    @Override
    public Optional<PoImportTemDTO> partialUpdate(
        PoImportTemDTO poImportTemDTO
    ) {
        LOG.debug(
            "Request to partially update PoImportTem : {}",
            poImportTemDTO
        );

        return poImportTemRepository
            .findById(poImportTemDTO.getId())
            .map(existingPoImportTem -> {
                poImportTemMapper.partialUpdate(
                    existingPoImportTem,
                    poImportTemDTO
                );

                return existingPoImportTem;
            })
            .map(poImportTemRepository::save)
            .map(poImportTemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PoImportTemDTO> findOne(Long id) {
        LOG.debug("Request to get PoImportTem : {}", id);
        return poImportTemRepository.findById(id).map(poImportTemMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PoImportTem : {}", id);
        poImportTemRepository.deleteById(id);
    }
}
