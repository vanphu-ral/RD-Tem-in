package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.PalletInforDetailService;
import com.mycompany.myapp.service.SerialBoxPalletMappingService;
import com.mycompany.myapp.service.WarehouseStampInfoDetailService;
import com.mycompany.myapp.service.WarehouseStampInfoService;
import com.mycompany.myapp.service.dto.PalletInforDetailDTO;
import com.mycompany.myapp.service.dto.SerialBoxPalletMappingDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDTO;
import com.mycompany.myapp.service.dto.WarehouseStampInfoDetailDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.util.*;
import liquibase.pro.packaged.is;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * REST controller for managing reconciliation QMS.
 */
@RestController
@RequestMapping("/api/import-requirements")
public class ReconciliationQmsResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        ReconciliationQmsResource.class
    );

    private static final String ENTITY_NAME = "reconciliationQms";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WarehouseStampInfoDetailService warehouseStampInfoDetailService;
    private final SerialBoxPalletMappingService serialBoxPalletMappingService;
    private final PalletInforDetailService palletInforDetailService;
    private final WarehouseStampInfoService warehouseStampInfoService;
    private final RestTemplate restTemplate;

    public ReconciliationQmsResource(
        WarehouseStampInfoDetailService warehouseStampInfoDetailService,
        SerialBoxPalletMappingService serialBoxPalletMappingService,
        PalletInforDetailService palletInforDetailService,
        WarehouseStampInfoService warehouseStampInfoService,
        RestTemplate restTemplate
    ) {
        this.warehouseStampInfoDetailService = warehouseStampInfoDetailService;
        this.serialBoxPalletMappingService = serialBoxPalletMappingService;
        this.palletInforDetailService = palletInforDetailService;
        this.warehouseStampInfoService = warehouseStampInfoService;
        this.restTemplate = restTemplate;
    }

    /**
     * {@code POST  /import-requirements/reconciliation-qms} : Process
     * reconciliation QMS.
     *
     * @param request the reconciliation QMS request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the response.
     */
    @PostMapping("/reconciliation-qms")
    public ResponseEntity<Map<String, Object>> processReconciliationQms(
        @RequestBody Map<String, Object> request
    ) {
        LOG.debug("REST request to process reconciliation QMS : {}", request);

        try {
            // Step 1: Update warehouse_note_info_detail
            List<Map<String, Object>> modePalletList = (List<
                Map<String, Object>
            >) request.get("mode_pallet");
            List<Map<String, Object>> modeBoxList = (List<
                Map<String, Object>
            >) request.get("mode_box");

            // Update mode_pallet
            if (modePalletList != null) {
                for (Map<String, Object> modePallet : modePalletList) {
                    List<Map<String, Object>> listBox = (List<
                        Map<String, Object>
                    >) modePallet.get("list_box");
                    if (listBox != null) {
                        for (Map<String, Object> box : listBox) {
                            Integer id = ((Number) box.get("id")).intValue();
                            updateWarehouseStampInfoDetail(id.longValue(), box);
                        }
                    }
                }
            }

            // Update mode_box
            if (modeBoxList != null) {
                for (Map<String, Object> modeBox : modeBoxList) {
                    Integer id = ((Number) modeBox.get("id")).intValue();
                    updateWarehouseStampInfoDetail(id.longValue(), modeBox);
                }
            }

            // Step 2: Query data to construct the new body
            Integer maLenhSanXuatId = null;
            if (modePalletList != null && !modePalletList.isEmpty()) {
                maLenhSanXuatId = (Integer) modePalletList
                    .get(0)
                    .get("ma_lenh_san_xuat_id");
            }

            if (maLenhSanXuatId == null) {
                throw new BadRequestAlertException(
                    "ma_lenh_san_xuat_id is required",
                    ENTITY_NAME,
                    "missing"
                );
            }

            // Query warehouse_note_info
            Optional<WarehouseStampInfoDTO> warehouseNoteInfoOpt =
                warehouseStampInfoService.findOne(maLenhSanXuatId.longValue());
            if (!warehouseNoteInfoOpt.isPresent()) {
                throw new BadRequestAlertException(
                    "WarehouseNoteInfo not found",
                    ENTITY_NAME,
                    "notfound"
                );
            }

            WarehouseStampInfoDTO warehouseNoteInfo =
                warehouseNoteInfoOpt.get();

            // Query warehouse_note_info_detail
            List<WarehouseStampInfoDetailDTO> warehouseNoteInfoDetails =
                warehouseStampInfoDetailService.findByMaLenhSanXuatId(
                    maLenhSanXuatId.longValue()
                );

            // Query serial_box_pallet_mapping
            List<SerialBoxPalletMappingDTO> serialBoxPalletMappings =
                serialBoxPalletMappingService.findByMaLenhSanXuatId(
                    maLenhSanXuatId.longValue()
                );

            // Query pallet_infor_detail
            List<PalletInforDetailDTO> palletInforDetails = new ArrayList<>();
            for (SerialBoxPalletMappingDTO mapping : serialBoxPalletMappings) {
                Optional<PalletInforDetailDTO> palletOpt =
                    palletInforDetailService.findOne(mapping.getSerialPallet());
                palletOpt.ifPresent(palletInforDetails::add);
            }

            // Step 3: Construct the new body
            Map<String, Object> generalInfo = new HashMap<>();
            generalInfo.put("client_id", "Lowes-YK");
            generalInfo.put("inventory_code", warehouseNoteInfo.getSapCode());
            // String inventoryName = warehouseNoteInfo.getSapName();
            // generalInfo.put(
            // "inventory_name",
            // inventoryName != null && inventoryName.length() > 15
            // ? inventoryName.substring(0, 15)
            // : inventoryName
            // );
            generalInfo.put("inventory_name", warehouseNoteInfo.getSapName());
            generalInfo.put("wo_code", warehouseNoteInfo.getWorkOrderCode());
            generalInfo.put("lot_number", warehouseNoteInfo.getLotNumber());
            generalInfo.put("note", warehouseNoteInfo.getComment());
            generalInfo.put(
                "production_date",
                warehouseNoteInfo.getEntryTime() != null
                    ? warehouseNoteInfo.getEntryTime().toString()
                    : ""
            );
            generalInfo.put("created_by", warehouseNoteInfo.getCreateBy());
            generalInfo.put("branch", warehouseNoteInfo.getBranch());
            generalInfo.put(
                "production_team",
                warehouseNoteInfo.getGroupName().trim()
            );
            // Calculate number_of_pallet, number_of_box, and quantity based on the request
            // body
            int numberOfPallet = 0;
            int numberOfBox = 0;
            int totalQuantity = 0;
            Set<String> boxCodesInPallets = new HashSet<>();

            // Count pallets and collect box codes from pallets
            if (modePalletList != null) {
                numberOfPallet = modePalletList.size();
                for (Map<String, Object> modePallet : modePalletList) {
                    List<Map<String, Object>> listBox = (List<
                        Map<String, Object>
                    >) modePallet.get("list_box");
                    if (listBox != null) {
                        for (Map<String, Object> box : listBox) {
                            String boxCode = (String) box.get("reel_id");
                            if (boxCode != null) {
                                boxCodesInPallets.add(boxCode);
                                numberOfBox++;
                                // Sum the initial_quantity from the box
                                if (box.containsKey("initial_quantity")) {
                                    totalQuantity += ((Number) box.get(
                                            "initial_quantity"
                                        )).intValue();
                                }
                            }
                        }
                    }
                }
            }

            // Sum the initial_quantity from mode_box items that are not in pallets
            if (modeBoxList != null) {
                for (Map<String, Object> modeBox : modeBoxList) {
                    String boxCode = (String) modeBox.get("reel_id");
                    if (
                        boxCode != null && !boxCodesInPallets.contains(boxCode)
                    ) {
                        // Sum the initial_quantity from the box
                        if (modeBox.containsKey("initial_quantity")) {
                            totalQuantity += ((Number) modeBox.get(
                                    "initial_quantity"
                                )).intValue();
                        }
                    }
                }
            }

            generalInfo.put("number_of_pallet", numberOfPallet);
            generalInfo.put("number_of_box", numberOfBox);
            generalInfo.put("quantity", totalQuantity);
            Object warehouseValue = 1;
            String storageCode = warehouseNoteInfo.getStorageCode();
            if (storageCode == null || storageCode.isEmpty()) {
                warehouseValue = 0;
                // } else if (storageCode.equals("RD-01")) {
                // warehouseValue = 1;
                // }
            }
            generalInfo.put("destination_warehouse", warehouseValue);
            generalInfo.put(
                "pallet_note_creation_id",
                warehouseNoteInfo.getId()
            );

            List<Map<String, Object>> listPallet = new ArrayList<>();

            // Add pallets from mode_pallet
            if (modePalletList != null) {
                for (Map<String, Object> modePallet : modePalletList) {
                    String serialPallet = (String) modePallet.get(
                        "serial_pallet"
                    );
                    Map<String, Object> pallet = new HashMap<>();
                    pallet.put("serial_pallet", serialPallet);
                    pallet.put("quantity_per_box", 1);

                    // Calculate num_box_per_pallet and total_quantity from list_box
                    int numBoxPerPallet = 0;
                    int totalQuantityPerPallet = 0;

                    List<Map<String, Object>> listBox = new ArrayList<>();
                    List<Map<String, Object>> listBoxFromMode = (List<
                        Map<String, Object>
                    >) modePallet.get("list_box");
                    if (listBoxFromMode != null) {
                        for (Map<String, Object> box : listBoxFromMode) {
                            Map<String, Object> boxInfo = new HashMap<>();
                            boxInfo.put("box_code", box.get("reel_id"));
                            boxInfo.put("quantity", 1);
                            boxInfo.put("note", "");
                            boxInfo.put("list_serial_items", "");
                            listBox.add(boxInfo);
                            numBoxPerPallet++;
                            totalQuantityPerPallet++;
                        }
                    }

                    pallet.put("num_box_per_pallet", numBoxPerPallet);
                    pallet.put("total_quantity", totalQuantityPerPallet);
                    pallet.put("po_number", "");
                    pallet.put("customer_name", "");
                    pallet.put("production_decision_number", "");
                    pallet.put("item_no_sku", "");
                    pallet.put("date_code", "");
                    pallet.put("note", "");
                    pallet.put(
                        "production_date",
                        warehouseNoteInfo.getEntryTime() != null
                            ? warehouseNoteInfo.getEntryTime().toString()
                            : ""
                    );
                    pallet.put("list_box", listBox);
                    listPallet.add(pallet);
                }
            }

            // Add pallets from mode_box - group into single list_pallet with empty
            // serial_pallet and quantity_per_box
            if (modeBoxList != null && !modeBoxList.isEmpty()) {
                Map<String, Object> modeBoxPallet = new HashMap<>();
                modeBoxPallet.put("serial_pallet", "");
                modeBoxPallet.put("quantity_per_box", 0);

                // Calculate num_box_per_pallet and total_quantity from mode_box items
                int numBoxPerPallet = modeBoxList.size();
                int totalQuantityModeBox = modeBoxList.size();

                modeBoxPallet.put("num_box_per_pallet", numBoxPerPallet);
                modeBoxPallet.put("total_quantity", totalQuantityModeBox);
                modeBoxPallet.put("po_number", "");
                modeBoxPallet.put("customer_name", "");
                modeBoxPallet.put("production_decision_number", "");
                modeBoxPallet.put("item_no_sku", "");
                modeBoxPallet.put("date_code", "");
                modeBoxPallet.put("note", "");
                modeBoxPallet.put(
                    "production_date",
                    warehouseNoteInfo.getEntryTime() != null
                        ? warehouseNoteInfo.getEntryTime().toString()
                        : ""
                );

                // Add box information from mode_box
                List<Map<String, Object>> listBox = new ArrayList<>();
                for (Map<String, Object> modeBox : modeBoxList) {
                    Map<String, Object> boxInfo = new HashMap<>();
                    boxInfo.put("box_code", modeBox.get("reel_id"));
                    boxInfo.put("quantity", 1);
                    boxInfo.put("note", "");
                    boxInfo.put("list_serial_items", "");
                    listBox.add(boxInfo);
                }
                modeBoxPallet.put("list_box", listBox);
                listPallet.add(modeBoxPallet);
            }

            generalInfo.put("list_pallet", listPallet);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("general_info", generalInfo);

            // Step 4: Send to external API
            String url = "http://localhost:9030/api/import-requirements/wms";
            // hiển thị requestBody trên log
            LOG.debug("Request body gửi WMS", requestBody);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                url,
                requestBody,
                Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                return ResponseEntity.ok(result);
            } else {
                throw new BadRequestAlertException(
                    "Failed to send data to external API",
                    ENTITY_NAME,
                    "apierror"
                );
            }
        } catch (Exception e) {
            LOG.error(
                "Error processing reconciliation QMS: {}",
                e.getMessage()
            );
            throw new BadRequestAlertException(
                "Error processing reconciliation QMS: " + e.getMessage(),
                ENTITY_NAME,
                "error"
            );
        }
    }

    private void updateWarehouseStampInfoDetail(
        Long id,
        Map<String, Object> data
    ) {
        Optional<WarehouseStampInfoDetailDTO> detailOpt =
            warehouseStampInfoDetailService.findOne(id);
        if (detailOpt.isPresent()) {
            WarehouseStampInfoDetailDTO detail = detailOpt.get();
            // Update fields from the request
            if (data.containsKey("qms_stored_check_id")) {
                detail.setQmsStoredCheckId(
                    (Integer) data.get("qms_stored_check_id")
                );
            }
            if (data.containsKey("time_qms_approve")) {
                detail.setTimeQmsApprove((String) data.get("time_qms_approve"));
            }
            if (data.containsKey("qms_result_check")) {
                detail.setQmsResultCheck(
                    (Integer) data.get("qms_result_check")
                );
            }
            if (data.containsKey("qms_scan_mode")) {
                detail.setQmsScanMode((Integer) data.get("qms_scan_mode"));
            }
            if (data.containsKey("updated_by")) {
                detail.setUpdatedBy((String) data.get("updated_by"));
            }
            warehouseStampInfoDetailService.update(detail);
        }
    }
}
