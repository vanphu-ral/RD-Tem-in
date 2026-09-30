package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.PalletBoxMapping;
import com.mycompany.myapp.domain.PalletMngt;
import com.mycompany.myapp.domain.VendorLabelInfo;
import com.mycompany.myapp.repository.PalletBoxMappingRepository;
import com.mycompany.myapp.repository.PalletMngtRepository;
import com.mycompany.myapp.service.dto.PalletBoxMappingDTO;
import com.mycompany.myapp.service.dto.PalletMngtDTO;
import com.mycompany.myapp.service.dto.PalletMngtDetailDTO;
import com.mycompany.myapp.service.dto.PalletVendorLabelInfoItemDTO;
import com.mycompany.myapp.service.mapper.PalletMngtMapper;
import com.mycompany.myapp.service.mapper.VendorLabelInfoMapper;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.PalletMngt}.
 */
@Service
@Transactional("partner5TransactionManager")
public class PalletMngtService {

    private static final Logger LOG = LoggerFactory.getLogger(
        PalletMngtService.class
    );

    private final PalletMngtRepository palletMngtRepository;

    private final PalletMngtMapper palletMngtMapper;

    private final PalletBoxMappingRepository palletBoxMappingRepository;

    private final VendorLabelInfoMapper vendorLabelInfoMapper;

    public PalletMngtService(
        PalletMngtRepository palletMngtRepository,
        PalletMngtMapper palletMngtMapper,
        PalletBoxMappingRepository palletBoxMappingRepository,
        VendorLabelInfoMapper vendorLabelInfoMapper
    ) {
        this.palletMngtRepository = palletMngtRepository;
        this.palletMngtMapper = palletMngtMapper;
        this.palletBoxMappingRepository = palletBoxMappingRepository;
        this.vendorLabelInfoMapper = vendorLabelInfoMapper;
    }

    /**
     * Save a palletMngt.
     *
     * @param palletMngtDTO the entity to save.
     * @return the persisted entity.
     */
    public PalletMngtDTO save(PalletMngtDTO palletMngtDTO) {
        LOG.debug("Request to save PalletMngt : {}", palletMngtDTO);
        PalletMngt palletMngt = palletMngtMapper.toEntity(palletMngtDTO);
        palletMngt = palletMngtRepository.save(palletMngt);
        return palletMngtMapper.toDto(palletMngt);
    }

    /**
     * Update a palletMngt.
     *
     * @param palletMngtDTO the entity to save.
     * @return the persisted entity.
     */
    public PalletMngtDTO update(PalletMngtDTO palletMngtDTO) {
        LOG.debug("Request to update PalletMngt : {}", palletMngtDTO);
        PalletMngt palletMngt = palletMngtMapper.toEntity(palletMngtDTO);
        palletMngt = palletMngtRepository.save(palletMngt);
        return palletMngtMapper.toDto(palletMngt);
    }

    /**
     * Partially update a palletMngt.
     *
     * @param palletMngtDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PalletMngtDTO> partialUpdate(PalletMngtDTO palletMngtDTO) {
        LOG.debug("Request to partially update PalletMngt : {}", palletMngtDTO);

        return palletMngtRepository
            .findById(palletMngtDTO.getId())
            .map(existingPalletMngt -> {
                palletMngtMapper.partialUpdate(
                    existingPalletMngt,
                    palletMngtDTO
                );

                return existingPalletMngt;
            })
            .map(palletMngtRepository::save)
            .map(palletMngtMapper::toDto);
    }

    /**
     * Get all the palletMngts.
     *
     * @return the list of entities.
     */
    @Transactional(value = "partner5TransactionManager", readOnly = true)
    public List<PalletMngtDTO> findAll() {
        LOG.debug("Request to get all PalletMngts");
        return palletMngtRepository
            .findAll()
            .stream()
            .map(palletMngtMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one palletMngt by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(value = "partner5TransactionManager", readOnly = true)
    public Optional<PalletMngtDTO> findOne(Long id) {
        LOG.debug("Request to get PalletMngt : {}", id);
        return palletMngtRepository.findById(id).map(palletMngtMapper::toDto);
    }

    /**
     * Delete the palletMngt by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PalletMngt : {}", id);
        palletMngtRepository.deleteById(id);
    }

    /**
     * Get one palletMngt by serialPallet together with the boxes linked to it
     * through {@code pallet_box_mapping}.
     *
     * @param serialPallet the serial pallet of the entity.
     * @return the pallet with its list of vendor label information.
     */
    @Transactional(value = "partner5TransactionManager", readOnly = true)
    public Optional<PalletMngtDetailDTO> findOneWithBoxesBySerialPallet(
        String serialPallet
    ) {
        LOG.debug(
            "Request to get PalletMngt by serialPallet : {}",
            serialPallet
        );
        return palletMngtRepository
            .findBySerialPallet(serialPallet)
            .map(palletMngt -> {
                PalletMngtDetailDTO detailDTO = new PalletMngtDetailDTO();
                BeanUtils.copyProperties(
                    palletMngtMapper.toDto(palletMngt),
                    detailDTO
                );
                detailDTO.setVendorLabelInfoList(
                    buildVendorLabelInfoList(
                        palletBoxMappingRepository.findBySerialPalletWithVendorLabelInfo(
                            serialPallet
                        )
                    )
                );
                return detailDTO;
            });
    }

    private List<PalletVendorLabelInfoItemDTO> buildVendorLabelInfoList(
        List<PalletBoxMapping> palletBoxMappingList
    ) {
        List<PalletVendorLabelInfoItemDTO> result = new ArrayList<>(
            palletBoxMappingList.size()
        );
        for (PalletBoxMapping palletBoxMapping : palletBoxMappingList) {
            VendorLabelInfo vendorLabelInfo =
                palletBoxMapping.getVendorLabelInfo();
            if (vendorLabelInfo == null) {
                continue;
            }
            PalletVendorLabelInfoItemDTO itemDTO =
                new PalletVendorLabelInfoItemDTO();
            BeanUtils.copyProperties(
                vendorLabelInfoMapper.toDto(vendorLabelInfo),
                itemDTO,
                "deliveryNotification",
                "sapPor1",
                "palletBoxMapping"
            );
            itemDTO.setPalletBoxMappingId(palletBoxMapping.getId());
            itemDTO.setPalletBoxMapping(toBoxMappingDTO(palletBoxMapping));
            result.add(itemDTO);
        }
        return result;
    }

    private PalletBoxMappingDTO toBoxMappingDTO(
        PalletBoxMapping palletBoxMapping
    ) {
        PalletBoxMappingDTO dto = new PalletBoxMappingDTO();
        dto.setId(palletBoxMapping.getId());
        dto.setSerialPallet(palletBoxMapping.getSerialPallet());
        dto.setReelIdBox(palletBoxMapping.getReelIdBox());
        dto.setCreateAt(palletBoxMapping.getCreateAt());
        dto.setCreateBy(palletBoxMapping.getCreateBy());
        return dto;
    }
}
