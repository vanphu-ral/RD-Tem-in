package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.partner3.Partner3SerialBoxPalletMappingRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoDetailRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoRepository;
import com.mycompany.myapp.service.SerialBoxPalletMappingService;
import com.mycompany.myapp.service.dto.CheckRequestDTO;
import com.mycompany.myapp.service.dto.CheckResponseDTO;
import com.mycompany.myapp.service.dto.SerialBoxPalletMappingInsertDTO;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing check requests.
 */
@RestController
@RequestMapping("/api/check")
public class CheckResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        CheckResource.class
    );

    private final Partner3WarehouseStampInfoRepository warehouseNoteInfoRepository;
    private final Partner3WarehouseStampInfoDetailRepository warehouseNoteInfoDetailRepository;
    private final Partner3SerialBoxPalletMappingRepository serialBoxPalletMappingRepository;
    private final SerialBoxPalletMappingService serialBoxPalletMappingService;

    public CheckResource(
        Partner3WarehouseStampInfoRepository warehouseNoteInfoRepository,
        Partner3WarehouseStampInfoDetailRepository warehouseNoteInfoDetailRepository,
        Partner3SerialBoxPalletMappingRepository serialBoxPalletMappingRepository,
        SerialBoxPalletMappingService serialBoxPalletMappingService
    ) {
        this.warehouseNoteInfoRepository = warehouseNoteInfoRepository;
        this.warehouseNoteInfoDetailRepository =
            warehouseNoteInfoDetailRepository;
        this.serialBoxPalletMappingRepository =
            serialBoxPalletMappingRepository;
        this.serialBoxPalletMappingService = serialBoxPalletMappingService;
    }

    /**
     * {@code POST /check} : Check the validity of the work order, pallet, and box.
     *
     * @param checkRequestDTO the check request DTO.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the check response DTO.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CheckResponseDTO> check(
        @RequestBody CheckRequestDTO checkRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to check : {}", checkRequestDTO);

        String workOrderCode = checkRequestDTO.getWorkOrderCode();
        String palletCode = checkRequestDTO.getPalletCode();
        String boxCode = checkRequestDTO.getBoxCode();
        String updatedBy = checkRequestDTO.getUpdatedBy();

        // Case 1: Check if pallet_code is in work_order_code
        boolean isPalletInWorkOrder =
            warehouseNoteInfoDetailRepository.existsByPalletCodeAndWorkOrderCode(
                palletCode,
                workOrderCode
            );
        if (!isPalletInWorkOrder) {
            // Save to database with empty work_order_code
            SerialBoxPalletMappingInsertDTO insertDTO =
                new SerialBoxPalletMappingInsertDTO();
            insertDTO.setSerialBox(boxCode);
            insertDTO.setSerialPallet(palletCode);
            insertDTO.setStatus(0);
            insertDTO.setUpdatedBy(updatedBy);
            serialBoxPalletMappingService.insertWithMaLenhSanXuat(
                null,
                insertDTO
            );

            CheckResponseDTO response = new CheckResponseDTO(
                0,
                "Pallet không nằm trong lệnh sản xuất."
            );
            return ResponseEntity.ok(response);
        }

        // Case 2: Check if box_code is in work_order_code
        boolean isBoxInWorkOrder =
            warehouseNoteInfoDetailRepository.existsByBoxCodeAndWorkOrderCode(
                boxCode,
                workOrderCode
            );
        if (!isBoxInWorkOrder) {
            // Save to database
            SerialBoxPalletMappingInsertDTO insertDTO =
                new SerialBoxPalletMappingInsertDTO();
            insertDTO.setSerialBox(boxCode);
            insertDTO.setSerialPallet(palletCode);
            insertDTO.setStatus(0);
            insertDTO.setUpdatedBy(updatedBy);
            serialBoxPalletMappingService.insertWithMaLenhSanXuat(
                null,
                insertDTO
            );

            CheckResponseDTO response = new CheckResponseDTO(
                0,
                "Box không nằm trong lệnh sản xuất."
            );
            return ResponseEntity.ok(response);
        }

        // Case 3: Check if box_code is already declared on another pallet
        boolean isBoxDeclaredOnAnotherPallet =
            serialBoxPalletMappingRepository.existsBySerialBoxAndStatus(
                boxCode,
                1
            );
        if (isBoxDeclaredOnAnotherPallet) {
            // Save to database
            SerialBoxPalletMappingInsertDTO insertDTO =
                new SerialBoxPalletMappingInsertDTO();
            insertDTO.setSerialBox(boxCode);
            insertDTO.setSerialPallet(palletCode);
            insertDTO.setStatus(0);
            insertDTO.setUpdatedBy(updatedBy);
            serialBoxPalletMappingService.insertWithMaLenhSanXuat(
                null,
                insertDTO
            );

            CheckResponseDTO response = new CheckResponseDTO(
                0,
                "Box đã được khai báo trên Pallet khác."
            );
            return ResponseEntity.ok(response);
        }

        // Case 4: All checks passed
        SerialBoxPalletMappingInsertDTO insertDTO =
            new SerialBoxPalletMappingInsertDTO();
        insertDTO.setSerialBox(boxCode);
        insertDTO.setSerialPallet(palletCode);
        insertDTO.setStatus(1);
        insertDTO.setUpdatedBy(updatedBy);
        serialBoxPalletMappingService.insertWithMaLenhSanXuat(null, insertDTO);

        CheckResponseDTO response = new CheckResponseDTO(1, "OK");
        return ResponseEntity.ok(response);
    }
}
