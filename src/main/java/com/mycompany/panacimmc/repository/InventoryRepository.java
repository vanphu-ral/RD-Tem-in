package com.mycompany.panacimmc.repository;

import com.mycompany.panacimmc.domain.Inventory;
import com.mycompany.panacimmc.domain.InventoryResponse;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    public Inventory findAllByMaterialIdentifier(String materialIdentifier);

    @Query(
        value = "SELECT  \n" +
        "    a.[Inventory_PartNumber] AS partNumber\n" +
        "    ,sum(a.[Inventory_Quantity]) AS quantity\n" +
        "    ,SUM(CASE WHEN a.Inventory_Status = 3 THEN a.Inventory_Quantity ELSE 0 END) AS availableQuantity \n" +
        "    ,count(a.[Inventory_MaterialIdentifier]) AS recordCount \n" +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_Quantity >0 " +
        "AND a.Inventory_PartNumber like ?1 " +
        "GROUP BY a.[Inventory_PartNumber] " +
        "ORDER BY a.Inventory_PartNumber desc " +
        "OFFSET ?2 ROWS FETCH NEXT ?3 ROWS ONLY ;\n",
        nativeQuery = true
    )
    public List<InventoryResponse> getDataGroupByPartNumber(
        String partNumber,
        Integer pageNumber,
        Integer itemPerPage
    );

    @Query(
        value = "SELECT count(*) from (select a.[Inventory_PartNumber] \n" +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_Quantity >0 " +
        "AND a.Inventory_PartNumber like ?1 " +
        "GROUP BY a.[Inventory_PartNumber] ) AS grouped_parts ;",
        nativeQuery = true
    )
    public Integer getTotalDataGroupByPartNumber(String partNumber);

    @Query(
        value = "SELECT  \n" +
        "    a.Inventory_PartNumber AS partNumber\n" +
        "    ,sum(a.Inventory_Quantity) AS quantity\n" +
        "    ,SUM(CASE WHEN a.Inventory_Status = 3 THEN a.Inventory_Quantity ELSE 0 END) AS availableQuantity \n" +
        "    ,count(a.Inventory_MaterialIdentifier) AS recordCount \n" +
        "    ,d1.InventoryMaterialTraceDetail_MaterialTraceDataValue as userData4 " +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_Quantity >0 " +
        "AND a.Inventory_PartNumber like ?1 " +
        "AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName like ?2 " +
        "GROUP BY a.Inventory_PartNumber, d1.InventoryMaterialTraceDetail_MaterialTraceDataValue " +
        "ORDER BY a.Inventory_PartNumber desc " +
        "OFFSET ?3 ROWS FETCH NEXT ?4 ROWS ONLY ;\n",
        nativeQuery = true
    )
    public List<InventoryResponse> getDataGroupByUserData4(
        String partNumber,
        String userData4,
        Integer pageNumber,
        Integer itemPerPage
    );

    @Query(
        value = "SELECT count(*) from ( " +
        "select a.Inventory_PartNumber " +
        ", d1.InventoryMaterialTraceDetail_MaterialTraceDataValue \n" +
        "FROM Inventory AS a \n" +
        "INNER JOIN Location AS b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace AS c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_Quantity >0 " +
        "AND a.Inventory_PartNumber like ?1 " +
        "AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName like ?2 " +
        "GROUP BY a.Inventory_PartNumber, d1.InventoryMaterialTraceDetail_MaterialTraceDataValue ) as resultss ;\n",
        nativeQuery = true
    )
    public Integer getTotalDataGroupByUserData4(
        String partNumber,
        String userData4
    );

    @Query(
        value = "SELECT  \n" +
        "    a.Inventory_PartNumber AS partNumber\n" +
        "    ,sum(a.Inventory_Quantity) AS quantity\n" +
        "    ,SUM(CASE WHEN a.Inventory_Status = 3 THEN a.Inventory_Quantity ELSE 0 END) AS availableQuantity \n" +
        "    ,count(a.Inventory_MaterialIdentifier) AS recordCount \n" +
        "    ,d1.InventoryMaterialTraceDetail_MaterialTraceDataValue as userData5 " +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 5'\n" +
        "\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_Quantity >0 " +
        "AND a.Inventory_PartNumber like ?1 " +
        "AND d1.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?2 " +
        "GROUP BY a.Inventory_PartNumber, d1.InventoryMaterialTraceDetail_MaterialTraceDataValue " +
        "ORDER BY a.Inventory_PartNumber desc " +
        "OFFSET ?3 ROWS FETCH NEXT ?4 ROWS ONLY ;\n",
        nativeQuery = true
    )
    public List<InventoryResponse> getDataGroupByUserData5(
        String partNumber,
        String userData5,
        Integer pageNumber,
        Integer itemPerPage
    );

    @Query(
        value = "SELECT count(*) from ( " +
        "select a.Inventory_PartNumber " +
        ", d1.InventoryMaterialTraceDetail_MaterialTraceDataValue \n" +
        "FROM Inventory AS a \n" +
        "INNER JOIN Location AS b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace AS c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 5'\n" +
        "\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_Quantity >0 " +
        "AND a.Inventory_PartNumber like ?1 " +
        "AND d1.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?2 " +
        "GROUP BY a.Inventory_PartNumber, d1.InventoryMaterialTraceDetail_MaterialTraceDataValue ) as resultss ;\n",
        nativeQuery = true
    )
    public Integer getTotalDataGroupByUserData5(
        String partNumber,
        String userData5
    );

    @Query(
        value = "SELECT  \n" +
        "    a.Inventory_PartNumber AS partNumber\n" +
        "    ,b.Location_FullName AS locationName\n" +
        "    ,sum(a.[Inventory_Quantity]) AS quantity\n" +
        "    ,SUM(CASE WHEN a.Inventory_Status = 3 THEN a.Inventory_Quantity ELSE 0 END) AS availableQuantity \n" +
        "    ,count(a.[Inventory_MaterialIdentifier]) AS recordCount \n" +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_Quantity >0 " +
        "AND a.Inventory_PartNumber like ?1 " +
        "AND b.Location_FullName like ?2 " +
        "GROUP BY a.Inventory_PartNumber,b.Location_FullName " +
        "ORDER BY a.Inventory_PartNumber desc " +
        "OFFSET ?3 ROWS FETCH NEXT ?4 ROWS ONLY ;\n",
        nativeQuery = true
    )
    public List<InventoryResponse> getDataGroupByLocationName(
        String partNumber,
        String locationName,
        Integer pageNumber,
        Integer itemPerPage
    );

    @Query(
        value = "SELECT count(*) from ( select a.Inventory_PartNumber,b.Location_FullName \n" +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_Quantity >0 " +
        "AND a.Inventory_PartNumber like ?1 " +
        "AND b.Location_FullName like ?2 " +
        "GROUP BY a.Inventory_PartNumber,b.Location_FullName) as result ;\n",
        nativeQuery = true
    )
    public Integer getTotalDataGroupByLocationName(
        String partNumber,
        String locationName
    );

    @Query(
        value = "SELECT  \n" +
        "    a.Inventory_PartNumber AS partNumber\n" +
        "    ,d2.InventoryMaterialTraceDetail_MaterialTraceDataValue  as lotNumber " +
        "    ,sum(a.[Inventory_Quantity]) AS quantity\n" +
        "    ,SUM(CASE WHEN a.Inventory_Status = 3 THEN a.Inventory_Quantity ELSE 0 END) AS availableQuantity \n" +
        "    ,count(a.[Inventory_MaterialIdentifier]) AS recordCount \n" +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_Quantity >0 " +
        "AND a.Inventory_PartNumber like ?1 " +
        "AND d2.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?2 " +
        "GROUP BY a.Inventory_PartNumber, d2.InventoryMaterialTraceDetail_MaterialTraceDataValue " +
        "ORDER BY a.Inventory_PartNumber desc " +
        "OFFSET ?3 ROWS FETCH NEXT ?4 ROWS ONLY ;\n",
        nativeQuery = true
    )
    public List<InventoryResponse> getDataGroupByLotNumber(
        String partNumber,
        String lotNumber,
        Integer pageNumber,
        Integer itemPerPage
    );

    @Query(
        value = "SELECT count(*) from ( select a.Inventory_PartNumber, d2.InventoryMaterialTraceDetail_MaterialTraceDataValue \n" +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_Quantity >0 " +
        "AND a.Inventory_PartNumber like ?1 " +
        "AND d2.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?2 " +
        "GROUP BY a.Inventory_PartNumber, d2.InventoryMaterialTraceDetail_MaterialTraceDataValue ) as result ;",
        nativeQuery = true
    )
    public Integer getTotalDataGroupByLotNumber(
        String partNumber,
        String lotNumber
    );

    @Query(
        value = "SELECT \n" +
        "    a.[Inventory_PartNumber] AS partNumber,\n" +
        "    a.[Inventory_Quantity] AS quantity,\n" +
        "    a.[Inventory_AvailableQuantity] AS availableQuantity,\n" +
        "    a.[Inventory_MaterialIdentifier] AS materialIdentifier,\n" +
        "    a.[Inventory_ExpirationDate] AS expirationDate,\n" +
        "    a.[Inventory_ReceivedDate] AS receivedDate,\n" +
        "    ISNULL(d1.InventoryMaterialTraceDetail_MaterialTraceDataValue, '') AS userData4,\n" +
        "    ISNULL(d2.InventoryMaterialTraceDetail_MaterialTraceDataValue, '') AS lotNumber,\n" +
        "    ISNULL(d3.InventoryMaterialTraceDetail_MaterialTraceDataValue, '') AS userData5,\n" +
        "    a.[Inventory_Status] AS status,\n" +
        "    b.Location_FullName AS locationName\n" +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId\n" +
        "OUTER APPLY (\n" +
        "    SELECT TOP 1 InventoryMaterialTraceDetail_MaterialTraceDataValue\n" +
        "    FROM InventoryMaterialTraceDetail\n" +
        "    WHERE InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id\n" +
        "      AND InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        ") d1\n" +
        "OUTER APPLY (\n" +
        "    SELECT TOP 1 InventoryMaterialTraceDetail_MaterialTraceDataValue\n" +
        "    FROM InventoryMaterialTraceDetail\n" +
        "    WHERE InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id\n" +
        "      AND InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'\n" +
        ") d2\n" +
        "OUTER APPLY (\n" +
        "    SELECT TOP 1 InventoryMaterialTraceDetail_MaterialTraceDataValue\n" +
        "    FROM InventoryMaterialTraceDetail\n" +
        "    WHERE InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id\n" +
        "      AND InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 5'\n" +
        ") d3\n" +
        "WHERE a.Inventory_Status IN (3, 6, 19)\n" +
        "  AND a.Inventory_Quantity > 0\n" +
        "  AND a.Inventory_PartNumber LIKE ?1\n" +
        "  AND ISNULL(d2.InventoryMaterialTraceDetail_MaterialTraceDataValue, '') LIKE ?2\n" +
        "  AND ISNULL(d1.InventoryMaterialTraceDetail_MaterialTraceDataValue, '') LIKE ?3\n" +
        "  AND ISNULL(d3.InventoryMaterialTraceDetail_MaterialTraceDataValue, '') LIKE ?4\n" +
        "  AND b.Location_FullName LIKE ?5\n" +
        "  AND a.Inventory_MaterialIdentifier LIKE ?6\n" +
        "ORDER BY a.Inventory_PartNumber DESC\n" +
        "OFFSET ?7 ROWS FETCH NEXT ?8 ROWS ONLY;",
        nativeQuery = true
    )
    public List<InventoryResponse> getDataDetail(
        String partNumber,
        String lotNumber,
        String userData4,
        String userData5,
        String locationName,
        String material,
        Integer pageNumber,
        Integer itemPerPage
    );

    @Query(
        value = "SELECT count(*) " +
        "FROM Inventory a " +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id " +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "LEFT JOIN InventoryMaterialTraceDetail d1 " +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id " +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4' " +
        "INNER JOIN InventoryMaterialTraceDetail d3 " +
        "  ON d3.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id " +
        " AND d3.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 5' " +
        "LEFT JOIN InventoryMaterialTraceDetail d2 " +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id " +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot' " +
        "WHERE a.Inventory_Status IN (3,6,19) " +
        "  AND a.Inventory_Quantity > 0 " +
        "  AND a.Inventory_PartNumber LIKE ?1 " +
        "  AND d2.InventoryMaterialTraceDetail_MaterialTraceDataValue LIKE ?2 " +
        "  AND d1.InventoryMaterialTraceDetail_MaterialTraceDataValue LIKE ?3 " +
        "  AND d3.InventoryMaterialTraceDetail_MaterialTraceDataValue LIKE ?4 " +
        "  AND b.Location_FullName LIKE ?5 " +
        "  AND a.Inventory_MaterialIdentifier LIKE ?6",
        nativeQuery = true
    )
    public Integer getTotalDataDetail(
        String partNumber,
        String lotNumber,
        String userData4,
        String userData5,
        String locationName,
        String material
    );

    @Query(
        value = "SELECT  \n" +
        "     a.[Inventory_Id] AS inventoryId\n" +
        "    ,a.[Inventory_PartId] AS partId\n" +
        "    ,a.[Inventory_PartNumber] AS partNumber\n" +
        "    ,a.[Inventory_TrackingType] AS trackingType\n" +
        "    ,a.[Inventory_MaterialTraceId] AS materialTraceId\n" +
        "    ,a.[Inventory_Quantity] AS quantity\n" +
        "    ,a.[Inventory_LocationId] AS locationId\n" +
        "    ,a.[Inventory_ParentLocationId] AS parentLocationId\n" +
        "    ,a.[Inventory_LastLocationId] AS lastLocationId\n" +
        "    ,a.[Inventory_MaterialControl] AS materialControl\n" +
        "    ,a.[Inventory_MaterialIdentifier] AS materialIdentifier\n" +
        "    ,a.[Inventory_Status] AS status\n" +
        "    ,a.[Inventory_ReservationReference] AS reservationReference\n" +
        "    ,a.[Inventory_ExpirationDate] AS expirationDate\n" +
        "    ,a.[Inventory_ReceivedDate] AS receivedDate\n" +
        "    ,a.[Inventory_UOMId] AS uomId\n" +
        "    ,a.[Inventory_UOMName] AS uomName\n" +
        "    ,a.[Inventory_UpdatedDate] AS updatedDate\n" +
        "    ,a.[Inventory_UpdatedBy] AS updatedBy\n" +
        "    ,a.[Inventory_LabelingStatus] AS labelingStatus\n" +
        "    ,a.[Inventory_Printer] AS printer\n" +
        "    ,a.[Inventory_SplicedMaterialIdentifier] AS splicedMaterialIdentifier\n" +
        "    ,a.[Inventory_SplicedInventoryId] AS splicedInventoryId\n" +
        "    ,a.[Inventory_CarrierId] AS carrierId\n" +
        "    ,a.[Inventory_CarrierNumber] AS carrierNumber\n" +
        "    ,a.[Inventory_ReservedQuantity] AS reservedQuantity\n" +
        "    ,a.[Inventory_CalculatedStatus] AS calculatedStatus\n" +
        "    ,a.[Inventory_InitialQuantity] AS initialQuantity\n" +
        "    ,a.[Inventory_AvailableQuantity] AS availableQuantity\n" +
        "    ,a.[Inventory_ConsumedQuantity] AS consumedQuantity\n" +
        "    ,a.[Inventory_ScrappedQuantity] AS scrappedQuantity\n" +
        "    ,a.[Inventory_ParentInventory_Id] AS parentInventoryId\n" +
        "    ,a.[Inventory_ParentMaterialIdentifier] AS parentMaterialIdentifier\n" +
        "    ,a.[Inventroy_MaterialName] AS materialName\n" +
        "    ,a.[Inventory_PU_Location] AS puLocation\n" +
        "    ,a.[Inventory_LifetimeCount] AS lifetimeCount\n" +
        "    ,a.[Inventory_Bulk_Barcode] AS bulkBarcode\n" +
        "    ,a.[Inventory_Is_Bulk] AS isBulk\n" +
        "    ,a.[Inventory_ManufacturingDate] AS manufacturingDate\n" +
        "    ,a.[Inventory_PartClass] AS partClass\n" +
        "    ,a.[Inventory_MaterialType] AS materialType\n" +
        "    ,a.[Inventory_CheckinDate] AS checkinDate\n" +
        "    ,a.[Inventory_UsageCount] AS usageCount\n" +
        "    ,a.[Inventory_PartAlternateNumbers_Id] AS partAlternateNumbersId\n" +
        "    ,a.[Inventory_ReelNumber] AS reelNumber\n" +
        "    ,a.[Inventory_MainReelId] AS mainReelId\n" +
        "    ,a.[Inventory_ReasonCode] AS reasonCode\n" +
        "    ,a.[Inventory_LastCarrierNumber] AS lastCarrierNumber\n" +
        "    ,b.Location_FullName AS locationName\n" +
        "    ,d1.InventoryMaterialTraceDetail_MaterialTraceDataValue as userData4 " +
        "    ,d2.InventoryMaterialTraceDetail_MaterialTraceDataValue  as lotNumber " +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where a.Inventory_Status in(3,6,19) and Inventory_Quantity >0 " +
        "ORDER BY a.Inventory_Id " +
        "OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY",
        nativeQuery = true
    )
    List<InventoryResponse> getDataNew(
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    @Modifying
    @Transactional
    @Query(
        value = "UPDATE Inventory SET " +
        "Inventory_Quantity= ?1 ," +
        "Inventory_Status=3, " +
        "Inventory_UpdatedBy= ?2 ," +
        "Inventory_ExpirationDate= ?3 , " +
        "Inventory_UpdatedDate= ?4 ," +
        "Inventory_LocationId = ?5 " +
        "where Inventory_MaterialIdentifier= ?6 ;\n",
        nativeQuery = true
    )
    public void extendIventory(
        Integer quantity,
        String updateBy,
        String expirationDate,
        String date,
        Long locationId,
        String materialIdentifier
    );

    @Modifying
    @Transactional
    @Query(
        value = "UPDATE Inventory SET " +
        "Inventory_Quantity= ?1 ," +
        "Inventory_Status=3, " +
        "Inventory_UpdatedBy= ?2 ," +
        "Inventory_UpdatedDate= ?3 ," +
        "Inventory_LocationId = ?4 " +
        "where Inventory_MaterialIdentifier = ?5 ;",
        nativeQuery = true
    )
    public void updateIventory(
        Integer quantity,
        String updateBy,
        String date,
        Long locationId,
        String materialIdentifier
    );

    //    @Query(
    //        value = "SELECT  \n" +
    //        "     a.[Inventory_Id] AS inventoryId\n" +
    //        "    ,a.[Inventory_PartId] AS partId\n" +
    //        "    ,a.[Inventory_PartNumber] AS partNumber\n" +
    //        "    ,a.[Inventory_TrackingType] AS trackingType\n" +
    //        "    ,a.[Inventory_MaterialTraceId] AS materialTraceId\n" +
    //        "    ,a.[Inventory_Quantity] AS quantity\n" +
    //        "    ,a.[Inventory_LocationId] AS locationId\n" +
    //        "    ,a.[Inventory_ParentLocationId] AS parentLocationId\n" +
    //        "    ,a.[Inventory_LastLocationId] AS lastLocationId\n" +
    //        "    ,a.[Inventory_MaterialControl] AS materialControl\n" +
    //        "    ,a.[Inventory_MaterialIdentifier] AS materialIdentifier\n" +
    //        "    ,a.[Inventory_Status] AS status\n" +
    //        "    ,a.[Inventory_ReservationReference] AS reservationReference\n" +
    //        "    ,a.[Inventory_ExpirationDate] AS expirationDate\n" +
    //        "    ,a.[Inventory_ReceivedDate] AS receivedDate\n" +
    //        "    ,a.[Inventory_UOMId] AS uomId\n" +
    //        "    ,a.[Inventory_UOMName] AS uomName\n" +
    //        "    ,a.[Inventory_UpdatedDate] AS updatedDate\n" +
    //        "    ,a.[Inventory_UpdatedBy] AS updatedBy\n" +
    //        "    ,a.[Inventory_LabelingStatus] AS labelingStatus\n" +
    //        "    ,a.[Inventory_Printer] AS printer\n" +
    //        "    ,a.[Inventory_SplicedMaterialIdentifier] AS splicedMaterialIdentifier\n" +
    //        "    ,a.[Inventory_SplicedInventoryId] AS splicedInventoryId\n" +
    //        "    ,a.[Inventory_CarrierId] AS carrierId\n" +
    //        "    ,a.[Inventory_CarrierNumber] AS carrierNumber\n" +
    //        "    ,a.[Inventory_ReservedQuantity] AS reservedQuantity\n" +
    //        "    ,a.[Inventory_CalculatedStatus] AS calculatedStatus\n" +
    //        "    ,a.[Inventory_InitialQuantity] AS initialQuantity\n" +
    //        "    ,a.[Inventory_AvailableQuantity] AS availableQuantity\n" +
    //        "    ,a.[Inventory_ConsumedQuantity] AS consumedQuantity\n" +
    //        "    ,a.[Inventory_ScrappedQuantity] AS scrappedQuantity\n" +
    //        "    ,a.[Inventory_ParentInventory_Id] AS parentInventoryId\n" +
    //        "    ,a.[Inventory_ParentMaterialIdentifier] AS parentMaterialIdentifier\n" +
    //        "    ,a.[Inventroy_MaterialName] AS materialName\n" +
    //        "    ,a.[Inventory_PU_Location] AS puLocation\n" +
    //        "    ,a.[Inventory_LifetimeCount] AS lifetimeCount\n" +
    //        "    ,a.[Inventory_Bulk_Barcode] AS bulkBarcode\n" +
    //        "    ,a.[Inventory_Is_Bulk] AS isBulk\n" +
    //        "    ,a.[Inventory_ManufacturingDate] AS manufacturingDate\n" +
    //        "    ,a.[Inventory_PartClass] AS partClass\n" +
    //        "    ,a.[Inventory_MaterialType] AS materialType\n" +
    //        "    ,a.[Inventory_CheckinDate] AS checkinDate\n" +
    //        "    ,a.[Inventory_UsageCount] AS usageCount\n" +
    //        "    ,a.[Inventory_PartAlternateNumbers_Id] AS partAlternateNumbersId\n" +
    //        "    ,a.[Inventory_ReelNumber] AS reelNumber\n" +
    //        "    ,a.[Inventory_MainReelId] AS mainReelId\n" +
    //        "    ,a.[Inventory_ReasonCode] AS reasonCode\n" +
    //        "    ,a.[Inventory_LastCarrierNumber] AS lastCarrierNumber\n" +
    //        "    ,b.Location_FullName AS locationName\n" +
    //        "    ,d1.InventoryMaterialTraceDetail_MaterialTraceDataValue as userData4 " +
    //        "    ,d2.InventoryMaterialTraceDetail_MaterialTraceDataValue  as lotNumber " +
    //        "    ,d3.InventoryMaterialTraceDetail_MaterialTraceDataValue  as userData5 " +
    //        "FROM Inventory a\n" +
    //        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
    //        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
    //        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
    //        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
    //        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
    //        "\n" +
    //        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
    //        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
    //        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot' " +
    //        "LEFT JOIN InventoryMaterialTraceDetail d3 \n" +
    //        "  ON d3.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
    //        " AND d3.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 5' \n" +
    //        "WHERE a.Inventory_MaterialIdentifier like ?1 " +
    //        "AND a.Inventory_Status in(3,6,19) and Inventory_Quantity >0 " +
    //        "AND a.Inventory_Status like ?2 " +
    //        "AND a.Inventory_PartNumber like ?3 " +
    //        "AND ( ?4 IS NULL OR a.Inventory_Quantity = ?4 ) " +
    //        "AND ( ?5 IS NULL OR a.Inventory_AvailableQuantity = ?5 ) " +
    //        "AND d2.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?6 " +
    //        "AND d1.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?7 " +
    //        "AND d3.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?8 " +
    //        "AND b.Location_FullName like ?9 " +
    //        "AND ( ?10 IS NULL OR a.Inventory_ExpirationDate like ?10) " +
    //        "AND (\n" +
    //        "  ?11 IS NULL OR \n" +
    //        "  a.Inventory_UpdatedDate >= CAST(?11 AS BIGINT) AND \n" +
    //        "  a.Inventory_UpdatedDate < CAST(?11 AS BIGINT) + 86400\n" +
    //        ")" +
    //        "ORDER BY a.Inventory_UpdatedDate desc " +
    //        "OFFSET ?12 ROWS FETCH NEXT ?13 ROWS ONLY ;\n",
    //        nativeQuery = true
    //    )
    //    public List<InventoryResponse> getInventories(
    //        String materialIdentifier,
    //        String status,
    //        String partNumber,
    //        Integer quantity,
    //        Integer availableQuantity,
    //        String lotNumber,
    //        String userData4,
    //        String userData5,
    //        String locationName,
    //        String expirationDate,
    //        String updatedDate,
    //        Integer pageNumber,
    //        Integer itemPerPage
    //    );

    //
    @Query(
        value = "SELECT  \n" +
        "     a.[Inventory_Id] AS inventoryId\n" +
        "    ,a.[Inventory_PartId] AS partId\n" +
        "    ,a.[Inventory_PartNumber] AS partNumber\n" +
        "    ,a.[Inventory_TrackingType] AS trackingType\n" +
        "    ,a.[Inventory_MaterialTraceId] AS materialTraceId\n" +
        "    ,a.[Inventory_Quantity] AS quantity\n" +
        "    ,a.[Inventory_LocationId] AS locationId\n" +
        "    ,a.[Inventory_ParentLocationId] AS parentLocationId\n" +
        "    ,a.[Inventory_LastLocationId] AS lastLocationId\n" +
        "    ,a.[Inventory_MaterialControl] AS materialControl\n" +
        "    ,a.[Inventory_MaterialIdentifier] AS materialIdentifier\n" +
        "    ,a.[Inventory_Status] AS status\n" +
        "    ,a.[Inventory_ReservationReference] AS reservationReference\n" +
        "    ,a.[Inventory_ExpirationDate] AS expirationDate\n" +
        "    ,a.[Inventory_ReceivedDate] AS receivedDate\n" +
        "    ,a.[Inventory_UOMId] AS uomId\n" +
        "    ,a.[Inventory_UOMName] AS uomName\n" +
        "    ,a.[Inventory_UpdatedDate] AS updatedDate\n" +
        "    ,a.[Inventory_UpdatedBy] AS updatedBy\n" +
        "    ,a.[Inventory_LabelingStatus] AS labelingStatus\n" +
        "    ,a.[Inventory_Printer] AS printer\n" +
        "    ,a.[Inventory_SplicedMaterialIdentifier] AS splicedMaterialIdentifier\n" +
        "    ,a.[Inventory_SplicedInventoryId] AS splicedInventoryId\n" +
        "    ,a.[Inventory_CarrierId] AS carrierId\n" +
        "    ,a.[Inventory_CarrierNumber] AS carrierNumber\n" +
        "    ,a.[Inventory_ReservedQuantity] AS reservedQuantity\n" +
        "    ,a.[Inventory_CalculatedStatus] AS calculatedStatus\n" +
        "    ,a.[Inventory_InitialQuantity] AS initialQuantity\n" +
        "    ,a.[Inventory_AvailableQuantity] AS availableQuantity\n" +
        "    ,a.[Inventory_ConsumedQuantity] AS consumedQuantity\n" +
        "    ,a.[Inventory_ScrappedQuantity] AS scrappedQuantity\n" +
        "    ,a.[Inventory_ParentInventory_Id] AS parentInventoryId\n" +
        "    ,a.[Inventory_ParentMaterialIdentifier] AS parentMaterialIdentifier\n" +
        "    ,a.[Inventroy_MaterialName] AS materialName\n" +
        "    ,a.[Inventory_PU_Location] AS puLocation\n" +
        "    ,a.[Inventory_LifetimeCount] AS lifetimeCount\n" +
        "    ,a.[Inventory_Bulk_Barcode] AS bulkBarcode\n" +
        "    ,a.[Inventory_Is_Bulk] AS isBulk\n" +
        "    ,a.[Inventory_ManufacturingDate] AS manufacturingDate\n" +
        "    ,a.[Inventory_PartClass] AS partClass\n" +
        "    ,a.[Inventory_MaterialType] AS materialType\n" +
        "    ,a.[Inventory_CheckinDate] AS checkinDate\n" +
        "    ,a.[Inventory_UsageCount] AS usageCount\n" +
        "    ,a.[Inventory_PartAlternateNumbers_Id] AS partAlternateNumbersId\n" +
        "    ,a.[Inventory_ReelNumber] AS reelNumber\n" +
        "    ,a.[Inventory_MainReelId] AS mainReelId\n" +
        "    ,a.[Inventory_ReasonCode] AS reasonCode\n" +
        "    ,a.[Inventory_LastCarrierNumber] AS lastCarrierNumber\n" +
        "    ,b.Location_FullName AS locationName\n" +
        "    ,d1.InventoryMaterialTraceDetail_MaterialTraceDataValue as userData4\n" +
        "    ,d2.InventoryMaterialTraceDetail_MaterialTraceDataValue as lotNumber\n" +
        "    ,d3.InventoryMaterialTraceDetail_MaterialTraceDataValue as userData5\n" +
        "    ,d4.InventoryMaterialTraceDetail_MaterialTraceDataValue as rankAp\n" +
        "    ,d5.InventoryMaterialTraceDetail_MaterialTraceDataValue as rankQuang\n" +
        "    ,d6.InventoryMaterialTraceDetail_MaterialTraceDataValue as rankMau\n" +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d1 ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d3 ON d3.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id AND d3.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 5'\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d4 ON d4.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id AND d4.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Rank ap'\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d5 ON d5.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id AND d5.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Rank quang'\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d6 ON d6.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id AND d6.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Rank mau'\n" +
        "WHERE a.Inventory_MaterialIdentifier LIKE ?1\n" +
        "AND a.Inventory_Status IN (3,6,19) AND a.Inventory_Quantity > 0\n" +
        "AND a.Inventory_Status LIKE ?2\n" +
        "AND a.Inventory_PartNumber LIKE ?3\n" +
        "AND (?4 IS NULL OR a.Inventory_Quantity = ?4)\n" +
        "AND (?5 IS NULL OR a.Inventory_AvailableQuantity = ?5)\n" +
        "AND d2.InventoryMaterialTraceDetail_MaterialTraceDataValue LIKE ?6\n" +
        "AND d1.InventoryMaterialTraceDetail_MaterialTraceDataValue LIKE ?7\n" +
        "AND d3.InventoryMaterialTraceDetail_MaterialTraceDataValue LIKE ?8\n" +
        "AND b.Location_FullName LIKE ?9\n" +
        "AND (?10 IS NULL OR a.Inventory_ExpirationDate LIKE ?10)\n" +
        "AND (?11 IS NULL OR a.Inventory_UpdatedDate >= CAST(?11 AS BIGINT) AND a.Inventory_UpdatedDate < CAST(?11 AS BIGINT) + 86400)" +
        "ORDER BY a.Inventory_UpdatedDate DESC " +
        "OFFSET ?12 ROWS FETCH NEXT ?13 ROWS ONLY",
        countQuery = "SELECT COUNT(*) FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d1 ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d3 ON d3.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id AND d3.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 5'\n" +
        "WHERE a.Inventory_MaterialIdentifier LIKE ?1\n" +
        "AND a.Inventory_Status IN (3,6,19) AND a.Inventory_Quantity > 0\n" +
        "AND a.Inventory_Status LIKE ?2\n" +
        "AND a.Inventory_PartNumber LIKE ?3\n" +
        "AND (?4 IS NULL OR a.Inventory_Quantity = ?4)\n" +
        "AND (?5 IS NULL OR a.Inventory_AvailableQuantity = ?5)\n" +
        "AND d2.InventoryMaterialTraceDetail_MaterialTraceDataValue LIKE ?6\n" +
        "AND d1.InventoryMaterialTraceDetail_MaterialTraceDataValue LIKE ?7\n" +
        "AND d3.InventoryMaterialTraceDetail_MaterialTraceDataValue LIKE ?8\n" +
        "AND b.Location_FullName LIKE ?9\n" +
        "AND (?10 IS NULL OR a.Inventory_ExpirationDate LIKE ?10)\n" +
        "AND (?11 IS NULL OR a.Inventory_UpdatedDate >= CAST(?11 AS BIGINT) AND a.Inventory_UpdatedDate < CAST(?11 AS BIGINT) + 86400)",
        nativeQuery = true
    )
    List<InventoryResponse> getInventories(
        String materialIdentifier,
        String status,
        String partNumber,
        Integer quantity,
        Integer availableQuantity,
        String lotNumber,
        String userData4,
        String userData5,
        String locationName,
        String expirationDate,
        String updatedDate,
        Integer offset,
        Integer pageSize
    );

    @Query(
        value = "SELECT  count(*) " +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot' " +
        "  LEFT JOIN InventoryMaterialTraceDetail d3 \n" +
        "  ON d3.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d3.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 5'\n" +
        "\n" +
        "WHERE a.Inventory_MaterialIdentifier like ?1 " +
        "AND a.Inventory_Status in(3,6,19) and Inventory_Quantity >0 " +
        "AND a.Inventory_Status like ?2 " +
        "AND a.Inventory_PartNumber like ?3 " +
        "AND ( ?4 IS NULL OR a.Inventory_Quantity = ?4 ) " +
        "AND ( ?5 IS NULL OR a.Inventory_AvailableQuantity = ?5 ) " +
        "AND d2.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?6 " +
        "AND d1.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?7 " +
        "AND d3.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?8 " +
        "AND b.Location_FullName like ?9 " +
        "AND ( ?10 IS NULL OR a.Inventory_ExpirationDate like ?10 ) " +
        "AND ( ?11 IS NULL OR a.Inventory_UpdatedDate >= CAST(?11 AS BIGINT) AND a.Inventory_UpdatedDate < CAST(?11 AS BIGINT) + 86400 )",
        nativeQuery = true
    )
    public Integer getTotalInventories(
        String materialIdentifier,
        String status,
        String partNumber,
        Integer quantity,
        Integer availableQuantity,
        String lotNumber,
        String userData4,
        String userData5,
        String locationName,
        String expirationDate,
        String updatedDate
    );

    @Query(
        value = "SELECT  \n" +
        "     a.[Inventory_Id] AS inventoryId\n" +
        "    ,a.[Inventory_PartId] AS partId\n" +
        "    ,a.[Inventory_PartNumber] AS partNumber\n" +
        "    ,a.[Inventory_TrackingType] AS trackingType\n" +
        "    ,a.[Inventory_MaterialTraceId] AS materialTraceId\n" +
        "    ,a.[Inventory_Quantity] AS quantity\n" +
        "    ,a.[Inventory_LocationId] AS locationId\n" +
        "    ,a.[Inventory_ParentLocationId] AS parentLocationId\n" +
        "    ,a.[Inventory_LastLocationId] AS lastLocationId\n" +
        "    ,a.[Inventory_MaterialControl] AS materialControl\n" +
        "    ,a.[Inventory_MaterialIdentifier] AS materialIdentifier\n" +
        "    ,a.[Inventory_Status] AS status\n" +
        "    ,a.[Inventory_ReservationReference] AS reservationReference\n" +
        "    ,a.[Inventory_ExpirationDate] AS expirationDate\n" +
        "    ,a.[Inventory_ReceivedDate] AS receivedDate\n" +
        "    ,a.[Inventory_UOMId] AS uomId\n" +
        "    ,a.[Inventory_UOMName] AS uomName\n" +
        "    ,a.[Inventory_UpdatedDate] AS updatedDate\n" +
        "    ,a.[Inventory_UpdatedBy] AS updatedBy\n" +
        "    ,a.[Inventory_LabelingStatus] AS labelingStatus\n" +
        "    ,a.[Inventory_Printer] AS printer\n" +
        "    ,a.[Inventory_SplicedMaterialIdentifier] AS splicedMaterialIdentifier\n" +
        "    ,a.[Inventory_SplicedInventoryId] AS splicedInventoryId\n" +
        "    ,a.[Inventory_CarrierId] AS carrierId\n" +
        "    ,a.[Inventory_CarrierNumber] AS carrierNumber\n" +
        "    ,a.[Inventory_ReservedQuantity] AS reservedQuantity\n" +
        "    ,a.[Inventory_CalculatedStatus] AS calculatedStatus\n" +
        "    ,a.[Inventory_InitialQuantity] AS initialQuantity\n" +
        "    ,a.[Inventory_AvailableQuantity] AS availableQuantity\n" +
        "    ,a.[Inventory_ConsumedQuantity] AS consumedQuantity\n" +
        "    ,a.[Inventory_ScrappedQuantity] AS scrappedQuantity\n" +
        "    ,a.[Inventory_ParentInventory_Id] AS parentInventoryId\n" +
        "    ,a.[Inventory_ParentMaterialIdentifier] AS parentMaterialIdentifier\n" +
        "    ,a.[Inventroy_MaterialName] AS materialName\n" +
        "    ,a.[Inventory_PU_Location] AS puLocation\n" +
        "    ,a.[Inventory_LifetimeCount] AS lifetimeCount\n" +
        "    ,a.[Inventory_Bulk_Barcode] AS bulkBarcode\n" +
        "    ,a.[Inventory_Is_Bulk] AS isBulk\n" +
        "    ,a.[Inventory_ManufacturingDate] AS manufacturingDate\n" +
        "    ,a.[Inventory_PartClass] AS partClass\n" +
        "    ,a.[Inventory_MaterialType] AS materialType\n" +
        "    ,a.[Inventory_CheckinDate] AS checkinDate\n" +
        "    ,a.[Inventory_UsageCount] AS usageCount\n" +
        "    ,a.[Inventory_PartAlternateNumbers_Id] AS partAlternateNumbersId\n" +
        "    ,a.[Inventory_ReelNumber] AS reelNumber\n" +
        "    ,a.[Inventory_MainReelId] AS mainReelId\n" +
        "    ,a.[Inventory_ReasonCode] AS reasonCode\n" +
        "    ,a.[Inventory_LastCarrierNumber] AS lastCarrierNumber\n" +
        "    ,b.Location_FullName AS locationName\n" +
        "    ,d1.InventoryMaterialTraceDetail_MaterialTraceDataValue AS userData4\n" +
        "    ,d2.InventoryMaterialTraceDetail_MaterialTraceDataValue AS lotNumber\n" +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d1 ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id\n" +
        "    AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id\n" +
        "    AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'\n" +
        "WHERE a.Inventory_MaterialIdentifier = ?1\n" +
        "AND a.Inventory_Status IN (3, 6, 19)\n",
        nativeQuery = true
    )
    InventoryResponse findResponseByMaterialIdentifier(
        String materialIdentifier
    );

    @Query(
        value = "SELECT  \n" +
        "     a.[Inventory_Id] AS inventoryId\n" +
        "    ,a.[Inventory_PartId] AS partId\n" +
        "    ,a.[Inventory_PartNumber] AS partNumber\n" +
        "    ,a.[Inventory_TrackingType] AS trackingType\n" +
        "    ,a.[Inventory_MaterialTraceId] AS materialTraceId\n" +
        "    ,a.[Inventory_Quantity] AS quantity\n" +
        "    ,a.[Inventory_LocationId] AS locationId\n" +
        "    ,a.[Inventory_ParentLocationId] AS parentLocationId\n" +
        "    ,a.[Inventory_LastLocationId] AS lastLocationId\n" +
        "    ,a.[Inventory_MaterialControl] AS materialControl\n" +
        "    ,a.[Inventory_MaterialIdentifier] AS materialIdentifier\n" +
        "    ,a.[Inventory_Status] AS status\n" +
        "    ,a.[Inventory_ReservationReference] AS reservationReference\n" +
        "    ,a.[Inventory_ExpirationDate] AS expirationDate\n" +
        "    ,a.[Inventory_ReceivedDate] AS receivedDate\n" +
        "    ,a.[Inventory_UOMId] AS uomId\n" +
        "    ,a.[Inventory_UOMName] AS uomName\n" +
        "    ,a.[Inventory_UpdatedDate] AS updatedDate\n" +
        "    ,a.[Inventory_UpdatedBy] AS updatedBy\n" +
        "    ,a.[Inventory_LabelingStatus] AS labelingStatus\n" +
        "    ,a.[Inventory_Printer] AS printer\n" +
        "    ,a.[Inventory_SplicedMaterialIdentifier] AS splicedMaterialIdentifier\n" +
        "    ,a.[Inventory_SplicedInventoryId] AS splicedInventoryId\n" +
        "    ,a.[Inventory_CarrierId] AS carrierId\n" +
        "    ,a.[Inventory_CarrierNumber] AS carrierNumber\n" +
        "    ,a.[Inventory_ReservedQuantity] AS reservedQuantity\n" +
        "    ,a.[Inventory_CalculatedStatus] AS calculatedStatus\n" +
        "    ,a.[Inventory_InitialQuantity] AS initialQuantity\n" +
        "    ,a.[Inventory_AvailableQuantity] AS availableQuantity\n" +
        "    ,a.[Inventory_ConsumedQuantity] AS consumedQuantity\n" +
        "    ,a.[Inventory_ScrappedQuantity] AS scrappedQuantity\n" +
        "    ,a.[Inventory_ParentInventory_Id] AS parentInventoryId\n" +
        "    ,a.[Inventory_ParentMaterialIdentifier] AS parentMaterialIdentifier\n" +
        "    ,a.[Inventroy_MaterialName] AS materialName\n" +
        "    ,a.[Inventory_PU_Location] AS puLocation\n" +
        "    ,a.[Inventory_LifetimeCount] AS lifetimeCount\n" +
        "    ,a.[Inventory_Bulk_Barcode] AS bulkBarcode\n" +
        "    ,a.[Inventory_Is_Bulk] AS isBulk\n" +
        "    ,a.[Inventory_ManufacturingDate] AS manufacturingDate\n" +
        "    ,a.[Inventory_PartClass] AS partClass\n" +
        "    ,a.[Inventory_MaterialType] AS materialType\n" +
        "    ,a.[Inventory_CheckinDate] AS checkinDate\n" +
        "    ,a.[Inventory_UsageCount] AS usageCount\n" +
        "    ,a.[Inventory_PartAlternateNumbers_Id] AS partAlternateNumbersId\n" +
        "    ,a.[Inventory_ReelNumber] AS reelNumber\n" +
        "    ,a.[Inventory_MainReelId] AS mainReelId\n" +
        "    ,a.[Inventory_ReasonCode] AS reasonCode\n" +
        "    ,a.[Inventory_LastCarrierNumber] AS lastCarrierNumber\n" +
        "    ,b.Location_FullName AS locationName\n" +
        "    ,d1.InventoryMaterialTraceDetail_MaterialTraceDataValue AS userData4\n" +
        "    ,d2.InventoryMaterialTraceDetail_MaterialTraceDataValue AS lotNumber\n" +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'\n" +
        "WHERE b.Location_FullName = :locationName\n" +
        "  AND a.Inventory_Status IN (3, 6, 19) AND a.Inventory_Quantity > 0\n" +
        "ORDER BY a.Inventory_UpdatedDate DESC",
        nativeQuery = true
    )
    List<InventoryResponse> getInventoriesByLocation(
        @Param("locationName") String locationName
    );
}
