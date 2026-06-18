package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.InventoriesResponse;
import com.mycompany.myapp.domain.WarehouseAreaSummaryRowDto;
import com.mycompany.myapp.domain.WarehouseSummaryResponse;
import com.mycompany.myapp.domain.WarehouseSummaryStatsDto;
import com.mycompany.myapp.service.dto.InventoryDTO;
import com.mycompany.myapp.service.dto.InventoryRequestDTO;
import com.mycompany.myapp.service.dto.WarehouseSummaryRequestDTO;
import com.mycompany.panacimmc.domain.Inventory;
import com.mycompany.panacimmc.domain.InventoryResponse;
import com.mycompany.panacimmc.domain.WarehouseAreaItemSummaryResponse;
import com.mycompany.panacimmc.domain.WarehouseAreaSummaryResponse;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

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

        int materialTypeCount = (int) aggregated
            .stream()
            .map(WarehouseAreaSummaryResponse::getMaterialType)
            .filter(
                name -> name != null && !name.isBlank() && !"-".equals(name)
            )
            .distinct()
            .count();

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

        WarehouseSummaryStatsDto stats = new WarehouseSummaryStatsDto();
        stats.setWarehouseCount(
            Optional.ofNullable(locationRepository.countAllAreas()).orElse(0)
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
        stats.setMaterialTypeCount(materialTypeCount);

        WarehouseSummaryResponse response = new WarehouseSummaryResponse();
        response.setTotalItems(totalItems);
        response.setStats(stats);
        response.setInventories(new ArrayList<>(pageRows));
        return response;
    }

    private String normalizeFilter(String value) {
        return Optional.ofNullable(value)
            .orElse("")
            .trim()
            .toLowerCase(Locale.ROOT);
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
