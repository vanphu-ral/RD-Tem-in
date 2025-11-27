package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.ProductionTeam;
import com.mycompany.myapp.domain.Workshop;
import com.mycompany.myapp.domain.branch;
import com.mycompany.myapp.repository.partner3.Partner3WorkshopRepository;
import com.mycompany.myapp.service.dto.ProductionTeamDTO;
import com.mycompany.myapp.service.dto.WorkshopDTO;
import com.mycompany.myapp.service.dto.WorkshopHierarchyDTO;
import com.mycompany.myapp.service.dto.branchHierarchyDTO;
import com.mycompany.myapp.service.mapper.WorkshopMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link com.mycompany.myapp.domain.Workshop} using partner3 datasource.
 */
@Service
@Transactional("partner3TransactionManager")
public class Partner3WorkshopService {

    private static final Logger LOG = LoggerFactory.getLogger(
        Partner3WorkshopService.class
    );

    private final Partner3WorkshopRepository partner3WorkshopRepository;

    private final WorkshopMapper workshopMapper;

    public Partner3WorkshopService(
        Partner3WorkshopRepository partner3WorkshopRepository,
        WorkshopMapper workshopMapper
    ) {
        this.partner3WorkshopRepository = partner3WorkshopRepository;
        this.workshopMapper = workshopMapper;
    }

    /**
     * Get all workshops.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WorkshopDTO> findAll() {
        LOG.debug("Request to get all Workshops");
        return partner3WorkshopRepository
            .findAll()
            .stream()
            .map(workshopMapper::toDto)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get one workshop by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WorkshopDTO> findOne(Long id) {
        LOG.debug("Request to get Workshop : {}", id);
        return partner3WorkshopRepository
            .findById(id)
            .map(workshopMapper::toDto);
    }

    /**
     * Get all workshops with hierarchy.
     *
     * @return the list of workshop hierarchy DTOs.
     */
    @Transactional(readOnly = true)
    public List<WorkshopHierarchyDTO> findAllWithHierarchy() {
        LOG.debug("Request to get all Workshops with hierarchy");
        return partner3WorkshopRepository
            .findAllWithHierarchy()
            .stream()
            .map(this::toHierarchyDto)
            .collect(java.util.stream.Collectors.toList());
    }

    private WorkshopHierarchyDTO toHierarchyDto(Workshop workshop) {
        WorkshopHierarchyDTO dto = new WorkshopHierarchyDTO();
        dto.setId(workshop.getId());
        dto.setWorkshopCode(workshop.getWorkshopCode());
        dto.setWorkShopName(workshop.getWorkShopName());
        dto.setDescription(workshop.getDescription());

        if (workshop.getbranchs() != null) {
            dto.setbranchs(
                workshop
                    .getbranchs()
                    .stream()
                    .map(this::tobranchHierarchyDto)
                    .collect(java.util.stream.Collectors.toList())
            );
        }

        return dto;
    }

    private branchHierarchyDTO tobranchHierarchyDto(branch branch) {
        branchHierarchyDTO dto = new branchHierarchyDTO();
        dto.setId(branch.getId());
        dto.setWorkshopCode(branch.getWorkshopCode());
        dto.setBranchCode(branch.getBranchCode());
        dto.setBranchName(branch.getBranchName());

        if (branch.getProductionTeams() != null) {
            dto.setProductionTeams(
                branch
                    .getProductionTeams()
                    .stream()
                    .map(productionTeam -> {
                        ProductionTeamDTO productionTeamDto =
                            new ProductionTeamDTO();
                        productionTeamDto.setId(productionTeam.getId());
                        productionTeamDto.setBranchCode(
                            productionTeam.getBranchCode()
                        );
                        productionTeamDto.setProductionTeamCode(
                            productionTeam.getProductionTeamCode()
                        );
                        productionTeamDto.setProductionTeamName(
                            productionTeam.getProductionTeamName()
                        );
                        return productionTeamDto;
                    })
                    .collect(java.util.stream.Collectors.toList())
            );
        }

        return dto;
    }
}
