package com.mycompany.panacimmc.repository;

import com.mycompany.panacimmc.domain.Inventory;
import com.mycompany.panacimmc.domain.InventoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    public Inventory findAllByMaterialIdentifier(String materialIdentifier);
    @Query(value = "UPDATE Inventory SET " +
        "Inventory_Quantity= ?1 ," +
        "Inventory_Status=3, " +
        "Inventory_UpdatedBy= ?2 ," +
        "Inventory_ExpirationDate= ?3 , " +
        "Inventory_UpdatedDate= ?4 " +
        "where Inventory_MaterialIdentifier= ?5 ;\n",nativeQuery = true)
    @Modifying
    public void extendIventory(Integer quantity, String updateBy, String expirationDate,String date ,String materialIdentifier);
    @Query(value = "UPDATE Inventory SET " +
        "Inventory_Quantity= ?1 ," +
        "Inventory_Status=3, " +
        "Inventory_UpdatedBy= ?2 ," +
        "Inventory_UpdatedDate= ?3 " +
        "where Inventory_MaterialIdentifier= ?4 ;\n",nativeQuery = true)
    @Modifying
    @Transactional
    public void updateIventory(Integer quantity, String updateBy,String date ,String materialIdentifier);
    @Query(value = "SELECT \n" +
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
        "    ,b.Location_Name AS locationName\n" +
        "    ,d1.InventoryMaterialTraceDetail_MaterialTraceDataValue as userData4 " +
        "    ,d2.InventoryMaterialTraceDetail_MaterialTraceDataValue  as lotNumber " +
        "FROM [panacim_test].[dbo].[Inventory] a\n" +
        "INNER JOIN Location b ON a.Inventory_LocationId = b.Location_Id\n" +
        "INNER JOIN InventoryMaterialTrace c ON c.InventoryMaterialTrace_Id = a.Inventory_MaterialTraceId " +
        "  LEFT JOIN InventoryMaterialTraceDetail d1 \n" +
        "  ON d1.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d1.InventoryMaterialTraceDetail_MaterialTraceDataName = 'User data 4'\n" +
        "\n" +
        "LEFT JOIN InventoryMaterialTraceDetail d2 \n" +
        "  ON d2.InventoryMaterialTraceDetail_MaterialTraceId = c.InventoryMaterialTrace_Id \n" +
        " AND d2.InventoryMaterialTraceDetail_MaterialTraceDataName = 'Lot' ;\n",nativeQuery = true)
    public List<InventoryResponse> getInventories();
}
