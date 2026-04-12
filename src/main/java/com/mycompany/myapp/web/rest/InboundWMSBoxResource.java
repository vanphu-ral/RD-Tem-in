package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.InboundWMSBox;
import com.mycompany.myapp.domain.WarehouseNoteInfo;
import com.mycompany.myapp.domain.WarehouseNoteInfoDetail;
import com.mycompany.myapp.repository.partner3.InboundWMSBoxRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoDetailRepository;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoRepository;
import com.mycompany.myapp.service.dto.InboundWMSBoxScanDTO;
import com.mycompany.myapp.service.dto.InboundWMSBoxScanResponseDTO;
import com.mycompany.myapp.service.mapper.WarehouseStampInfoMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.InboundWMSBox}.
 */
@RestController
@RequestMapping("/api/inbound-wms-boxes")
@Transactional
public class InboundWMSBoxResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        InboundWMSBoxResource.class
    );

    private static final String ENTITY_NAME = "inboundWMSBox";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InboundWMSBoxRepository inboundWMSBoxRepository;

    private final Partner3WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository;

    private final Partner3WarehouseStampInfoRepository warehouseStampInfoRepository;

    private final WarehouseStampInfoMapper warehouseStampInfoMapper;

    public InboundWMSBoxResource(
        InboundWMSBoxRepository inboundWMSBoxRepository,
        Partner3WarehouseStampInfoDetailRepository warehouseStampInfoDetailRepository,
        Partner3WarehouseStampInfoRepository warehouseStampInfoRepository,
        WarehouseStampInfoMapper warehouseStampInfoMapper
    ) {
        this.inboundWMSBoxRepository = inboundWMSBoxRepository;
        this.warehouseStampInfoDetailRepository =
            warehouseStampInfoDetailRepository;
        this.warehouseStampInfoRepository = warehouseStampInfoRepository;
        this.warehouseStampInfoMapper = warehouseStampInfoMapper;
    }

    /**
     * {@code POST  /inbound-wms-boxes} : Create a new inboundWMSBox.
     *
     * @param inboundWMSBox the inboundWMSBox to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new inboundWMSBox, or with status {@code 400 (Bad Request)} if the inboundWMSBox has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InboundWMSBox> createInboundWMSBox(
        @Valid @RequestBody InboundWMSBox inboundWMSBox
    ) throws URISyntaxException {
        LOG.debug("REST request to save InboundWMSBox : {}", inboundWMSBox);
        if (inboundWMSBox.getId() != null) {
            throw new BadRequestAlertException(
                "A new inboundWMSBox cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        inboundWMSBox = inboundWMSBoxRepository.save(inboundWMSBox);
        return ResponseEntity.created(
            new URI("/api/inbound-wms-boxes/" + inboundWMSBox.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    inboundWMSBox.getId().toString()
                )
            )
            .body(inboundWMSBox);
    }

    /**
     * {@code POST  /inbound-wms-boxes/scan} : Scan a box for inbound WMS.
     *
     * @param scanDTO the scan request data.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the scan response, or with status {@code 400 (Bad Request)} if the serial_box is not found.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/scan")
    public ResponseEntity<InboundWMSBoxScanResponseDTO> scanInboundWMSBox(
        @RequestBody InboundWMSBoxScanDTO scanDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to scan InboundWMSBox : {}", scanDTO);

        // Find the WarehouseNoteInfoDetail by reel_id (serial_box)
        Optional<WarehouseNoteInfoDetail> detailOpt =
            warehouseStampInfoDetailRepository.findByReelId(
                scanDTO.getSerialBox()
            );
        if (detailOpt.isEmpty()) {
            throw new BadRequestAlertException(
                "Serial box not found in warehouse_note_info_detail",
                ENTITY_NAME,
                "serialboxnotfound"
            );
        }

        WarehouseNoteInfoDetail detail = detailOpt.get();
        Long warehouseNoteInfoId = detail.getmaLenhSanXuatId();
        if (warehouseNoteInfoId == null) {
            throw new BadRequestAlertException(
                "ma_lenh_san_xuat_id not found for serial box",
                ENTITY_NAME,
                "malenhnotfound"
            );
        }

        // Find the WarehouseNoteInfo
        Optional<WarehouseNoteInfo> warehouseNoteInfoOpt =
            warehouseStampInfoRepository.findById(warehouseNoteInfoId);
        if (warehouseNoteInfoOpt.isEmpty()) {
            throw new BadRequestAlertException(
                "Warehouse note info not found",
                ENTITY_NAME,
                "warehousenoteinfonotfound"
            );
        }

        WarehouseNoteInfo warehouseNoteInfo = warehouseNoteInfoOpt.get();

        // Create new InboundWMSBox
        InboundWMSBox inboundWMSBox = new InboundWMSBox();
        inboundWMSBox.setWarehouseNoteInfoId(warehouseNoteInfoId.intValue());
        inboundWMSBox.setInboundWMSSessionId(
            scanDTO.getInboundWmsSessionId().intValue()
        );
        inboundWMSBox.setSerialBox(scanDTO.getSerialBox());
        inboundWMSBox.setCreatedBy(scanDTO.getScannedBy());
        inboundWMSBox.setCreatedAt(scanDTO.getScannedAt());
        inboundWMSBox.setWmsSendStatus(false); // Default to false

        inboundWMSBox = inboundWMSBoxRepository.save(inboundWMSBox);

        // Create response DTO
        InboundWMSBoxScanResponseDTO response =
            new InboundWMSBoxScanResponseDTO();
        response.setInboundWMSBox(inboundWMSBox);
        response.setWarehouseNoteInfo(
            warehouseStampInfoMapper.toDto(warehouseNoteInfo)
        );
        response.setPartNumber(detail.getPartNumber());
        response.setLot(detail.getLot());
        response.setUserData1(detail.getUserData1());
        response.setUserData2(detail.getUserData2());
        response.setUserData3(detail.getUserData3());
        response.setUserData4(detail.getUserData4());
        response.setUserData5(detail.getUserData5());
        response.setInitialQuantity(detail.getInitialQuantity());
        response.setWmsSendStatus(detail.getWmsSendStatus());
        response.setSapCode(detail.getSapCode());

        return ResponseEntity.created(
            new URI("/api/inbound-wms-boxes/" + inboundWMSBox.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    inboundWMSBox.getId().toString()
                )
            )
            .body(response);
    }

    /**
     * {@code PUT  /inbound-wms-boxes/:id} : Updates an existing inboundWMSBox.
     *
     * @param id the id of the inboundWMSBox to save.
     * @param inboundWMSBox the inboundWMSBox to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inboundWMSBox,
     * or with status {@code 400 (Bad Request)} if the inboundWMSBox is not valid,
     * or with status {@code 500 (Internal Server Error)} if the inboundWMSBox couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InboundWMSBox> updateInboundWMSBox(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InboundWMSBox inboundWMSBox
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update InboundWMSBox : {}, {}",
            id,
            inboundWMSBox
        );
        if (inboundWMSBox.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, inboundWMSBox.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!inboundWMSBoxRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        inboundWMSBox = inboundWMSBoxRepository.save(inboundWMSBox);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    inboundWMSBox.getId().toString()
                )
            )
            .body(inboundWMSBox);
    }

    /**
     * {@code PATCH  /inbound-wms-boxes/:id} : Partial updates given fields of an existing inboundWMSBox, field will ignore if it is null
     *
     * @param id the id of the inboundWMSBox to save.
     * @param inboundWMSBox the inboundWMSBox to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inboundWMSBox,
     * or with status {@code 400 (Bad Request)} if the inboundWMSBox is not valid,
     * or with status {@code 404 (Not Found)} if the inboundWMSBox is not found,
     * or with status {@code 500 (Internal Server Error)} if the inboundWMSBox couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<InboundWMSBox> partialUpdateInboundWMSBox(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InboundWMSBox inboundWMSBox
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to partial update InboundWMSBox partially : {}, {}",
            id,
            inboundWMSBox
        );
        if (inboundWMSBox.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, inboundWMSBox.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!inboundWMSBoxRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<InboundWMSBox> result = inboundWMSBoxRepository
            .findById(inboundWMSBox.getId())
            .map(existingInboundWMSBox -> {
                if (inboundWMSBox.getWarehouseNoteInfoId() != null) {
                    existingInboundWMSBox.setWarehouseNoteInfoId(
                        inboundWMSBox.getWarehouseNoteInfoId()
                    );
                }
                if (inboundWMSBox.getSerialBox() != null) {
                    existingInboundWMSBox.setSerialBox(
                        inboundWMSBox.getSerialBox()
                    );
                }
                if (inboundWMSBox.getWmsSendStatus() != null) {
                    existingInboundWMSBox.setWmsSendStatus(
                        inboundWMSBox.getWmsSendStatus()
                    );
                }
                if (inboundWMSBox.getCreatedBy() != null) {
                    existingInboundWMSBox.setCreatedBy(
                        inboundWMSBox.getCreatedBy()
                    );
                }
                if (inboundWMSBox.getCreatedAt() != null) {
                    existingInboundWMSBox.setCreatedAt(
                        inboundWMSBox.getCreatedAt()
                    );
                }

                return existingInboundWMSBox;
            })
            .map(inboundWMSBoxRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                inboundWMSBox.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /inbound-wms-boxes} : get all the inboundWMSBoxes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of inboundWMSBoxes in body.
     */
    @GetMapping("")
    public List<InboundWMSBox> getAllInboundWMSBoxes() {
        LOG.debug("REST request to get all InboundWMSBoxes");
        return inboundWMSBoxRepository.findAll();
    }

    /**
     * {@code GET  /inbound-wms-boxes/:id} : get the "id" inboundWMSBox.
     *
     * @param id the id of the inboundWMSBox to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the inboundWMSBox, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InboundWMSBox> getInboundWMSBox(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to get InboundWMSBox : {}", id);
        Optional<InboundWMSBox> inboundWMSBox =
            inboundWMSBoxRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(inboundWMSBox);
    }

    /**
     * {@code DELETE  /inbound-wms-boxes/:id} : delete the "id" inboundWMSBox.
     *
     * @param id the id of the inboundWMSBox to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInboundWMSBox(
        @PathVariable("id") Long id
    ) {
        LOG.debug("REST request to delete InboundWMSBox : {}", id);
        inboundWMSBoxRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(
                HeaderUtil.createEntityDeletionAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    id.toString()
                )
            )
            .build();
    }
}
