package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.partner3.Partner3branchRepository;
import com.mycompany.myapp.service.dto.branchDTO;
import com.mycompany.myapp.service.mapper.branchMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.branch}
 * using partner3 datasource.
 */
@Service
@Transactional("partner3TransactionManager")
public class Partner3branchService {

    private static final Logger LOG = LoggerFactory.getLogger(
        Partner3branchService.class
    );

    private final Partner3branchRepository partner3branchRepository;

    private final branchMapper branchMapper;

    public Partner3branchService(
        Partner3branchRepository partner3branchRepository,
        branchMapper branchMapper
    ) {
        this.partner3branchRepository = partner3branchRepository;
        this.branchMapper = branchMapper;
    }

    /**
     * Get all branchs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<branchDTO> findAll() {
        LOG.debug("Request to get all branch");
        return partner3branchRepository
            .findAll()
            .stream()
            .map(branchMapper::toDto)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get one branch by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<branchDTO> findOne(Long id) {
        LOG.debug("Request to get branch : {}", id);
        return partner3branchRepository.findById(id).map(branchMapper::toDto);
    }
}
