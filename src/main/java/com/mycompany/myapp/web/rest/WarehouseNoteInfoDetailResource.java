package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.WarehouseNoteInfoDetail;
import com.mycompany.myapp.repository.partner3.Partner3WarehouseStampInfoDetailRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing
 * {@link com.mycompany.myapp.domain.WarehouseNoteInfoDetail}.
 */
@RestController
@RequestMapping("/api")
@Transactional("partner3TransactionManager")
public class WarehouseNoteInfoDetailResource {

    private final Logger log = LoggerFactory.getLogger(
        WarehouseNoteInfoDetailResource.class
    );

    private static final String ENTITY_NAME = "warehouseNoteInfoDetail";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final Partner3WarehouseStampInfoDetailRepository partner3WarehouseStampInfoDetailRepository;

    public WarehouseNoteInfoDetailResource(
        Partner3WarehouseStampInfoDetailRepository partner3WarehouseStampInfoDetailRepository
    ) {
        this.partner3WarehouseStampInfoDetailRepository =
            partner3WarehouseStampInfoDetailRepository;
    }

    /**
     * {@code POST  /warehouse-note-info-details} : Create or update a list of
     * warehouseNoteInfoDetail.
     *
     * @param warehouseNoteInfoDetails the list of warehouseNoteInfoDetail to create
     *                                 or update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with
     *         body the list of warehouseNoteInfoDetail.
     */
    @PostMapping("/warehouse-note-info-details")
    public ResponseEntity<
        List<WarehouseNoteInfoDetail>
    > createOrUpdateWarehouseNoteInfoDetails(
        @Valid @RequestBody List<
            WarehouseNoteInfoDetail
        > warehouseNoteInfoDetails
    ) {
        log.debug(
            "REST request to save or update WarehouseNoteInfoDetails : {}",
            warehouseNoteInfoDetails
        );
        List<WarehouseNoteInfoDetail> result =
            partner3WarehouseStampInfoDetailRepository.saveAll(
                warehouseNoteInfoDetails
            );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    "bulk"
                )
            )
            .body(result);
    }

    /**
     * {@code PUT  /warehouse-note-info-details/:id} : Updates an existing
     * warehouseNoteInfoDetail.
     *
     * @param id                      the id of the warehouseNoteInfoDetail to save.
     * @param warehouseNoteInfoDetail the warehouseNoteInfoDetail to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated warehouseNoteInfoDetail,
     *         or with status {@code 400 (Bad Request)} if the
     *         warehouseNoteInfoDetail is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         warehouseNoteInfoDetail couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/warehouse-note-info-details/{id}")
    public ResponseEntity<
        WarehouseNoteInfoDetail
    > updateWarehouseNoteInfoDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WarehouseNoteInfoDetail warehouseNoteInfoDetail
    ) throws URISyntaxException {
        log.debug(
            "REST request to update WarehouseNoteInfoDetail : {}, {}",
            id,
            warehouseNoteInfoDetail
        );
        if (warehouseNoteInfoDetail.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, warehouseNoteInfoDetail.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!partner3WarehouseStampInfoDetailRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        WarehouseNoteInfoDetail result =
            partner3WarehouseStampInfoDetailRepository.save(
                warehouseNoteInfoDetail
            );
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    warehouseNoteInfoDetail.getId().toString()
                )
            )
            .body(result);
    }

    /**
     * {@code PATCH  /warehouse-note-info-details/:id} : Partial updates given
     * fields of an existing warehouseNoteInfoDetail, field will ignore if it is
     * null
     *
     * @param id                      the id of the warehouseNoteInfoDetail to save.
     * @param warehouseNoteInfoDetail the warehouseNoteInfoDetail to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated warehouseNoteInfoDetail,
     *         or with status {@code 400 (Bad Request)} if the
     *         warehouseNoteInfoDetail is not valid,
     *         or with status {@code 404 (Not Found)} if the warehouseNoteInfoDetail
     *         is not found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         warehouseNoteInfoDetail couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/warehouse-note-info-details/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<
        WarehouseNoteInfoDetail
    > partialUpdateWarehouseNoteInfoDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WarehouseNoteInfoDetail warehouseNoteInfoDetail
    ) throws URISyntaxException {
        log.debug(
            "REST request to partial update WarehouseNoteInfoDetail partially : {}, {}",
            id,
            warehouseNoteInfoDetail
        );
        if (warehouseNoteInfoDetail.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, warehouseNoteInfoDetail.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        if (!partner3WarehouseStampInfoDetailRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        Optional<WarehouseNoteInfoDetail> result =
            partner3WarehouseStampInfoDetailRepository
                .findById(warehouseNoteInfoDetail.getId())
                .map(existingWarehouseNoteInfoDetail -> {
                    if (warehouseNoteInfoDetail.getReelId() != null) {
                        existingWarehouseNoteInfoDetail.setReelId(
                            warehouseNoteInfoDetail.getReelId()
                        );
                    }
                    if (warehouseNoteInfoDetail.getPartNumber() != null) {
                        existingWarehouseNoteInfoDetail.setPartNumber(
                            warehouseNoteInfoDetail.getPartNumber()
                        );
                    }
                    if (warehouseNoteInfoDetail.getVendor() != null) {
                        existingWarehouseNoteInfoDetail.setVendor(
                            warehouseNoteInfoDetail.getVendor()
                        );
                    }
                    if (warehouseNoteInfoDetail.getLot() != null) {
                        existingWarehouseNoteInfoDetail.setLot(
                            warehouseNoteInfoDetail.getLot()
                        );
                    }
                    if (warehouseNoteInfoDetail.getUserData1() != null) {
                        existingWarehouseNoteInfoDetail.setUserData1(
                            warehouseNoteInfoDetail.getUserData1()
                        );
                    }
                    if (warehouseNoteInfoDetail.getUserData2() != null) {
                        existingWarehouseNoteInfoDetail.setUserData2(
                            warehouseNoteInfoDetail.getUserData2()
                        );
                    }
                    if (warehouseNoteInfoDetail.getUserData3() != null) {
                        existingWarehouseNoteInfoDetail.setUserData3(
                            warehouseNoteInfoDetail.getUserData3()
                        );
                    }
                    if (warehouseNoteInfoDetail.getUserData4() != null) {
                        existingWarehouseNoteInfoDetail.setUserData4(
                            warehouseNoteInfoDetail.getUserData4()
                        );
                    }
                    if (warehouseNoteInfoDetail.getUserData5() != null) {
                        existingWarehouseNoteInfoDetail.setUserData5(
                            warehouseNoteInfoDetail.getUserData5()
                        );
                    }
                    if (warehouseNoteInfoDetail.getInitialQuantity() != null) {
                        existingWarehouseNoteInfoDetail.setInitialQuantity(
                            warehouseNoteInfoDetail.getInitialQuantity()
                        );
                    }
                    if (warehouseNoteInfoDetail.getMsdLevel() != null) {
                        existingWarehouseNoteInfoDetail.setMsdLevel(
                            warehouseNoteInfoDetail.getMsdLevel()
                        );
                    }
                    if (
                        warehouseNoteInfoDetail.getMsdInitialFloorTime() != null
                    ) {
                        existingWarehouseNoteInfoDetail.setMsdInitialFloorTime(
                            warehouseNoteInfoDetail.getMsdInitialFloorTime()
                        );
                    }
                    if (warehouseNoteInfoDetail.getMsdBagSealDate() != null) {
                        existingWarehouseNoteInfoDetail.setMsdBagSealDate(
                            warehouseNoteInfoDetail.getMsdBagSealDate()
                        );
                    }
                    if (warehouseNoteInfoDetail.getMarketUsage() != null) {
                        existingWarehouseNoteInfoDetail.setMarketUsage(
                            warehouseNoteInfoDetail.getMarketUsage()
                        );
                    }
                    if (warehouseNoteInfoDetail.getQuantityOverride() != null) {
                        existingWarehouseNoteInfoDetail.setQuantityOverride(
                            warehouseNoteInfoDetail.getQuantityOverride()
                        );
                    }
                    if (warehouseNoteInfoDetail.getShelfTime() != null) {
                        existingWarehouseNoteInfoDetail.setShelfTime(
                            warehouseNoteInfoDetail.getShelfTime()
                        );
                    }
                    if (warehouseNoteInfoDetail.getSpMaterialName() != null) {
                        existingWarehouseNoteInfoDetail.setSpMaterialName(
                            warehouseNoteInfoDetail.getSpMaterialName()
                        );
                    }
                    if (warehouseNoteInfoDetail.getWarningLimit() != null) {
                        existingWarehouseNoteInfoDetail.setWarningLimit(
                            warehouseNoteInfoDetail.getWarningLimit()
                        );
                    }
                    if (warehouseNoteInfoDetail.getMaximumLimit() != null) {
                        existingWarehouseNoteInfoDetail.setMaximumLimit(
                            warehouseNoteInfoDetail.getMaximumLimit()
                        );
                    }
                    if (warehouseNoteInfoDetail.getComments() != null) {
                        existingWarehouseNoteInfoDetail.setComments(
                            warehouseNoteInfoDetail.getComments()
                        );
                    }
                    if (warehouseNoteInfoDetail.getWarmupTime() != null) {
                        existingWarehouseNoteInfoDetail.setWarmupTime(
                            warehouseNoteInfoDetail.getWarmupTime()
                        );
                    }
                    if (warehouseNoteInfoDetail.getStorageUnit() != null) {
                        existingWarehouseNoteInfoDetail.setStorageUnit(
                            warehouseNoteInfoDetail.getStorageUnit()
                        );
                    }
                    if (warehouseNoteInfoDetail.getSubStorageUnit() != null) {
                        existingWarehouseNoteInfoDetail.setSubStorageUnit(
                            warehouseNoteInfoDetail.getSubStorageUnit()
                        );
                    }
                    if (warehouseNoteInfoDetail.getLocationOverride() != null) {
                        existingWarehouseNoteInfoDetail.setLocationOverride(
                            warehouseNoteInfoDetail.getLocationOverride()
                        );
                    }
                    if (warehouseNoteInfoDetail.getExpirationDate() != null) {
                        existingWarehouseNoteInfoDetail.setExpirationDate(
                            warehouseNoteInfoDetail.getExpirationDate()
                        );
                    }
                    if (
                        warehouseNoteInfoDetail.getManufacturingDate() != null
                    ) {
                        existingWarehouseNoteInfoDetail.setManufacturingDate(
                            warehouseNoteInfoDetail.getManufacturingDate()
                        );
                    }
                    if (warehouseNoteInfoDetail.getPartClass() != null) {
                        existingWarehouseNoteInfoDetail.setPartClass(
                            warehouseNoteInfoDetail.getPartClass()
                        );
                    }
                    if (warehouseNoteInfoDetail.getSapCode() != null) {
                        existingWarehouseNoteInfoDetail.setSapCode(
                            warehouseNoteInfoDetail.getSapCode()
                        );
                    }
                    if (warehouseNoteInfoDetail.getmaLenhSanXuatId() != null) {
                        existingWarehouseNoteInfoDetail.setmaLenhSanXuatId(
                            warehouseNoteInfoDetail.getmaLenhSanXuatId()
                        );
                    }
                    if (warehouseNoteInfoDetail.getLenhSanXuatId() != null) {
                        existingWarehouseNoteInfoDetail.setLenhSanXuatId(
                            warehouseNoteInfoDetail.getLenhSanXuatId()
                        );
                    }
                    if (warehouseNoteInfoDetail.getTrangThai() != null) {
                        existingWarehouseNoteInfoDetail.setTrangThai(
                            warehouseNoteInfoDetail.getTrangThai()
                        );
                    }
                    if (warehouseNoteInfoDetail.getChecked() != null) {
                        existingWarehouseNoteInfoDetail.setChecked(
                            warehouseNoteInfoDetail.getChecked()
                        );
                    }
                    if (warehouseNoteInfoDetail.getWmsSendStatus() != null) {
                        existingWarehouseNoteInfoDetail.setWmsSendStatus(
                            warehouseNoteInfoDetail.getWmsSendStatus()
                        );
                    }
                    if (warehouseNoteInfoDetail.getListSerialItems() != null) {
                        existingWarehouseNoteInfoDetail.setListSerialItems(
                            warehouseNoteInfoDetail.getListSerialItems()
                        );
                    }
                    if (warehouseNoteInfoDetail.getQrCode() != null) {
                        existingWarehouseNoteInfoDetail.setQrCode(
                            warehouseNoteInfoDetail.getQrCode()
                        );
                    }
                    if (warehouseNoteInfoDetail.getTpNk() != null) {
                        existingWarehouseNoteInfoDetail.setTpNk(
                            warehouseNoteInfoDetail.getTpNk()
                        );
                    }
                    if (warehouseNoteInfoDetail.getRank() != null) {
                        existingWarehouseNoteInfoDetail.setRank(
                            warehouseNoteInfoDetail.getRank()
                        );
                    }
                    if (warehouseNoteInfoDetail.getNote2() != null) {
                        existingWarehouseNoteInfoDetail.setNote2(
                            warehouseNoteInfoDetail.getNote2()
                        );
                    }
                    if (warehouseNoteInfoDetail.getQmsStoredCheckId() != null) {
                        existingWarehouseNoteInfoDetail.setQmsStoredCheckId(
                            warehouseNoteInfoDetail.getQmsStoredCheckId()
                        );
                    }
                    if (warehouseNoteInfoDetail.getTimeQmsApprove() != null) {
                        existingWarehouseNoteInfoDetail.setTimeQmsApprove(
                            warehouseNoteInfoDetail.getTimeQmsApprove()
                        );
                    }
                    if (warehouseNoteInfoDetail.getQmsResultCheck() != null) {
                        existingWarehouseNoteInfoDetail.setQmsResultCheck(
                            warehouseNoteInfoDetail.getQmsResultCheck()
                        );
                    }
                    if (warehouseNoteInfoDetail.getQmsScanMode() != null) {
                        existingWarehouseNoteInfoDetail.setQmsScanMode(
                            warehouseNoteInfoDetail.getQmsScanMode()
                        );
                    }
                    if (warehouseNoteInfoDetail.getUpdatedAt() != null) {
                        existingWarehouseNoteInfoDetail.setUpdatedAt(
                            warehouseNoteInfoDetail.getUpdatedAt()
                        );
                    }
                    if (warehouseNoteInfoDetail.getUpdatedBy() != null) {
                        existingWarehouseNoteInfoDetail.setUpdatedBy(
                            warehouseNoteInfoDetail.getUpdatedBy()
                        );
                    }

                    return existingWarehouseNoteInfoDetail;
                })
                .map(partner3WarehouseStampInfoDetailRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                false,
                ENTITY_NAME,
                warehouseNoteInfoDetail.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /warehouse-note-info-details} : get all the
     * warehouseNoteInfoDetails.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of warehouseNoteInfoDetails in body.
     */
    @GetMapping("/warehouse-note-info-details")
    public ResponseEntity<
        List<WarehouseNoteInfoDetail>
    > getAllWarehouseNoteInfoDetails(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of WarehouseNoteInfoDetails");
        Page<WarehouseNoteInfoDetail> page =
            partner3WarehouseStampInfoDetailRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /warehouse-note-info-details/:id} : get the "id"
     * warehouseNoteInfoDetail.
     *
     * @param id the id of the warehouseNoteInfoDetail to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the warehouseNoteInfoDetail, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/warehouse-note-info-details/{id}")
    public ResponseEntity<WarehouseNoteInfoDetail> getWarehouseNoteInfoDetail(
        @PathVariable Long id
    ) {
        log.debug("REST request to get WarehouseNoteInfoDetail : {}", id);
        Optional<WarehouseNoteInfoDetail> warehouseNoteInfoDetail =
            partner3WarehouseStampInfoDetailRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(warehouseNoteInfoDetail);
    }

    /**
     * {@code DELETE  /warehouse-note-info-details/:id} : delete the "id"
     * warehouseNoteInfoDetail.
     *
     * @param id the id of the warehouseNoteInfoDetail to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/warehouse-note-info-details/{id}")
    public ResponseEntity<Void> deleteWarehouseNoteInfoDetail(
        @PathVariable Long id
    ) {
        log.debug("REST request to delete WarehouseNoteInfoDetail : {}", id);
        partner3WarehouseStampInfoDetailRepository.deleteById(id);
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
