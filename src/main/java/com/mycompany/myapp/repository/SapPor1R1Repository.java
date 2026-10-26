package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SapPor1R1;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SapPor1R1 entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SapPor1R1Repository extends JpaRepository<SapPor1R1, Long> {
    Optional<SapPor1R1> findOneByLineId(UUID lineId);
    void deleteByDeliveryNotification_DeliveryNotificationCode(
        String deliveryNotificationCode
    );
    List<SapPor1R1> findByDeliveryNotificationIdOrderByIdAsc(
        Long deliveryNotificationId
    );

    List<SapPor1R1> findByDeliveryNotificationIdInOrderByIdAsc(
        Collection<Long> deliveryNotificationIds
    );

    /** Dòng PO mang deliveryId (GUID) của phiếu lịch giao hàng */
    List<SapPor1R1> findByDeliveryIdOrderByIdAsc(UUID deliveryId);
}
