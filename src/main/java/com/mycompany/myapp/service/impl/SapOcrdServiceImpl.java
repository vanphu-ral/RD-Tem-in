package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.SapOcrd;
import com.mycompany.myapp.repository.partner4.SapOcrdRepository;
import com.mycompany.myapp.service.SapOcrdService;
import com.mycompany.myapp.service.dto.SapOcrdDTO;
import com.mycompany.myapp.service.mapper.SapOcrdMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.SapOcrd}.
 */
@Service
@Transactional
public class SapOcrdServiceImpl implements SapOcrdService {

    private static final Logger LOG = LoggerFactory.getLogger(
        SapOcrdServiceImpl.class
    );

    private final SapOcrdRepository sapOcrdRepository;

    private final SapOcrdMapper sapOcrdMapper;

    public SapOcrdServiceImpl(
        SapOcrdRepository sapOcrdRepository,
        SapOcrdMapper sapOcrdMapper
    ) {
        this.sapOcrdRepository = sapOcrdRepository;
        this.sapOcrdMapper = sapOcrdMapper;
    }

    @Override
    public SapOcrdDTO save(SapOcrdDTO sapOcrdDTO) {
        LOG.debug("Request to save SapOcrd : {}", sapOcrdDTO);
        SapOcrd sapOcrd = sapOcrdMapper.toEntity(sapOcrdDTO);
        sapOcrd = sapOcrdRepository.save(sapOcrd);
        return sapOcrdMapper.toDto(sapOcrd);
    }

    @Override
    public SapOcrdDTO update(SapOcrdDTO sapOcrdDTO) {
        LOG.debug("Request to update SapOcrd : {}", sapOcrdDTO);
        SapOcrd sapOcrd = sapOcrdMapper.toEntity(sapOcrdDTO);
        sapOcrd = sapOcrdRepository.save(sapOcrd);
        return sapOcrdMapper.toDto(sapOcrd);
    }

    @Override
    public Optional<SapOcrdDTO> partialUpdate(SapOcrdDTO sapOcrdDTO) {
        LOG.debug("Request to partially update SapOcrd : {}", sapOcrdDTO);

        return sapOcrdRepository
            .findById(sapOcrdDTO.getId())
            .map(existingSapOcrd -> {
                sapOcrdMapper.partialUpdate(existingSapOcrd, sapOcrdDTO);

                return existingSapOcrd;
            })
            .map(sapOcrdRepository::save)
            .map(sapOcrdMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SapOcrdDTO> findAll() {
        LOG.debug("Request to get all SapOcrds");
        return sapOcrdRepository
            .findAll()
            .stream()
            .map(sapOcrdMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SapOcrdDTO> findOne(Long id) {
        LOG.debug("Request to get SapOcrd : {}", id);
        return sapOcrdRepository.findById(id).map(sapOcrdMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SapOcrd : {}", id);
        sapOcrdRepository.deleteById(id);
    }
}
