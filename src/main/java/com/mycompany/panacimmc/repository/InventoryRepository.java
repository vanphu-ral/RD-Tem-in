package com.mycompany.panacimmc.repository;

import com.mycompany.panacimmc.domain.Inventory;
import com.mycompany.panacimmc.domain.InventoryResponse;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    public Inventory findAllByMaterialIdentifier(String materialIdentifier);

    @Query(
        value = "SELECT  \n" +
        "    a.[Inventory_PartNumber] AS partNumber\n" +
        "    ,sum(a.[Inventory_Quantity]) AS quantity\n" +
        "    ,sum(a.[Inventory_AvailableQuantity]) AS availableQuantity \n" +
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
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_AvailableQuantity >0 " +
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
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_AvailableQuantity >0 " +
        "AND a.Inventory_PartNumber like ?1 " +
        "GROUP BY a.[Inventory_PartNumber] ) AS grouped_parts ;",
        nativeQuery = true
    )
    public Integer getTotalDataGroupByPartNumber(String partNumber);

    @Query(
        value = "SELECT  \n" +
        "    a.Inventory_PartNumber AS partNumber\n" +
        "    ,sum(a.Inventory_Quantity) AS quantity\n" +
        "    ,sum(a.Inventory_AvailableQuantity) AS availableQuantity \n" +
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
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_AvailableQuantity >0 " +
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
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_AvailableQuantity >0 " +
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
        "    ,b.Location_FullName AS locationName\n" +
        "    ,sum(a.[Inventory_Quantity]) AS quantity\n" +
        "    ,sum(a.[Inventory_AvailableQuantity]) AS availableQuantity \n" +
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
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_AvailableQuantity >0 " +
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
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_AvailableQuantity >0 " +
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
        "    ,sum(a.[Inventory_AvailableQuantity]) AS availableQuantity \n" +
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
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_AvailableQuantity >0 " +
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
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_AvailableQuantity >0 " +
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
        value = "SELECT  \n" +
        "    a.[Inventory_PartNumber] AS partNumber\n" +
        "    ,a.[Inventory_Quantity] AS quantity\n" +
        "    ,a.[Inventory_AvailableQuantity] AS availableQuantity\n" +
        "    ,a.[Inventory_MaterialIdentifier] AS materialIdentifier\n" +
        "    ,a.[Inventory_ExpirationDate] AS expirationDate\n" +
        "    ,a.[Inventory_ReceivedDate] AS receivedDate\n" +
        "    ,d1.InventoryMaterialTraceDetail_MaterialTraceDataValue as userData4 " +
        "    ,d2.InventoryMaterialTraceDetail_MaterialTraceDataValue  as lotNumber " +
        "    ,a.[Inventory_Status] AS status\n" +
        "    ,b.Location_FullName AS locationName\n" +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_AvailableQuantity >0 " +
        "AND a.Inventory_PartNumber like ?1 " +
        "AND d2.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?2 " +
        "AND d1.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?3 " +
        "AND b.Location_FullName like ?4 " +
        "ORDER BY a.Inventory_PartNumber desc " +
        "OFFSET ?5 ROWS FETCH NEXT ?6 ROWS ONLY ;\n",
        nativeQuery = true
    )
    public List<InventoryResponse> getDataDetail(
        String partNumber,
        String lotNumber,
        String userData4,
        String locationName,
        Integer pageNumber,
        Integer itemPerPage
    );

    @Query(
        value = "SELECT count(*) \n" +
        "FROM Inventory a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where Inventory_Status in(3,6,19) and Inventory_AvailableQuantity >0 " +
        "AND a.Inventory_PartNumber like ?1 " +
        "AND d2.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?2 " +
        "AND d1.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?3 " +
        "AND b.Location_FullName like ?4 ;\n",
        nativeQuery = true
    )
    public Integer getTotalDataDetail(
        String partNumber,
        String lotNumber,
        String userData4,
        String locationName
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
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot'  where a.Inventory_Status in(3,6,19) and Inventory_AvailableQuantity >0 ",
        nativeQuery = true
    )
    public List<InventoryResponse> getDataNew();

    @Query(
        value = "UPDATE Inventory SET " +
        "Inventory_Quantity= ?1 ," +
        "Inventory_Status=3, " +
        "Inventory_UpdatedBy= ?2 ," +
        "Inventory_ExpirationDate= ?3 , " +
        "Inventory_UpdatedDate= ?4 " +
        "where Inventory_MaterialIdentifier= ?5 ;\n",
        nativeQuery = true
    )
    @Modifying
    public void extendIventory(
        Integer quantity,
        String updateBy,
        String expirationDate,
        String date,
        String materialIdentifier
    );

    @Query(
        value = "UPDATE Inventory SET " +
        "Inventory_Quantity= ?1 ," +
        "Inventory_Status=3, " +
        "Inventory_UpdatedBy= ?2 ," +
        "Inventory_UpdatedDate= ?3 " +
        "where Inventory_MaterialIdentifier= ?4 ;\n",
        nativeQuery = true
    )
    @Modifying
    @Transactional
    public void updateIventory(
        Integer quantity,
        String updateBy,
        String date,
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
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot' " +
        "WHERE a.Inventory_MaterialIdentifier like ?1 " +
        "AND a.Inventory_Status in(3,6,19) and Inventory_AvailableQuantity >0 " +
        "AND a.Inventory_Status like ?2 " +
        "AND a.Inventory_PartNumber like ?3 " +
        "AND ( ?4 IS NULL OR a.Inventory_Quantity = ?4 ) " +
        "AND ( ?5 IS NULL OR a.Inventory_AvailableQuantity = ?5 ) " +
        "AND d2.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?6 " +
        "AND d1.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?7 " +
        "AND b.Location_FullName like ?8 " +
        "AND ( ?9 IS NULL OR a.Inventory_ExpirationDate like ?9) " +
        "ORDER BY a.Inventory_UpdatedDate desc " +
        "OFFSET ?10 ROWS FETCH NEXT ?11 ROWS ONLY ;\n",
        nativeQuery = true
    )
    public List<InventoryResponse> getInventories(
        String materialIdentifier,
        String status,
        String partNumber,
        Integer quantity,
        Integer availableQuantity,
        String lotNumber,
        String userData4,
        String locationName,
        String expirationDate,
        Integer pageNumber,
        Integer itemPerPage
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
        "WHERE a.Inventory_MaterialIdentifier like ?1 " +
        "AND a.Inventory_Status in(3,6,19) and Inventory_AvailableQuantity >0 " +
        "AND a.Inventory_Status like ?2 " +
        "AND a.Inventory_PartNumber like ?3 " +
        "AND ( ?4 IS NULL OR a.Inventory_Quantity = ?4 ) " +
        "AND ( ?5 IS NULL OR a.Inventory_AvailableQuantity = ?5 ) " +
        "AND d2.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?6 " +
        "AND d1.InventoryMaterialTraceDetail_MaterialTraceDataValue like ?7 " +
        "AND b.Location_FullName like ?8 " +
        "AND ( ?9 IS NULL OR a.Inventory_ExpirationDate like ?9) ;",
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
        String locationName,
        String expirationDate
    );
}
