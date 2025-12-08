package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.PalletInforDetail;
import com.mycompany.myapp.domain.SerialBoxPalletMapping;
import com.mycompany.myapp.domain.WarehouseNoteInfoDetail;
import com.mycompany.myapp.repository.partner3.Partner3PalletInforDetailRepository;
import com.mycompany.myapp.repository.partner3.Partner3SerialBoxPalletMappingRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoDetailRepository;
import com.mycompany.myapp.service.dto.PalletInforDetailDTO;
import com.mycompany.myapp.service.dto.ScanResponseDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDetailDTO;
import com.mycompany.myapp.service.mapper.PalletInforDetailMapper;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoDetailMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for scanning pallet and warehouse information.
 */
@Service
@Transactional("partner3TransactionManager")
public class ScanService {

    private static final Logger LOG = LoggerFactory.getLogger(
        ScanService.class
    );

    private final Partner3PalletInforDetailRepository palletRepository;
    private final Partner3WarehouseStampInfoDetailRepository warehouseRepository;
    private final Partner3SerialBoxPalletMappingRepository mappingRepository;
    private final PalletInforDetailMapper palletMapper;
    private final WarehouseStampInfoDetailMapper warehouseMapper;

    public ScanService(
        Partner3PalletInforDetailRepository palletRepository,
        Partner3WarehouseStampInfoDetailRepository warehouseRepository,
        Partner3SerialBoxPalletMappingRepository mappingRepository,
        PalletInforDetailMapper palletMapper,
        WarehouseStampInfoDetailMapper warehouseMapper
    ) {
        this.palletRepository = palletRepository;
        this.warehouseRepository = warehouseRepository;
        this.mappingRepository = mappingRepository;
        this.palletMapper = palletMapper;
        this.warehouseMapper = warehouseMapper;
    }

    /**
     * Scan by serial_pallet to get pallet information and all boxes inside it.
     *
     * @param serialPallet the serial pallet to scan
     * @return the scan response containing pallet and warehouse details
     */
    @Transactional(value = "partner3TransactionManager", readOnly = true)
    public Optional<ScanResponseDTO> scanBySerialPallet(String serialPallet) {
        LOG.debug("Scanning by serial_pallet: {}", serialPallet);

        // Find pallet information
        Optional<PalletInforDetail> palletOpt =
            palletRepository.findBySerialPallet(serialPallet);
        if (palletOpt.isEmpty()) {
            LOG.debug("No pallet found with serial_pallet: {}", serialPallet);
            return Optional.empty();
        }

        PalletInforDetailDTO palletDTO = palletMapper.toDto(palletOpt.get());

        // Find all boxes (serial_box) in this pallet
        List<SerialBoxPalletMapping> mappings =
            mappingRepository.findBySerialPallet(serialPallet);
        LOG.debug(
            "Found {} box mappings for pallet: {}",
            mappings.size(),
            serialPallet
        );

        // Get warehouse details for each box
        List<WarehouseStampInfoDetailDTO> warehouseDTOs = mappings
            .stream()
            .map(mapping ->
                warehouseRepository.findByReelId(mapping.getSerialBox())
            )
            .filter(Optional::isPresent)
            .map(opt -> warehouseMapper.toDto(opt.get()))
            .collect(Collectors.toList());

        LOG.debug(
            "Found {} warehouse details for pallet: {}",
            warehouseDTOs.size(),
            serialPallet
        );

        return Optional.of(
            new ScanResponseDTO(
                Collections.singletonList(palletDTO),
                warehouseDTOs
            )
        );
    }

    /**
     * Scan by reelId to get warehouse information and the pallet it belongs to.
     *
     * @param reelId the reel ID to scan
     * @return the scan response containing warehouse and pallet details
     */
    @Transactional(value = "partner3TransactionManager", readOnly = true)
    public Optional<ScanResponseDTO> scanByReelId(String reelId) {
        LOG.debug("Scanning by reelId: {}", reelId);

        // Find warehouse information
        Optional<WarehouseNoteInfoDetail> warehouseOpt =
            warehouseRepository.findByReelId(reelId);
        if (warehouseOpt.isEmpty()) {
            LOG.debug("No warehouse detail found with reelId: {}", reelId);
            return Optional.empty();
        }

        WarehouseStampInfoDetailDTO warehouseDTO = warehouseMapper.toDto(
            warehouseOpt.get()
        );

        // Find the pallet this box belongs to
        Optional<SerialBoxPalletMapping> mappingOpt =
            mappingRepository.findBySerialBox(reelId);
        if (mappingOpt.isEmpty()) {
            LOG.debug(
                "No pallet mapping found for reelId: {}, returning warehouse detail only",
                reelId
            );
            return Optional.of(
                new ScanResponseDTO(
                    Collections.emptyList(),
                    Collections.singletonList(warehouseDTO)
                )
            );
        }

        // Get pallet information
        Optional<PalletInforDetail> palletOpt =
            palletRepository.findBySerialPallet(
                mappingOpt.get().getSerialPallet()
            );
        List<PalletInforDetailDTO> palletDTOs = palletOpt.isPresent()
            ? Collections.singletonList(palletMapper.toDto(palletOpt.get()))
            : Collections.emptyList();

        LOG.debug(
            "Found pallet: {} for reelId: {}",
            palletOpt.map(PalletInforDetail::getSerialPallet).orElse("none"),
            reelId
        );

        return Optional.of(
            new ScanResponseDTO(
                palletDTOs,
                Collections.singletonList(warehouseDTO)
            )
        );
    }
}
