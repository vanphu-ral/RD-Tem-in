package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.AttributesType;
import com.mycompany.myapp.repository.partner5.AttributesTypeRepository;
import com.mycompany.myapp.service.AttributesTypeService;
import com.mycompany.myapp.service.dto.AttributesTypeDTO;
import com.mycompany.myapp.service.mapper.AttributesTypeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.AttributesType}.
 */
@Service
@Transactional
public class AttributesTypeServiceImpl implements AttributesTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(
        AttributesTypeServiceImpl.class
    );

    private final AttributesTypeRepository attributesTypeRepository;

    private final AttributesTypeMapper attributesTypeMapper;

    public AttributesTypeServiceImpl(
        AttributesTypeRepository attributesTypeRepository,
        AttributesTypeMapper attributesTypeMapper
    ) {
        this.attributesTypeRepository = attributesTypeRepository;
        this.attributesTypeMapper = attributesTypeMapper;
    }

    @Override
    public AttributesTypeDTO save(AttributesTypeDTO attributesTypeDTO) {
        LOG.debug("Request to save AttributesType : {}", attributesTypeDTO);
        AttributesType attributesType = attributesTypeMapper.toEntity(
            attributesTypeDTO
        );
        attributesType = attributesTypeRepository.save(attributesType);
        return attributesTypeMapper.toDto(attributesType);
    }

    @Override
    public AttributesTypeDTO update(AttributesTypeDTO attributesTypeDTO) {
        LOG.debug("Request to update AttributesType : {}", attributesTypeDTO);
        AttributesType attributesType = attributesTypeMapper.toEntity(
            attributesTypeDTO
        );
        attributesType = attributesTypeRepository.save(attributesType);
        return attributesTypeMapper.toDto(attributesType);
    }

    @Override
    public Optional<AttributesTypeDTO> partialUpdate(
        AttributesTypeDTO attributesTypeDTO
    ) {
        LOG.debug(
            "Request to partially update AttributesType : {}",
            attributesTypeDTO
        );

        return attributesTypeRepository
            .findById(attributesTypeDTO.getId())
            .map(existingAttributesType -> {
                attributesTypeMapper.partialUpdate(
                    existingAttributesType,
                    attributesTypeDTO
                );

                return existingAttributesType;
            })
            .map(attributesTypeRepository::save)
            .map(attributesTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AttributesTypeDTO> findOne(Long id) {
        LOG.debug("Request to get AttributesType : {}", id);
        return attributesTypeRepository
            .findById(id)
            .map(attributesTypeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AttributesType : {}", id);
        attributesTypeRepository.deleteById(id);
    }
}
