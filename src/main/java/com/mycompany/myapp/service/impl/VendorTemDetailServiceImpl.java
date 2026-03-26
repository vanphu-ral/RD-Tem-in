package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.VendorTemDetail;
import com.mycompany.myapp.repository.partner5.VendorTemDetailRepository;
import com.mycompany.myapp.service.VendorTemDetailService;
import com.mycompany.myapp.service.dto.VendorTemDetailDTO;
import com.mycompany.myapp.service.mapper.VendorTemDetailMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link com.mycompany.myapp.domain.VendorTemDetail}.
 */
@Service
@Transactional
public class VendorTemDetailServiceImpl implements VendorTemDetailService {

    private static final Logger LOG = LoggerFactory.getLogger(
        VendorTemDetailServiceImpl.class
    );

    private final VendorTemDetailRepository vendorTemDetailRepository;

    private final VendorTemDetailMapper vendorTemDetailMapper;

    public VendorTemDetailServiceImpl(
        VendorTemDetailRepository vendorTemDetailRepository,
        VendorTemDetailMapper vendorTemDetailMapper
    ) {
        this.vendorTemDetailRepository = vendorTemDetailRepository;
        this.vendorTemDetailMapper = vendorTemDetailMapper;
    }

    @Override
    public VendorTemDetailDTO save(VendorTemDetailDTO vendorTemDetailDTO) {
        LOG.debug("Request to save VendorTemDetail : {}", vendorTemDetailDTO);
        VendorTemDetail vendorTemDetail = vendorTemDetailMapper.toEntity(
            vendorTemDetailDTO
        );
        vendorTemDetail = vendorTemDetailRepository.save(vendorTemDetail);
        return vendorTemDetailMapper.toDto(vendorTemDetail);
    }

    @Override
    public VendorTemDetailDTO update(VendorTemDetailDTO vendorTemDetailDTO) {
        LOG.debug("Request to update VendorTemDetail : {}", vendorTemDetailDTO);
        VendorTemDetail vendorTemDetail = vendorTemDetailMapper.toEntity(
            vendorTemDetailDTO
        );
        vendorTemDetail = vendorTemDetailRepository.save(vendorTemDetail);
        return vendorTemDetailMapper.toDto(vendorTemDetail);
    }

    @Override
    public List<VendorTemDetailDTO> updateBatch(
        List<VendorTemDetailDTO> vendorTemDetailDTOs
    ) {
        LOG.debug(
            "Request to update batch VendorTemDetails : {}",
            vendorTemDetailDTOs
        );
        List<VendorTemDetailDTO> result = new ArrayList<>();
        for (VendorTemDetailDTO dto : vendorTemDetailDTOs) {
            VendorTemDetail vendorTemDetail = vendorTemDetailMapper.toEntity(
                dto
            );
            vendorTemDetail = vendorTemDetailRepository.save(vendorTemDetail);
            result.add(vendorTemDetailMapper.toDto(vendorTemDetail));
        }
        return result;
    }

    @Override
    public Optional<VendorTemDetailDTO> partialUpdate(
        VendorTemDetailDTO vendorTemDetailDTO
    ) {
        LOG.debug(
            "Request to partially update VendorTemDetail : {}",
            vendorTemDetailDTO
        );

        return vendorTemDetailRepository
            .findById(vendorTemDetailDTO.getId())
            .map(existingVendorTemDetail -> {
                vendorTemDetailMapper.partialUpdate(
                    existingVendorTemDetail,
                    vendorTemDetailDTO
                );

                return existingVendorTemDetail;
            })
            .map(vendorTemDetailRepository::save)
            .map(vendorTemDetailMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VendorTemDetailDTO> findOne(Long id) {
        LOG.debug("Request to get VendorTemDetail : {}", id);
        return vendorTemDetailRepository
            .findById(id)
            .map(vendorTemDetailMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete VendorTemDetail : {}", id);
        vendorTemDetailRepository.deleteById(id);
    }
}
