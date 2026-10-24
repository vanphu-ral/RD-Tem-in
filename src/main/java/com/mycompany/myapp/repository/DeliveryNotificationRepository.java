package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.DeliveryNotification;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DeliveryNotification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DeliveryNotificationRepository
    extends
        JpaRepository<DeliveryNotification, Long>,
        JpaSpecificationExecutor<DeliveryNotification> {
    Optional<DeliveryNotification> findOneByDeliveryNotificationCode(
        String deliveryNotificationCode
    );
    void deleteByDeliveryNotificationCode(String deliveryNotificationCode);
}
