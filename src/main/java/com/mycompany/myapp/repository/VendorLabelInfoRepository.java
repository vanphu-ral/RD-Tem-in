package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.VendorLabelInfo;
import com.mycompany.myapp.service.dto.VendorLabelInfoUserData5SumDTO;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VendorLabelInfo entity.
 */
@SuppressWarnings("unused")
public interface VendorLabelInfoRepository
    extends JpaRepository<VendorLabelInfo, Long> {
    List<VendorLabelInfo> findBySapPor1IdInOrderByReelIdAsc(
        Collection<Long> sapPor1Ids
    );

    List<VendorLabelInfo> findByDeliveryNotificationIdOrderByReelIdAsc(
        Long deliveryNotificationId
    );

    Optional<VendorLabelInfo> findByReelId(String reelId);

    boolean existsByReelId(String reelId);

    List<VendorLabelInfo> findByReelIdIn(Collection<String> reelIds);

    /**
     * Aggregate the {@code initial_quantity} per {@code sap_code} for records
     * whose {@code user_data_5} equals the given value.
     *
     * @param userData5 the value of {@code user_data_5} to filter on.
     * @return one {@link VendorLabelInfoUserData5SumDTO} per {@code sap_code}.
     */
    @Query(
        "select new com.mycompany.myapp.service.dto.VendorLabelInfoUserData5SumDTO(" +
        "v.sapCode, sum(coalesce(v.initialQuantity, 0))" +
        ") from VendorLabelInfo v" +
        " where v.userData5 = :userData5" +
        " group by v.sapCode" +
        " order by v.sapCode"
    )
    List<VendorLabelInfoUserData5SumDTO> sumInitialQuantityByUserData5(
        @Param("userData5") String userData5
    );
}
