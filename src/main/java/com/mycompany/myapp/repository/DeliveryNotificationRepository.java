package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.DeliveryNotification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    /** Đơn (chưa xoá) theo mã thông báo giao hàng — id nhỏ nhất trước */
    List<
        DeliveryNotification
    > findByDeliveryNotificationCodeAndDeletedAtIsNullOrderByIdAsc(
        String deliveryNotificationCode
    );

    /** Các đơn (chưa xoá) của 1 phiếu lịch giao hàng */
    List<DeliveryNotification> findByDeliveryIdAndDeletedAtIsNull(
        UUID deliveryId
    );
}
