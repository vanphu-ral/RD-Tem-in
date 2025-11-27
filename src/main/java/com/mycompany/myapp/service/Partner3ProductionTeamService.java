package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.ProductionTeam;
import com.mycompany.myapp.repository.partner3.Partner3ProductionTeamRepository;
import com.mycompany.myapp.service.dto.ProductionTeamDTO;
import com.mycompany.myapp.service.mapper.ProductionTeamMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProductionTeam}.
 */
@Service
@Transactional
public class Partner3ProductionTeamService {

    private final Logger log = LoggerFactory.getLogger(
        Partner3ProductionTeamService.class
    );

    private final Partner3ProductionTeamRepository partner3ProductionTeamRepository;

    private final ProductionTeamMapper productionTeamMapper;

    public Partner3ProductionTeamService(
        Partner3ProductionTeamRepository partner3ProductionTeamRepository,
        ProductionTeamMapper productionTeamMapper
    ) {
        this.partner3ProductionTeamRepository =
            partner3ProductionTeamRepository;
        this.productionTeamMapper = productionTeamMapper;
    }

    /**
     * Save a productionTeam.
     *
     * @param productionTeamDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductionTeamDTO save(ProductionTeamDTO productionTeamDTO) {
        log.debug("Request to save ProductionTeam : {}", productionTeamDTO);
        ProductionTeam productionTeam = productionTeamMapper.toEntity(
            productionTeamDTO
        );
        productionTeam = partner3ProductionTeamRepository.save(productionTeam);
        return productionTeamMapper.toDto(productionTeam);
    }

    /**
     * Update a productionTeam.
     *
     * @param productionTeamDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductionTeamDTO update(ProductionTeamDTO productionTeamDTO) {
        log.debug("Request to update ProductionTeam : {}", productionTeamDTO);
        ProductionTeam productionTeam = productionTeamMapper.toEntity(
            productionTeamDTO
        );
        productionTeam = partner3ProductionTeamRepository.save(productionTeam);
        return productionTeamMapper.toDto(productionTeam);
    }

    /**
     * Partially update a productionTeam.
     *
     * @param productionTeamDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductionTeamDTO> partialUpdate(
        ProductionTeamDTO productionTeamDTO
    ) {
        log.debug(
            "Request to partially update ProductionTeam : {}",
            productionTeamDTO
        );

        return partner3ProductionTeamRepository
            .findById(productionTeamDTO.getId())
            .map(existingProductionTeam -> {
                productionTeamMapper.partialUpdate(
                    existingProductionTeam,
                    productionTeamDTO
                );

                return existingProductionTeam;
            })
            .map(partner3ProductionTeamRepository::save)
            .map(productionTeamMapper::toDto);
    }

    /**
     * Get all the productionTeams.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductionTeamDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductionTeams");
        return partner3ProductionTeamRepository
            .findAll(pageable)
            .map(productionTeamMapper::toDto);
    }

    /**
     * Get one productionTeam by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductionTeamDTO> findOne(Long id) {
        log.debug("Request to get ProductionTeam : {}", id);
        return partner3ProductionTeamRepository
            .findById(id)
            .map(productionTeamMapper::toDto);
    }

    /**
     * Delete the productionTeam by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ProductionTeam : {}", id);
        partner3ProductionTeamRepository.deleteById(id);
    }

    /**
     * Check if the productionTeam exists by id.
     *
     * @param id the id of the entity.
     * @return true if exists, false otherwise.
     */
    public boolean exists(Long id) {
        log.debug("Request to check if ProductionTeam exists : {}", id);
        return partner3ProductionTeamRepository.existsById(id);
    }
}
