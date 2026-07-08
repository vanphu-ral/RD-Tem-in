package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.InventoriesResponse;
import com.mycompany.myapp.domain.WarehouseAreaInventoryItemDto;
import com.mycompany.myapp.domain.WarehouseAreaInventoryItemsResponse;
import com.mycompany.myapp.domain.WarehouseAreaLocationDto;
import com.mycompany.myapp.domain.WarehouseAreaLocationsResponse;
import com.mycompany.myapp.domain.WarehouseAreaSummaryRowDto;
import com.mycompany.myapp.domain.WarehouseLocationMaterialItemDto;
import com.mycompany.myapp.domain.WarehouseLocationMaterialsResponse;
import com.mycompany.myapp.domain.WarehouseOverviewAreaDto;
import com.mycompany.myapp.domain.WarehouseOverviewMaterialTypeDto;
import com.mycompany.myapp.domain.WarehouseSummaryResponse;
import com.mycompany.myapp.domain.WarehouseSummaryStatsDto;
import com.mycompany.myapp.service.dto.InventoryDTO;
import com.mycompany.myapp.service.dto.InventoryRequestDTO;
import com.mycompany.myapp.service.dto.WarehouseSummaryRequestDTO;
import com.mycompany.panacimmc.domain.AreaAreaResponse;
import com.mycompany.panacimmc.domain.Inventory;
import com.mycompany.panacimmc.domain.InventoryResponse;
import com.mycompany.panacimmc.domain.WarehouseAreaInventoryItemRawResponse;
import com.mycompany.panacimmc.domain.WarehouseAreaItemSummaryResponse;
import com.mycompany.panacimmc.domain.WarehouseAreaLocationRawResponse;
import com.mycompany.panacimmc.domain.WarehouseAreaSummaryResponse;
import com.mycompany.panacimmc.domain.WarehouseLocationInventoryRawResponse;
import com.mycompany.panacimmc.domain.WarehouseSummaryStatsCombined;
import com.mycompany.panacimmc.repository.InventoryRepository;
import com.mycompany.panacimmc.repository.LocationRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private static final int NAME_ITEM_CODES_BATCH_SIZE = 400;
    private static final int NAME_SEARCH_ROW_LIMIT = 5000;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    WarehouseSummarySapCacheService warehouseSummarySapCacheService;

    @Autowired
    WarehouseSummaryDataCacheService warehouseSummaryDataCacheService;

    @Autowired
    LocationRepository locationRepository;

    public List<Inventory> getAll() {
        return this.inventoryRepository.findAll();
    }

    public List<InventoryResponse> serachNew(int offset, int limit) {
        return this.inventoryRepository.getDataNew(offset, limit);
    }

    public InventoriesResponse getDataGroupByPartNumber(
        InventoryRequestDTO request
    ) {
        InventoriesResponse inventoriesResponse = new InventoriesResponse();
        inventoriesResponse.setTotalItems(
            this.inventoryRepository.getTotalDataGroupByPartNumber(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%"
            )
        );
        inventoriesResponse.setInventories(
            this.inventoryRepository.getDataGroupByPartNumber(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                (request.getPageNumber() - 1) * request.getItemPerPage(),
                request.getItemPerPage()
            )
        );
        return inventoriesResponse;
    }

    public InventoriesResponse getDataGroupByLotNumber(
        InventoryRequestDTO request
    ) {
        InventoriesResponse inventoriesResponse = new InventoriesResponse();
        inventoriesResponse.setTotalItems(
            this.inventoryRepository.getTotalDataGroupByLotNumber(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLotNumber()).orElse("") +
                "%"
            )
        );
        inventoriesResponse.setInventories(
            this.inventoryRepository.getDataGroupByLotNumber(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLotNumber()).orElse("") +
                "%",
                (request.getPageNumber() - 1) * request.getItemPerPage(),
                request.getItemPerPage()
            )
        );
        return inventoriesResponse;
    }

    public InventoriesResponse getDataGroupByUserData4(
        InventoryRequestDTO request
    ) {
        InventoriesResponse inventoriesResponse = new InventoriesResponse();
        inventoriesResponse.setTotalItems(
            this.inventoryRepository.getTotalDataGroupByUserData4(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getUserData4()).orElse("") +
                "%"
            )
        );
        inventoriesResponse.setInventories(
            this.inventoryRepository.getDataGroupByUserData4(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getUserData4()).orElse("") +
                "%",
                (request.getPageNumber() - 1) * request.getItemPerPage(),
                request.getItemPerPage()
            )
        );
        return inventoriesResponse;
    }

    public InventoriesResponse getDataGroupByUserData5(
        InventoryRequestDTO request
    ) {
        InventoriesResponse inventoriesResponse = new InventoriesResponse();
        inventoriesResponse.setTotalItems(
            this.inventoryRepository.getTotalDataGroupByUserData5(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getUserData5()).orElse("") +
                "%"
            )
        );
        inventoriesResponse.setInventories(
            this.inventoryRepository.getDataGroupByUserData5(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getUserData5()).orElse("") +
                "%",
                (request.getPageNumber() - 1) * request.getItemPerPage(),
                request.getItemPerPage()
            )
        );
        return inventoriesResponse;
    }

    public InventoriesResponse getDataGroupByLocationName(
        InventoryRequestDTO request
    ) {
        InventoriesResponse inventoriesResponse = new InventoriesResponse();
        inventoriesResponse.setTotalItems(
            this.inventoryRepository.getTotalDataGroupByLocationName(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLocationName()).orElse("") +
                "%"
            )
        );
        inventoriesResponse.setInventories(
            this.inventoryRepository.getDataGroupByLocationName(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLocationName()).orElse("") +
                "%",
                (request.getPageNumber() - 1) * request.getItemPerPage(),
                request.getItemPerPage()
            )
        );
        return inventoriesResponse;
    }

    public InventoriesResponse getDataDetail(InventoryRequestDTO request) {
        InventoriesResponse inventoriesResponse = new InventoriesResponse();
        inventoriesResponse.setTotalItems(
            this.inventoryRepository.getTotalDataDetail(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLotNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getUserData4()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getUserData5()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLocationName()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getMaterialIdentifier()).orElse(
                    ""
                ) +
                "%"
            )
        );
        inventoriesResponse.setInventories(
            this.inventoryRepository.getDataDetail(
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLotNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getUserData4()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getUserData5()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLocationName()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getMaterialIdentifier()).orElse(
                    ""
                ) +
                "%",
                (request.getPageNumber() - 1) * request.getItemPerPage(),
                request.getItemPerPage()
            )
        );
        return inventoriesResponse;
    }

    public InventoriesResponse getInventoriesResponse(
        InventoryRequestDTO request
    ) {
        Page<InventoryResponse> page = this.getInventories(request);
        return new InventoriesResponse(
            (int) page.getTotalElements(),
            page.getContent()
        );
    }

    //    public InventoriesResponse getInventoriesResponse(
    //        InventoryRequestDTO request
    //    ) {
    //        List<InventoryResponse> inventories = this.getInventories(request);
    //        Integer totalItems = this.getTotalInventories(request);
    //        return new InventoriesResponse(totalItems, inventories);
    //    }

    //    public List<InventoryResponse> getInventories(InventoryRequestDTO request) {
    //        System.out.println("get pageNumber: " + request.getPageNumber());
    //
    //        String updatedDateEpoch = Optional.ofNullable(request.getUpdatedDate())
    //            .filter(dateStr -> !dateStr.isBlank())
    //            .map(dateStr -> {
    //                try {
    //                    // Nếu là số, giữ nguyên
    //                    if (dateStr.matches("\\d+")) {
    //                        return dateStr;
    //                    }
    //                    // Nếu là chuỗi ngày, parse sang epoch
    //                    LocalDate date = LocalDate.parse(
    //                        dateStr,
    //                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    //                    );
    //                    return String.valueOf(
    //                        date
    //                            .atStartOfDay(ZoneId.systemDefault())
    //                            .toEpochSecond()
    //                    );
    //                } catch (Exception e) {
    //                    return null;
    //                }
    //            })
    //            .orElse(null);
    //
    //        System.out.println("updatedDateEpoch: " + updatedDateEpoch);
    //        return this.inventoryRepository.getInventories(
    //            "%" +
    //            Optional.ofNullable(request.getMaterialIdentifier()).orElse("") +
    //            "%",
    //            "%" + Optional.ofNullable(request.getStatus()).orElse("") + "%",
    //            "%" + Optional.ofNullable(request.getPartNumber()).orElse("") + "%",
    //            request.getQuantity(),
    //            request.getAvailableQuantity(),
    //            "%" + Optional.ofNullable(request.getLotNumber()).orElse("") + "%",
    //            "%" + Optional.ofNullable(request.getUserData4()).orElse("") + "%",
    //            "%" + Optional.ofNullable(request.getUserData5()).orElse("") + "%",
    //            "%" +
    //            Optional.ofNullable(request.getLocationName()).orElse("") +
    //            "%",
    //            "%" +
    //            Optional.ofNullable(request.getExpirationDate()).orElse("") +
    //            "%",
    //            updatedDateEpoch,
    //            (request.getPageNumber() - 1) * request.getItemPerPage(),
    //            request.getItemPerPage()
    //        );
    //    }
    public Page<InventoryResponse> getInventories(InventoryRequestDTO request) {
        String updatedDateEpoch = Optional.ofNullable(request.getUpdatedDate())
            .filter(dateStr -> !dateStr.isBlank())
            .map(dateStr -> {
                try {
                    if (dateStr.matches("\\d+")) {
                        return dateStr;
                    }
                    LocalDate date = LocalDate.parse(
                        dateStr,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    );
                    return String.valueOf(
                        date
                            .atStartOfDay(ZoneId.systemDefault())
                            .toEpochSecond()
                    );
                } catch (Exception e) {
                    return null;
                }
            })
            .orElse(null);

        int pageNumber = request.getPageNumber();
        int pageSize = request.getItemPerPage();
        int offset = (pageNumber - 1) * pageSize;

        List<InventoryResponse> inventories =
            inventoryRepository.getInventories(
                "%" +
                Optional.ofNullable(request.getMaterialIdentifier()).orElse(
                    ""
                ) +
                "%",
                "%" + Optional.ofNullable(request.getStatus()).orElse("") + "%",
                "%" +
                Optional.ofNullable(request.getPartNumber()).orElse("") +
                "%",
                request.getQuantity(),
                request.getAvailableQuantity(),
                "%" +
                Optional.ofNullable(request.getLotNumber()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getUserData4()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getUserData5()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getLocationName()).orElse("") +
                "%",
                "%" +
                Optional.ofNullable(request.getExpirationDate()).orElse("") +
                "%",
                updatedDateEpoch,
                offset,
                pageSize
            );

        int totalItems = inventoryRepository.getTotalInventories(
            "%" +
            Optional.ofNullable(request.getMaterialIdentifier()).orElse("") +
            "%",
            "%" + Optional.ofNullable(request.getStatus()).orElse("") + "%",
            "%" + Optional.ofNullable(request.getPartNumber()).orElse("") + "%",
            request.getQuantity(),
            request.getAvailableQuantity(),
            "%" + Optional.ofNullable(request.getLotNumber()).orElse("") + "%",
            "%" + Optional.ofNullable(request.getUserData4()).orElse("") + "%",
            "%" + Optional.ofNullable(request.getUserData5()).orElse("") + "%",
            "%" +
            Optional.ofNullable(request.getLocationName()).orElse("") +
            "%",
            "%" +
            Optional.ofNullable(request.getExpirationDate()).orElse("") +
            "%",
            updatedDateEpoch
        );

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return new PageImpl<>(inventories, pageable, totalItems);
    }

    public Integer getTotalInventories(InventoryRequestDTO request) {
        String updatedDate = Optional.ofNullable(request.getUpdatedDate())
            .filter(dateStr -> !dateStr.isBlank())
            .map(dateStr -> {
                try {
                    if (dateStr.matches("\\d+")) {
                        return dateStr;
                    }
                    LocalDate date = LocalDate.parse(
                        dateStr,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    );
                    return String.valueOf(
                        date
                            .atStartOfDay(ZoneId.systemDefault())
                            .toEpochSecond()
                    );
                } catch (Exception e) {
                    return null;
                }
            })
            .orElse(null);
        return this.inventoryRepository.getTotalInventories(
            "%" +
            Optional.ofNullable(request.getMaterialIdentifier()).orElse("") +
            "%",
            "%" + Optional.ofNullable(request.getStatus()).orElse("") + "%",
            "%" + Optional.ofNullable(request.getPartNumber()).orElse("") + "%",
            request.getQuantity(),
            request.getAvailableQuantity(),
            "%" + Optional.ofNullable(request.getLotNumber()).orElse("") + "%",
            "%" + Optional.ofNullable(request.getUserData4()).orElse("") + "%",
            "%" + Optional.ofNullable(request.getUserData5()).orElse("") + "%",
            "%" +
            Optional.ofNullable(request.getLocationName()).orElse("") +
            "%",
            "%" +
            Optional.ofNullable(request.getExpirationDate()).orElse("") +
            "%",
            updatedDate
        );
    }

    @Transactional
    public boolean updateInventory(List<InventoryDTO> request) {
        boolean updated = false;
        try {
            for (InventoryDTO inventoryDTO : request) {
                Inventory inventory =
                    this.inventoryRepository.findAllByMaterialIdentifier(
                        inventoryDTO.getMaterialIdentifier()
                    );
                if (inventory != null) {
                    long timestampNow = Instant.now().getEpochSecond();
                    updated = true;
                    inventory.setQuantity(inventoryDTO.getQuantity());
                    inventory.setLocationId(inventoryDTO.getLocationId());
                    inventory.setStatus(inventoryDTO.getStatus());
                    inventory.setUpdatedBy(inventoryDTO.getUpdatedBy());
                    inventory.setUpdatedDate(timestampNow);

                    if (inventoryDTO.getType()) {
                        // *gia han
                        long timestamp = LocalDate.now()
                            .plusDays(15)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toEpochSecond();
                        inventory.setExpirationDate(timestamp);
                    }
                    this.inventoryRepository.save(inventory);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error updating inventory:: " + e.getMessage()
            );
        }
        return updated;
    }

    public InventoryResponse findByInventoryId(String materialIdentifier) {
        return inventoryRepository.findResponseByMaterialIdentifier(
            materialIdentifier
        );
    }

    public List<InventoryResponse> findByInventoryLocationName(
        String locationName
    ) {
        return inventoryRepository.getInventoriesByLocation(locationName);
    }

    public List<String> getWarehouseSummaryMaterialTypes() {
        return warehouseSummarySapCacheService.getDistinctGroupNames();
    }

    public WarehouseAreaLocationsResponse getWarehouseAreaLocations(
        String areaCode,
        String areaName,
        String selectedMaterialType,
        String locationSearch
    ) {
        String normalizedAreaCode = Optional.ofNullable(areaCode)
            .orElse("")
            .trim();
        String normalizedAreaName = Optional.ofNullable(areaName)
            .orElse("")
            .trim();
        String normalizedMaterialType = Optional.ofNullable(
            selectedMaterialType
        )
            .orElse("")
            .trim();
        String normalizedSearch = Optional.ofNullable(locationSearch)
            .orElse("")
            .trim();

        int searchLocations = normalizedSearch.isEmpty() ? 0 : 1;
        String locationPattern = searchLocations == 1
            ? "%" + normalizedSearch + "%"
            : "%";

        List<WarehouseAreaLocationRawResponse> rawRows =
            inventoryRepository.getWarehouseAreaLocationBreakdown(
                normalizedAreaCode,
                normalizedAreaName,
                searchLocations,
                locationPattern
            );

        Map<Long, WarehouseAreaLocationDto> grouped = new LinkedHashMap<>();
        Map<Long, Map<String, Integer>> typeQtyByLocation = new HashMap<>();
        Map<Long, Set<String>> itemCodesByLocation = new HashMap<>();

        for (WarehouseAreaLocationRawResponse row : rawRows) {
            if (row.getLocationId() == null) {
                continue;
            }

            WarehouseAreaLocationDto dto = grouped.computeIfAbsent(
                row.getLocationId(),
                id -> {
                    WarehouseAreaLocationDto loc =
                        new WarehouseAreaLocationDto();
                    loc.setLocationId(row.getLocationId());
                    loc.setLocationName(row.getLocationName());
                    loc.setLocationFullName(row.getLocationFullName());
                    loc.setXPos(row.getXPos());
                    loc.setYPos(row.getYPos());
                    loc.setProductLimit(row.getProductLimit());
                    loc.setTotalQuantity(0);
                    loc.setMaterialIdentifierCount(0);
                    return loc;
                }
            );

            int qty = Optional.ofNullable(row.getQuantity()).orElse(0);
            int qrCount = Optional.ofNullable(
                row.getMaterialIdentifierCount()
            ).orElse(0);
            dto.setTotalQuantity(
                Optional.ofNullable(dto.getTotalQuantity()).orElse(0) + qty
            );
            dto.setMaterialIdentifierCount(
                Optional.ofNullable(dto.getMaterialIdentifierCount()).orElse(
                    0
                ) +
                qrCount
            );

            String itemCode = Optional.ofNullable(row.getItemCode())
                .map(String::trim)
                .orElse("");
            if (!itemCode.isEmpty() && qty > 0) {
                itemCodesByLocation
                    .computeIfAbsent(row.getLocationId(), k ->
                        new LinkedHashSet<>()
                    )
                    .add(itemCode);
                String groupName =
                    warehouseSummarySapCacheService.resolveGroupName(itemCode);
                if (!"-".equals(groupName)) {
                    typeQtyByLocation
                        .computeIfAbsent(row.getLocationId(), k ->
                            new HashMap<>()
                        )
                        .merge(groupName, qty, Integer::sum);
                }
            }
        }

        Set<String> legendTypes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        for (WarehouseAreaLocationDto dto : grouped.values()) {
            Map<String, Integer> typeQty = typeQtyByLocation.getOrDefault(
                dto.getLocationId(),
                Map.of()
            );
            List<String> types = typeQty
                .entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
            dto.setMaterialTypes(types);
            dto.setMaterialTypeCount(types.size());
            dto.setDominantMaterialType(types.isEmpty() ? "-" : types.get(0));
            dto.setEmpty(
                Optional.ofNullable(dto.getTotalQuantity()).orElse(0) <= 0
            );

            boolean containsSelected =
                !normalizedMaterialType.isEmpty() &&
                !"-".equals(normalizedMaterialType) &&
                types
                    .stream()
                    .anyMatch(type ->
                        type.equalsIgnoreCase(normalizedMaterialType)
                    );
            dto.setContainsSelectedMaterialType(containsSelected);
            dto.setItemCodes(
                new ArrayList<>(
                    itemCodesByLocation.getOrDefault(
                        dto.getLocationId(),
                        Set.of()
                    )
                )
            );

            legendTypes.addAll(types);
        }

        List<WarehouseAreaLocationDto> locations = grouped
            .values()
            .stream()
            .sorted(
                Comparator.comparing(
                    (WarehouseAreaLocationDto loc) ->
                        !normalizedMaterialType.isEmpty() &&
                        !loc.isContainsSelectedMaterialType()
                ).thenComparing(
                    loc ->
                        Optional.ofNullable(loc.getLocationName()).orElse(""),
                    String.CASE_INSENSITIVE_ORDER
                )
            )
            .collect(Collectors.toList());

        WarehouseAreaLocationsResponse response =
            new WarehouseAreaLocationsResponse();
        response.setAreaCode(normalizedAreaCode);
        response.setAreaName(normalizedAreaName);
        response.setSelectedMaterialType(normalizedMaterialType);
        response.setLegendMaterialTypes(new ArrayList<>(legendTypes));
        response.setLocations(locations);
        response.setTotalCount(locations.size());
        return response;
    }

    public WarehouseAreaInventoryItemsResponse getWarehouseAreaInventoryItems(
        String areaCode,
        String areaName,
        String materialSearch,
        String searchField,
        Integer previewLimit
    ) {
        String normalizedAreaCode = Optional.ofNullable(areaCode)
            .orElse("")
            .trim();
        String normalizedAreaName = Optional.ofNullable(areaName)
            .orElse("")
            .trim();
        String normalizedSearch = Optional.ofNullable(materialSearch)
            .orElse("")
            .trim();

        int searchMaterials = normalizedSearch.isEmpty() ? 0 : 1;
        int fieldMode = resolveMaterialSearchField(
            searchField,
            searchMaterials
        );
        String materialPattern = searchMaterials == 1
            ? "%" + normalizedSearch + "%"
            : "%";

        int useNameItemCodes = 0;
        List<String> nameItemCodes = List.of("");
        if (searchMaterials == 1 && (fieldMode == 0 || fieldMode == 1)) {
            List<String> matchedCodes =
                warehouseSummarySapCacheService.findItemCodesByNameContaining(
                    normalizedSearch
                );
            if (fieldMode == 1 && matchedCodes.isEmpty()) {
                return emptyAreaInventoryItemsResponse(
                    searchMaterials,
                    previewLimit
                );
            }
            if (!matchedCodes.isEmpty()) {
                useNameItemCodes = 1;
                nameItemCodes = WarehouseItemCodeFilterUtils.dedupeItemCodes(
                    matchedCodes
                );
            }
        }

        int defaultPreview = Optional.ofNullable(previewLimit).orElse(100);
        int rowLimit = searchMaterials == 0
            ? defaultPreview
            : fieldMode == 1 && useNameItemCodes == 1
                ? NAME_SEARCH_ROW_LIMIT
                : 50000;

        int safeTotal;
        List<WarehouseAreaInventoryItemRawResponse> rawRows;
        if (fieldMode == 1 && useNameItemCodes == 1) {
            rawRows = getWarehouseAreaInventoryItemsByItemCodes(
                normalizedAreaCode,
                normalizedAreaName,
                nameItemCodes,
                rowLimit
            );
            safeTotal = rawRows.size();
        } else if (
            useNameItemCodes == 1 &&
            nameItemCodes.size() > NAME_ITEM_CODES_BATCH_SIZE
        ) {
            rawRows = getWarehouseAreaInventoryItemsBatched(
                normalizedAreaCode,
                normalizedAreaName,
                searchMaterials,
                fieldMode,
                materialPattern,
                nameItemCodes,
                rowLimit
            );
            safeTotal = searchMaterials == 1
                ? rawRows.size()
                : countWarehouseAreaInventoryItemsBatched(
                    normalizedAreaCode,
                    normalizedAreaName,
                    searchMaterials,
                    fieldMode,
                    materialPattern,
                    nameItemCodes
                );
        } else if (searchMaterials == 1) {
            rawRows = inventoryRepository.getWarehouseAreaInventoryItems(
                normalizedAreaCode,
                normalizedAreaName,
                searchMaterials,
                fieldMode,
                materialPattern,
                useNameItemCodes,
                nameItemCodes,
                rowLimit
            );
            safeTotal = rawRows.size();
        } else {
            Integer totalCount =
                inventoryRepository.countWarehouseAreaInventoryItems(
                    normalizedAreaCode,
                    normalizedAreaName,
                    searchMaterials,
                    fieldMode,
                    materialPattern,
                    useNameItemCodes,
                    nameItemCodes
                );
            safeTotal = Optional.ofNullable(totalCount).orElse(0);
            rawRows = inventoryRepository.getWarehouseAreaInventoryItems(
                normalizedAreaCode,
                normalizedAreaName,
                searchMaterials,
                fieldMode,
                materialPattern,
                useNameItemCodes,
                nameItemCodes,
                rowLimit
            );
        }

        List<WarehouseAreaInventoryItemDto> items = mapAreaInventoryItems(
            rawRows
        );

        WarehouseAreaInventoryItemsResponse response =
            new WarehouseAreaInventoryItemsResponse();
        response.setItems(items);
        response.setTotalCount(safeTotal);
        response.setPreviewLimit(searchMaterials == 0 ? defaultPreview : null);
        response.setPreviewLimited(
            searchMaterials == 0 && safeTotal > items.size()
        );
        return response;
    }

    public WarehouseLocationMaterialsResponse getLocationInventoryMaterials(
        String locationName,
        String qrCode,
        String materialName,
        String itemCode,
        String partNumber,
        String lotNumber,
        String materialType,
        String status,
        String locationFullName,
        Integer page,
        Integer size
    ) {
        String normalizedLocation = Optional.ofNullable(locationName)
            .orElse("")
            .trim();
        if (normalizedLocation.isEmpty()) {
            return emptyLocationMaterialsResponse(
                Optional.ofNullable(page).orElse(0),
                Optional.ofNullable(size).orElse(25)
            );
        }

        String qrFilter = extractQrSearchToken(qrCode);
        String nameFilter = normalizeFilter(materialName);
        String itemCodeFilter = normalizeFilter(itemCode);
        String partNumberFilter = normalizeFilter(partNumber);
        String lotFilter = normalizeFilter(lotNumber);
        String typeFilter = normalizeFilter(materialType);
        String statusFilter = normalizeFilter(status);
        String locationFullNameFilter = normalizeFilter(locationFullName);

        int filterQr = qrFilter.isEmpty() ? 0 : 1;
        int filterItemCode = itemCodeFilter.isEmpty() ? 0 : 1;
        int filterPartNumber = partNumberFilter.isEmpty() ? 0 : 1;
        int filterLot = lotFilter.isEmpty() ? 0 : 1;
        int filterStatus = statusFilter.isEmpty() ? 0 : 1;
        int filterLocationFullName = locationFullNameFilter.isEmpty() ? 0 : 1;

        String qrPattern = filterQr == 1 ? "%" + qrFilter + "%" : "%";
        String itemCodePattern = filterItemCode == 1
            ? "%" + itemCodeFilter + "%"
            : "%";
        String partNumberPattern = filterPartNumber == 1
            ? "%" + partNumberFilter + "%"
            : "%";
        String lotPattern = filterLot == 1 ? "%" + lotFilter + "%" : "%";
        String locationFullNamePattern = filterLocationFullName == 1
            ? "%" + locationFullNameFilter + "%"
            : "%";

        int useOitmItemCodes = 0;
        List<String> oitmItemCodes = List.of("");
        boolean hasNameFilter = !nameFilter.isEmpty();
        boolean hasTypeFilter = !typeFilter.isEmpty();
        if (hasNameFilter || hasTypeFilter) {
            List<String> matchedCodes = hasNameFilter
                ? warehouseSummarySapCacheService.findItemCodesByNameContaining(
                    nameFilter
                )
                : null;
            if (hasTypeFilter) {
                List<String> typeCodes =
                    warehouseSummarySapCacheService.findItemCodesByGroupNameContaining(
                        typeFilter
                    );
                matchedCodes = matchedCodes == null
                    ? typeCodes
                    : matchedCodes
                        .stream()
                        .filter(typeCodes::contains)
                        .collect(Collectors.toList());
            }
            if (matchedCodes == null || matchedCodes.isEmpty()) {
                int safePage = Math.max(0, Optional.ofNullable(page).orElse(0));
                int safeSize = Math.max(
                    1,
                    Math.min(200, Optional.ofNullable(size).orElse(25))
                );
                return emptyLocationMaterialsResponse(safePage, safeSize);
            }
            useOitmItemCodes = 1;
            oitmItemCodes = matchedCodes;
        }

        int safePage = Math.max(0, Optional.ofNullable(page).orElse(0));
        int safeSize = Math.max(
            1,
            Math.min(200, Optional.ofNullable(size).orElse(25))
        );
        int rowOffset = safePage * safeSize;

        Integer totalCount =
            inventoryRepository.countLocationInventoryMaterials(
                normalizedLocation,
                filterQr,
                qrPattern,
                filterItemCode,
                itemCodePattern,
                filterPartNumber,
                partNumberPattern,
                filterLocationFullName,
                locationFullNamePattern,
                filterLot,
                lotPattern,
                filterStatus,
                statusFilter,
                useOitmItemCodes,
                oitmItemCodes
            );
        int safeTotal = Optional.ofNullable(totalCount).orElse(0);

        List<WarehouseLocationInventoryRawResponse> rawRows =
            inventoryRepository.getLocationInventoryMaterialsPage(
                normalizedLocation,
                filterQr,
                qrPattern,
                filterItemCode,
                itemCodePattern,
                filterPartNumber,
                partNumberPattern,
                filterLocationFullName,
                locationFullNamePattern,
                filterLot,
                lotPattern,
                filterStatus,
                statusFilter,
                useOitmItemCodes,
                oitmItemCodes,
                rowOffset,
                safeSize
            );

        List<WarehouseLocationMaterialItemDto> items = new ArrayList<>();
        for (WarehouseLocationInventoryRawResponse row : rawRows) {
            WarehouseLocationMaterialItemDto dto =
                new WarehouseLocationMaterialItemDto();
            dto.setMaterialIdentifier(row.getMaterialIdentifier());
            dto.setPartNumber(row.getPartNumber());
            dto.setQuantity(row.getQuantity());
            dto.setStatus(row.getStatus());
            dto.setLotNumber(row.getLotNumber());
            String code = Optional.ofNullable(row.getItemCode())
                .map(String::trim)
                .orElse("");
            dto.setItemCode(code);
            dto.setMaterialName(
                code.isEmpty()
                    ? ""
                    : warehouseSummarySapCacheService.resolveItemName(code)
            );
            dto.setMaterialType(
                code.isEmpty()
                    ? "-"
                    : warehouseSummarySapCacheService.resolveGroupName(code)
            );
            dto.setLocationFullName(
                Optional.ofNullable(row.getLocationFullName())
                    .map(String::trim)
                    .orElse("")
            );
            items.add(dto);
        }

        WarehouseLocationMaterialsResponse response =
            new WarehouseLocationMaterialsResponse();
        response.setItems(items);
        response.setTotalCount(safeTotal);
        response.setPage(safePage);
        response.setSize(safeSize);
        return response;
    }

    private WarehouseLocationMaterialsResponse emptyLocationMaterialsResponse(
        int page,
        int size
    ) {
        WarehouseLocationMaterialsResponse response =
            new WarehouseLocationMaterialsResponse();
        response.setItems(List.of());
        response.setTotalCount(0);
        response.setPage(page);
        response.setSize(size);
        return response;
    }

    private WarehouseAreaInventoryItemsResponse emptyAreaInventoryItemsResponse(
        int searchMaterials,
        Integer previewLimit
    ) {
        WarehouseAreaInventoryItemsResponse response =
            new WarehouseAreaInventoryItemsResponse();
        response.setItems(List.of());
        response.setTotalCount(0);
        response.setPreviewLimit(
            searchMaterials == 0
                ? Optional.ofNullable(previewLimit).orElse(100)
                : null
        );
        response.setPreviewLimited(false);
        return response;
    }

    private int resolveMaterialSearchField(
        String searchField,
        int searchMaterials
    ) {
        if (searchMaterials == 0) {
            return 0;
        }
        String normalized = Optional.ofNullable(searchField)
            .orElse("")
            .trim()
            .toLowerCase(Locale.ROOT);
        if ("name".equals(normalized) || "ten".equals(normalized)) {
            return 1;
        }
        if ("sap".equals(normalized) || "ma".equals(normalized)) {
            return 2;
        }
        return 0;
    }

    private List<WarehouseAreaInventoryItemDto> mapAreaInventoryItems(
        List<WarehouseAreaInventoryItemRawResponse> rawRows
    ) {
        List<WarehouseAreaInventoryItemDto> items = new ArrayList<>();
        for (WarehouseAreaInventoryItemRawResponse row : rawRows) {
            WarehouseAreaInventoryItemDto dto =
                new WarehouseAreaInventoryItemDto();
            dto.setLocationId(row.getLocationId());
            dto.setLocationName(row.getLocationName());
            dto.setLocationFullName(row.getLocationFullName());
            dto.setMaterialIdentifier(row.getMaterialIdentifier());
            dto.setItemCode(row.getItemCode());
            dto.setPartNumber(row.getPartNumber());
            dto.setQuantity(row.getQuantity());
            dto.setStatus(row.getStatus());
            dto.setLotNumber(row.getLotNumber());
            String itemCode = Optional.ofNullable(row.getItemCode())
                .map(String::trim)
                .orElse("");
            dto.setMaterialName(
                itemCode.isEmpty()
                    ? ""
                    : warehouseSummarySapCacheService.resolveItemName(itemCode)
            );
            dto.setMaterialType(
                itemCode.isEmpty()
                    ? "-"
                    : warehouseSummarySapCacheService.resolveGroupName(itemCode)
            );
            items.add(dto);
        }
        return items;
    }

    public WarehouseSummaryResponse getWarehouseSummary(
        WarehouseSummaryRequestDTO request
    ) {
        if (Boolean.TRUE.equals(request.getRefreshCache())) {
            warehouseSummaryDataCacheService.invalidate();
        }

        String warehouseFilter = normalizeFilter(request.getWarehouseName());
        String warehouseAreaFilter = normalizeFilter(
            request.getWarehouseAreaName()
        );
        String materialTypeFilter = normalizeFilter(request.getMaterialType());

        int pageNumber = Optional.ofNullable(request.getPageNumber()).orElse(1);
        int itemPerPage = Optional.ofNullable(request.getItemPerPage()).orElse(
            50
        );
        int offset = Math.max(0, (pageNumber - 1) * itemPerPage);

        WarehouseSummaryDataCacheService.CachedSnapshot snapshot =
            warehouseSummaryDataCacheService.getOrLoadFullSnapshot();
        List<WarehouseAreaItemSummaryResponse> rawRows = snapshot.getRawRows();
        WarehouseSummaryStatsCombined combinedStats = snapshot.getStats();

        List<WarehouseAreaSummaryResponse> aggregated = new ArrayList<>(
            aggregateByAreaAndGroup(rawRows)
        );

        if (!warehouseFilter.isEmpty()) {
            aggregated = aggregated
                .stream()
                .filter(row -> matchesWarehouseFilter(row, warehouseFilter))
                .collect(Collectors.toList());
        }

        if (!warehouseAreaFilter.isEmpty()) {
            aggregated = aggregated
                .stream()
                .filter(row ->
                    containsIgnoreCase(row.getAreaName(), warehouseAreaFilter)
                )
                .collect(Collectors.toList());
        }

        if (!materialTypeFilter.isEmpty()) {
            aggregated = aggregated
                .stream()
                .filter(row ->
                    Optional.ofNullable(row.getMaterialType())
                        .orElse("")
                        .toLowerCase(Locale.ROOT)
                        .contains(materialTypeFilter)
                )
                .collect(Collectors.toList());
        }

        aggregated.sort(
            Comparator.comparing((WarehouseAreaSummaryResponse row) ->
                !isWarehouseInfoComplete(row)
            )
                .thenComparing(
                    (WarehouseAreaSummaryResponse row) ->
                        Optional.ofNullable(row.getAreaCode()).orElse(""),
                    String.CASE_INSENSITIVE_ORDER
                )
                .thenComparing(
                    row -> Optional.ofNullable(row.getAreaName()).orElse(""),
                    String.CASE_INSENSITIVE_ORDER
                )
                .thenComparing(
                    row ->
                        Optional.ofNullable(row.getMaterialType()).orElse(""),
                    String.CASE_INSENSITIVE_ORDER
                )
        );

        int totalItems = aggregated.size();
        int toIndex = Math.min(offset + itemPerPage, totalItems);
        List<WarehouseAreaSummaryResponse> pageRows = offset >= totalItems
            ? List.of()
            : aggregated.subList(offset, toIndex);

        WarehouseSummaryStatsDto stats = buildWarehouseSummaryStats(
            aggregated,
            combinedStats,
            warehouseFilter,
            warehouseAreaFilter,
            materialTypeFilter
        );

        WarehouseSummaryResponse response = new WarehouseSummaryResponse();
        response.setTotalItems(totalItems);
        response.setStats(stats);
        response.setInventories(new ArrayList<>(pageRows));

        if (Boolean.TRUE.equals(request.getIncludeOverview())) {
            response.setOverviewAreas(buildOverviewAreas(aggregated));
            response.setOverviewMaterialTypes(
                buildOverviewMaterialTypes(aggregated)
            );
        }
        return response;
    }

    private WarehouseSummaryStatsDto buildWarehouseSummaryStats(
        List<WarehouseAreaSummaryResponse> aggregated,
        WarehouseSummaryStatsCombined combinedStats,
        String warehouseFilter,
        String warehouseAreaFilter,
        String materialTypeFilter
    ) {
        boolean hasFilter =
            !warehouseFilter.isEmpty() ||
            !warehouseAreaFilter.isEmpty() ||
            !materialTypeFilter.isEmpty();

        WarehouseSummaryStatsDto stats = new WarehouseSummaryStatsDto();

        if (!hasFilter) {
            stats.setWarehouseCount(
                Optional.ofNullable(locationRepository.countAllAreas()).orElse(
                    0
                )
            );
            stats.setLocationCount(
                Optional.ofNullable(combinedStats)
                    .map(WarehouseSummaryStatsCombined::getLocationCount)
                    .orElse(0)
            );
            stats.setEmptyLocationCount(
                Optional.ofNullable(combinedStats)
                    .map(WarehouseSummaryStatsCombined::getEmptyLocationCount)
                    .orElse(0)
            );
            stats.setAvailableQuantity(
                Optional.ofNullable(combinedStats)
                    .map(WarehouseSummaryStatsCombined::getAvailableQuantity)
                    .orElse(0L)
            );
            stats.setUnavailableQuantity(
                Optional.ofNullable(combinedStats)
                    .map(WarehouseSummaryStatsCombined::getUnavailableQuantity)
                    .orElse(0L)
            );
            stats.setMaterialTypeCount(
                (int) aggregated
                    .stream()
                    .map(WarehouseAreaSummaryResponse::getMaterialType)
                    .filter(
                        name ->
                            name != null && !name.isBlank() && !"-".equals(name)
                    )
                    .distinct()
                    .count()
            );
            return stats;
        }

        List<String> matchingAreaCodes = aggregated
            .stream()
            .map(WarehouseAreaSummaryResponse::getAreaCode)
            .filter(this::hasDisplayValue)
            .map(String::trim)
            .distinct()
            .collect(Collectors.toList());

        stats.setWarehouseCount(matchingAreaCodes.size());
        stats.setMaterialTypeCount(
            (int) aggregated
                .stream()
                .map(WarehouseAreaSummaryResponse::getMaterialType)
                .filter(
                    name -> name != null && !name.isBlank() && !"-".equals(name)
                )
                .distinct()
                .count()
        );
        stats.setAvailableQuantity(
            aggregated
                .stream()
                .mapToLong(row ->
                    Optional.ofNullable(row.getQuantity()).orElse(0)
                )
                .sum()
        );

        if (matchingAreaCodes.isEmpty()) {
            stats.setLocationCount(0);
            stats.setEmptyLocationCount(0);
            stats.setUnavailableQuantity(0L);
            return stats;
        }

        stats.setLocationCount(
            Optional.ofNullable(
                inventoryRepository.countStockLocationsInAreaCodes(
                    matchingAreaCodes
                )
            ).orElse(0)
        );
        stats.setEmptyLocationCount(
            Optional.ofNullable(
                inventoryRepository.countEmptyLocationsInAreaCodes(
                    matchingAreaCodes
                )
            ).orElse(0)
        );
        stats.setUnavailableQuantity(
            Optional.ofNullable(
                inventoryRepository.countUnavailableIdentifiersInAreaCodes(
                    matchingAreaCodes
                )
            ).orElse(0L)
        );
        return stats;
    }

    private List<WarehouseOverviewAreaDto> buildOverviewAreas(
        List<WarehouseAreaSummaryResponse> aggregated
    ) {
        Map<String, WarehouseOverviewAreaDto> grouped = new LinkedHashMap<>();
        for (WarehouseAreaSummaryResponse row : aggregated) {
            String areaCode = Optional.ofNullable(row.getAreaCode()).orElse(
                "-"
            );
            String areaName = Optional.ofNullable(row.getAreaName()).orElse("");
            String key = areaCode + "|" + areaName;

            WarehouseOverviewAreaDto dto = grouped.computeIfAbsent(key, k -> {
                WarehouseOverviewAreaDto area = new WarehouseOverviewAreaDto();
                area.setAreaCode(areaCode);
                area.setAreaName(areaName);
                area.setQuantity(0);
                area.setMaterialIdentifierCount(0);
                area.setMaterialTypes(new ArrayList<>());
                return area;
            });

            int quantity = Optional.ofNullable(row.getQuantity()).orElse(0);
            int qrCount = Optional.ofNullable(
                row.getMaterialIdentifierCount()
            ).orElse(0);
            dto.setQuantity(
                Optional.ofNullable(dto.getQuantity()).orElse(0) + quantity
            );
            dto.setMaterialIdentifierCount(
                Optional.ofNullable(dto.getMaterialIdentifierCount()).orElse(
                    0
                ) +
                qrCount
            );

            String materialType = Optional.ofNullable(row.getMaterialType())
                .map(String::trim)
                .orElse("");
            if (
                !materialType.isEmpty() &&
                !"-".equals(materialType) &&
                dto
                    .getMaterialTypes()
                    .stream()
                    .noneMatch(type -> type.equalsIgnoreCase(materialType))
            ) {
                dto.getMaterialTypes().add(materialType);
            }
        }

        // Always include areas without inventory so they are visible on the map
        // with quantity = 0.
        for (AreaAreaResponse area : locationRepository.getArea()) {
            String areaCode = Optional.ofNullable(area.getAreaName())
                .map(String::trim)
                .orElse("-");
            String areaName = Optional.ofNullable(area.getAreaDescription())
                .map(String::trim)
                .orElse("");
            String key = areaCode + "|" + areaName;
            grouped.computeIfAbsent(key, k -> {
                WarehouseOverviewAreaDto emptyArea =
                    new WarehouseOverviewAreaDto();
                emptyArea.setAreaCode(areaCode);
                emptyArea.setAreaName(areaName);
                emptyArea.setQuantity(0);
                emptyArea.setMaterialIdentifierCount(0);
                emptyArea.setMaterialTypes(new ArrayList<>());
                emptyArea.setMaterialTypeCount(0);
                return emptyArea;
            });
        }

        for (WarehouseOverviewAreaDto dto : grouped.values()) {
            dto.setMaterialTypeCount(dto.getMaterialTypes().size());
            dto.getMaterialTypes().sort(String.CASE_INSENSITIVE_ORDER);
        }

        return grouped
            .values()
            .stream()
            .sorted(
                Comparator.comparing(
                    (WarehouseOverviewAreaDto row) ->
                        Optional.ofNullable(row.getAreaCode()).orElse(""),
                    String.CASE_INSENSITIVE_ORDER
                ).thenComparing(
                    row -> Optional.ofNullable(row.getAreaName()).orElse(""),
                    String.CASE_INSENSITIVE_ORDER
                )
            )
            .collect(Collectors.toList());
    }

    private List<WarehouseOverviewMaterialTypeDto> buildOverviewMaterialTypes(
        List<WarehouseAreaSummaryResponse> aggregated
    ) {
        Map<String, WarehouseOverviewMaterialTypeDto> grouped =
            new LinkedHashMap<>();
        Map<String, Set<String>> areaCodesByType = new HashMap<>();

        for (WarehouseAreaSummaryResponse row : aggregated) {
            String materialType = Optional.ofNullable(row.getMaterialType())
                .map(String::trim)
                .orElse("");
            if (materialType.isEmpty() || "-".equals(materialType)) {
                continue;
            }

            WarehouseOverviewMaterialTypeDto dto = grouped.computeIfAbsent(
                materialType.toLowerCase(Locale.ROOT),
                k -> {
                    WarehouseOverviewMaterialTypeDto type =
                        new WarehouseOverviewMaterialTypeDto();
                    type.setMaterialType(materialType);
                    type.setQuantity(0);
                    type.setMaterialIdentifierCount(0);
                    type.setWarehouseCount(0);
                    return type;
                }
            );

            int quantity = Optional.ofNullable(row.getQuantity()).orElse(0);
            int qrCount = Optional.ofNullable(
                row.getMaterialIdentifierCount()
            ).orElse(0);
            dto.setQuantity(
                Optional.ofNullable(dto.getQuantity()).orElse(0) + quantity
            );
            dto.setMaterialIdentifierCount(
                Optional.ofNullable(dto.getMaterialIdentifierCount()).orElse(
                    0
                ) +
                qrCount
            );

            String areaCode = Optional.ofNullable(row.getAreaCode())
                .map(String::trim)
                .orElse("");
            if (!areaCode.isEmpty() && !"-".equals(areaCode)) {
                areaCodesByType
                    .computeIfAbsent(materialType.toLowerCase(Locale.ROOT), k ->
                        new LinkedHashSet<>()
                    )
                    .add(areaCode);
            }
        }

        for (Map.Entry<
            String,
            WarehouseOverviewMaterialTypeDto
        > entry : grouped.entrySet()) {
            Set<String> areaCodes = areaCodesByType.getOrDefault(
                entry.getKey(),
                Set.of()
            );
            entry.getValue().setWarehouseCount(areaCodes.size());
        }

        return grouped
            .values()
            .stream()
            .sorted(
                Comparator.comparing(
                    (WarehouseOverviewMaterialTypeDto row) ->
                        Optional.ofNullable(row.getMaterialType()).orElse(""),
                    String.CASE_INSENSITIVE_ORDER
                )
            )
            .collect(Collectors.toList());
    }

    private List<
        WarehouseAreaInventoryItemRawResponse
    > getWarehouseAreaInventoryItemsByItemCodes(
        String areaCode,
        String areaName,
        List<String> itemCodes,
        int rowLimit
    ) {
        List<String> uniqueCodes = WarehouseItemCodeFilterUtils.dedupeItemCodes(
            itemCodes
        );
        if (uniqueCodes.isEmpty()) {
            return List.of();
        }
        if (uniqueCodes.size() == 1) {
            return inventoryRepository.getWarehouseAreaInventoryItemsBySingleItemCode(
                areaCode,
                areaName,
                uniqueCodes.get(0),
                rowLimit
            );
        }
        String itemCodesCsv = WarehouseItemCodeFilterUtils.encodeItemCodesCsv(
            uniqueCodes
        );
        return inventoryRepository.getWarehouseAreaInventoryItemsByItemCodesCsv(
            areaCode,
            areaName,
            itemCodesCsv,
            rowLimit
        );
    }

    private int countWarehouseAreaInventoryItemsBatched(
        String areaCode,
        String areaName,
        int searchMaterials,
        int fieldMode,
        String materialPattern,
        List<String> nameItemCodes
    ) {
        return getWarehouseAreaInventoryItemsBatched(
            areaCode,
            areaName,
            searchMaterials,
            fieldMode,
            materialPattern,
            nameItemCodes,
            Integer.MAX_VALUE
        ).size();
    }

    private List<
        WarehouseAreaInventoryItemRawResponse
    > getWarehouseAreaInventoryItemsBatched(
        String areaCode,
        String areaName,
        int searchMaterials,
        int fieldMode,
        String materialPattern,
        List<String> nameItemCodes,
        int rowLimit
    ) {
        LinkedHashMap<String, WarehouseAreaInventoryItemRawResponse> merged =
            new LinkedHashMap<>();

        for (
            int index = 0;
            index < nameItemCodes.size();
            index += NAME_ITEM_CODES_BATCH_SIZE
        ) {
            if (merged.size() >= rowLimit) {
                break;
            }
            List<String> batch = nameItemCodes.subList(
                index,
                Math.min(
                    index + NAME_ITEM_CODES_BATCH_SIZE,
                    nameItemCodes.size()
                )
            );
            int remaining = Math.max(1, rowLimit - merged.size());
            List<WarehouseAreaInventoryItemRawResponse> rows =
                inventoryRepository.getWarehouseAreaInventoryItems(
                    areaCode,
                    areaName,
                    searchMaterials,
                    fieldMode,
                    materialPattern,
                    1,
                    batch,
                    fieldMode == 1 ? remaining : Integer.MAX_VALUE
                );
            for (WarehouseAreaInventoryItemRawResponse row : rows) {
                merged.putIfAbsent(buildAreaInventoryItemKey(row), row);
                if (merged.size() >= rowLimit) {
                    break;
                }
            }
        }

        return new ArrayList<>(merged.values());
    }

    private String buildAreaInventoryItemKey(
        WarehouseAreaInventoryItemRawResponse row
    ) {
        return String.join(
            "|",
            String.valueOf(row.getLocationId()),
            Optional.ofNullable(row.getMaterialIdentifier()).orElse(""),
            Optional.ofNullable(row.getStatus()).orElse("")
        );
    }

    private String normalizeFilter(String value) {
        return Optional.ofNullable(value)
            .orElse("")
            .trim()
            .toLowerCase(Locale.ROOT);
    }

    private String extractQrSearchToken(String value) {
        String trimmed = Optional.ofNullable(value).orElse("").trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        int hashIndex = trimmed.indexOf('#');
        if (hashIndex >= 0) {
            return trimmed.substring(0, hashIndex).trim();
        }
        return trimmed;
    }

    private String toLikePattern(String value) {
        return "%" + Optional.ofNullable(value).orElse("").trim() + "%";
    }

    private boolean matchesWarehouseFilter(
        WarehouseAreaSummaryResponse row,
        String filter
    ) {
        return containsIgnoreCase(row.getAreaCode(), filter);
    }

    private boolean containsIgnoreCase(String value, String filter) {
        if (filter.isEmpty()) {
            return true;
        }
        return Optional.ofNullable(value)
            .orElse("")
            .toLowerCase(Locale.ROOT)
            .contains(filter);
    }

    private List<WarehouseAreaSummaryResponse> aggregateByAreaAndGroup(
        List<WarehouseAreaItemSummaryResponse> rawRows
    ) {
        Map<String, WarehouseAreaSummaryRowDto> grouped = new LinkedHashMap<>();
        for (WarehouseAreaItemSummaryResponse row : rawRows) {
            String areaCode = Optional.ofNullable(row.getAreaCode()).orElse(
                "-"
            );
            String areaName = Optional.ofNullable(row.getAreaName()).orElse("");
            String itemCode = Optional.ofNullable(row.getItemCode())
                .map(String::trim)
                .orElse("");
            String groupName = warehouseSummarySapCacheService.resolveGroupName(
                itemCode
            );
            String key = areaCode + "|" + areaName + "|" + groupName;

            WarehouseAreaSummaryRowDto existing = grouped.get(key);
            int quantity = Optional.ofNullable(row.getQuantity()).orElse(0);
            int materialIdentifierCount = Optional.ofNullable(
                row.getMaterialIdentifierCount()
            ).orElse(0);
            if (existing == null) {
                grouped.put(
                    key,
                    new WarehouseAreaSummaryRowDto(
                        areaCode,
                        areaName,
                        groupName,
                        quantity,
                        materialIdentifierCount
                    )
                );
            } else {
                existing.setQuantity(
                    Optional.ofNullable(existing.getQuantity()).orElse(0) +
                    quantity
                );
                existing.setMaterialIdentifierCount(
                    Optional.ofNullable(
                        existing.getMaterialIdentifierCount()
                    ).orElse(0) +
                    materialIdentifierCount
                );
            }
        }
        return new ArrayList<>(grouped.values());
    }

    private boolean isWarehouseInfoComplete(WarehouseAreaSummaryResponse row) {
        return (
            hasDisplayValue(row.getAreaCode()) &&
            hasDisplayValue(row.getAreaName())
        );
    }

    private boolean hasDisplayValue(String value) {
        if (value == null) {
            return false;
        }
        String trimmed = value.trim();
        return !trimmed.isEmpty() && !"-".equals(trimmed);
    }
}
