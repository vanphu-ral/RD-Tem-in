package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Delivery;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Delivery entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DeliveryRepository
    extends JpaRepository<Delivery, UUID>, JpaSpecificationExecutor<Delivery> {
    Optional<Delivery> findByNoticeNo(String noticeNo);

    @Query(
        "select d from Delivery d left join fetch d.detailDeliveries where d.deliveryId = :id"
    )
    Optional<Delivery> findWithDetailDeliveriesById(@Param("id") UUID id);
}
