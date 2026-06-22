package com.mycompany.myapp.repository.partner4;

import com.mycompany.myapp.domain.partner4.SapOitm;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SapOitm entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SapOitmRepository extends JpaRepository<SapOitm, Long> {
    List<SapOitm> findByItemCode(String itemCode);

    @Query(
        value = "SELECT ItemCode, U_IGroupName FROM SAP_OITM WHERE ItemCode IN (:itemCodes)",
        nativeQuery = true
    )
    List<Object[]> findGroupNamesByItemCodesIn(
        @Param("itemCodes") List<String> itemCodes
    );

    @Query(
        value = "SELECT ItemCode, U_IGroupName, ItemName FROM SAP_OITM WHERE ItemCode IS NOT NULL AND LTRIM(RTRIM(ItemCode)) <> ''",
        nativeQuery = true
    )
    List<Object[]> findAllItemGroupNames();
}
