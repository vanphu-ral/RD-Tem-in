package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.PoDetail;
import com.mycompany.myapp.repository.partner5.PoDetailRepository;
import com.mycompany.myapp.service.PoDetailService;
import com.mycompany.myapp.service.dto.PoDetailDTO;
import com.mycompany.myapp.service.mapper.PoDetailMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.PoDetail}.
 */
@Service
@Transactional
public class PoDetailServiceImpl implements PoDetailService {

    private static final Logger LOG = LoggerFactory.getLogger(
        PoDetailServiceImpl.class
    );

    private final PoDetailRepository poDetailRepository;

    private final PoDetailMapper poDetailMapper;

    public PoDetailServiceImpl(
        PoDetailRepository poDetailRepository,
        PoDetailMapper poDetailMapper
    ) {
        this.poDetailRepository = poDetailRepository;
        this.poDetailMapper = poDetailMapper;
    }

    @Override
    public PoDetailDTO save(PoDetailDTO poDetailDTO) {
        LOG.debug("Request to save PoDetail : {}", poDetailDTO);
        PoDetail poDetail = poDetailMapper.toEntity(poDetailDTO);
        poDetail = poDetailRepository.save(poDetail);
        return poDetailMapper.toDto(poDetail);
    }

    @Override
    public PoDetailDTO update(PoDetailDTO poDetailDTO) {
        LOG.debug("Request to update PoDetail : {}", poDetailDTO);
        PoDetail poDetail = poDetailMapper.toEntity(poDetailDTO);
        poDetail = poDetailRepository.save(poDetail);
        return poDetailMapper.toDto(poDetail);
    }

    @Override
    public Optional<PoDetailDTO> partialUpdate(PoDetailDTO poDetailDTO) {
        LOG.debug("Request to partially update PoDetail : {}", poDetailDTO);

        return poDetailRepository
            .findById(poDetailDTO.getId())
            .map(existingPoDetail -> {
                poDetailMapper.partialUpdate(existingPoDetail, poDetailDTO);

                return existingPoDetail;
            })
            .map(poDetailRepository::save)
            .map(poDetailMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PoDetailDTO> findOne(Long id) {
        LOG.debug("Request to get PoDetail : {}", id);
        return poDetailRepository.findById(id).map(poDetailMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PoDetail : {}", id);
        poDetailRepository.deleteById(id);
    }

    @Override
    public List<PoDetailDTO> saveAll(List<PoDetailDTO> poDetailDTOs) {
        LOG.debug("Request to save all PoDetails : {}", poDetailDTOs);
        List<PoDetail> poDetails = poDetailDTOs
            .stream()
            .map(poDetailMapper::toEntity)
            .collect(Collectors.toList());
        poDetails = poDetailRepository.saveAll(poDetails);
        return poDetails
            .stream()
            .map(poDetailMapper::toDto)
            .collect(Collectors.toList());
    }
}
