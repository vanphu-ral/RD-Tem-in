package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.RdMaterialAttributes;
import com.mycompany.myapp.repository.partner5.RdMaterialAttributesRepository;
import com.mycompany.myapp.service.RdMaterialAttributesService;
import com.mycompany.myapp.service.dto.RdMaterialAttributesDTO;
import com.mycompany.myapp.service.mapper.RdMaterialAttributesMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.RdMaterialAttributes}.
 */
@Service
@Transactional
public class RdMaterialAttributesServiceImpl
    implements RdMaterialAttributesService {

    private static final Logger LOG = LoggerFactory.getLogger(
        RdMaterialAttributesServiceImpl.class
    );

    private final RdMaterialAttributesRepository rdMaterialAttributesRepository;

    private final RdMaterialAttributesMapper rdMaterialAttributesMapper;

    public RdMaterialAttributesServiceImpl(
        RdMaterialAttributesRepository rdMaterialAttributesRepository,
        RdMaterialAttributesMapper rdMaterialAttributesMapper
    ) {
        this.rdMaterialAttributesRepository = rdMaterialAttributesRepository;
        this.rdMaterialAttributesMapper = rdMaterialAttributesMapper;
    }

    @Override
    public RdMaterialAttributesDTO save(
        RdMaterialAttributesDTO rdMaterialAttributesDTO
    ) {
        LOG.debug(
            "Request to save RdMaterialAttributes : {}",
            rdMaterialAttributesDTO
        );
        RdMaterialAttributes rdMaterialAttributes =
            rdMaterialAttributesMapper.toEntity(rdMaterialAttributesDTO);
        rdMaterialAttributes = rdMaterialAttributesRepository.save(
            rdMaterialAttributes
        );
        return rdMaterialAttributesMapper.toDto(rdMaterialAttributes);
    }

    @Override
    public RdMaterialAttributesDTO update(
        RdMaterialAttributesDTO rdMaterialAttributesDTO
    ) {
        LOG.debug(
            "Request to update RdMaterialAttributes : {}",
            rdMaterialAttributesDTO
        );
        RdMaterialAttributes rdMaterialAttributes =
            rdMaterialAttributesMapper.toEntity(rdMaterialAttributesDTO);
        rdMaterialAttributes = rdMaterialAttributesRepository.save(
            rdMaterialAttributes
        );
        return rdMaterialAttributesMapper.toDto(rdMaterialAttributes);
    }

    @Override
    public Optional<RdMaterialAttributesDTO> partialUpdate(
        RdMaterialAttributesDTO rdMaterialAttributesDTO
    ) {
        LOG.debug(
            "Request to partially update RdMaterialAttributes : {}",
            rdMaterialAttributesDTO
        );

        return rdMaterialAttributesRepository
            .findById(rdMaterialAttributesDTO.getId())
            .map(existingRdMaterialAttributes -> {
                rdMaterialAttributesMapper.partialUpdate(
                    existingRdMaterialAttributes,
                    rdMaterialAttributesDTO
                );

                return existingRdMaterialAttributes;
            })
            .map(rdMaterialAttributesRepository::save)
            .map(rdMaterialAttributesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RdMaterialAttributesDTO> findOne(Long id) {
        LOG.debug("Request to get RdMaterialAttributes : {}", id);
        return rdMaterialAttributesRepository
            .findById(id)
            .map(rdMaterialAttributesMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete RdMaterialAttributes : {}", id);
        rdMaterialAttributesRepository.deleteById(id);
    }
}
