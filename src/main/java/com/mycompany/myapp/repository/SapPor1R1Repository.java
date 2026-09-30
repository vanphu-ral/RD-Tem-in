package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SapPor1R1;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SapPor1R1 entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SapPor1R1Repository extends JpaRepository<SapPor1R1, Long> {
    List<SapPor1R1> findByDeliveryNotificationIdOrderByIdAsc(
        Long deliveryNotificationId
    );
}
