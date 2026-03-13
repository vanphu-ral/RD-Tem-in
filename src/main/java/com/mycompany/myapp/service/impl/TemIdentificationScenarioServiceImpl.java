package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.TemIdentificationScenario;
import com.mycompany.myapp.repository.partner5.TemIdentificationScenarioRepository;
import com.mycompany.myapp.service.TemIdentificationScenarioService;
import com.mycompany.myapp.service.dto.TemIdentificationScenarioDTO;
import com.mycompany.myapp.service.mapper.TemIdentificationScenarioMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TemIdentificationScenario}.
 */
@Service
@Transactional
public class TemIdentificationScenarioServiceImpl
    implements TemIdentificationScenarioService {

    private static final Logger LOG = LoggerFactory.getLogger(
        TemIdentificationScenarioServiceImpl.class
    );

    private final TemIdentificationScenarioRepository temIdentificationScenarioRepository;

    private final TemIdentificationScenarioMapper temIdentificationScenarioMapper;

    public TemIdentificationScenarioServiceImpl(
        TemIdentificationScenarioRepository temIdentificationScenarioRepository,
        TemIdentificationScenarioMapper temIdentificationScenarioMapper
    ) {
        this.temIdentificationScenarioRepository =
            temIdentificationScenarioRepository;
        this.temIdentificationScenarioMapper = temIdentificationScenarioMapper;
    }

    @Override
    public TemIdentificationScenarioDTO save(
        TemIdentificationScenarioDTO temIdentificationScenarioDTO
    ) {
        LOG.debug(
            "Request to save TemIdentificationScenario : {}",
            temIdentificationScenarioDTO
        );
        TemIdentificationScenario temIdentificationScenario =
            temIdentificationScenarioMapper.toEntity(
                temIdentificationScenarioDTO
            );
        temIdentificationScenario = temIdentificationScenarioRepository.save(
            temIdentificationScenario
        );
        return temIdentificationScenarioMapper.toDto(temIdentificationScenario);
    }

    @Override
    public TemIdentificationScenarioDTO update(
        TemIdentificationScenarioDTO temIdentificationScenarioDTO
    ) {
        LOG.debug(
            "Request to update TemIdentificationScenario : {}",
            temIdentificationScenarioDTO
        );
        TemIdentificationScenario temIdentificationScenario =
            temIdentificationScenarioMapper.toEntity(
                temIdentificationScenarioDTO
            );
        temIdentificationScenario = temIdentificationScenarioRepository.save(
            temIdentificationScenario
        );
        return temIdentificationScenarioMapper.toDto(temIdentificationScenario);
    }

    @Override
    public Optional<TemIdentificationScenarioDTO> partialUpdate(
        TemIdentificationScenarioDTO temIdentificationScenarioDTO
    ) {
        LOG.debug(
            "Request to partially update TemIdentificationScenario : {}",
            temIdentificationScenarioDTO
        );

        return temIdentificationScenarioRepository
            .findById(temIdentificationScenarioDTO.getId())
            .map(existingTemIdentificationScenario -> {
                temIdentificationScenarioMapper.partialUpdate(
                    existingTemIdentificationScenario,
                    temIdentificationScenarioDTO
                );

                return existingTemIdentificationScenario;
            })
            .map(temIdentificationScenarioRepository::save)
            .map(temIdentificationScenarioMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TemIdentificationScenarioDTO> findOne(Long id) {
        LOG.debug("Request to get TemIdentificationScenario : {}", id);
        return temIdentificationScenarioRepository
            .findById(id)
            .map(temIdentificationScenarioMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TemIdentificationScenario : {}", id);
        temIdentificationScenarioRepository.deleteById(id);
    }
}
