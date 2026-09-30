package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.VendorLabelInfo;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VendorLabelInfo entity.
 */
@SuppressWarnings("unused")
@Repository
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
}
