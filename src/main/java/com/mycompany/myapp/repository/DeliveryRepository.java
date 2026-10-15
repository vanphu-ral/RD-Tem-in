package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Delivery;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Delivery entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DeliveryRepository
    extends JpaRepository<Delivery, UUID>, JpaSpecificationExecutor<Delivery> {
    Optional<Delivery> findByNoticeNo(String noticeNo);
}
