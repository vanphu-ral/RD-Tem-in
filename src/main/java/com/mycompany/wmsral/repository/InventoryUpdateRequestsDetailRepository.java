package com.mycompany.wmsral.repository;

import com.mycompany.wmsral.domain.InventoryUpdateRequestsDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryUpdateRequestsDetailRepository extends JpaRepository<InventoryUpdateRequestsDetail, Long> {
    public List<InventoryUpdateRequestsDetail> findAllByRequestId(Long id);
    // Define custom query methods if needed
    // For example, to find by a specific field:
    // List<InventoryUpdateRequestsDetail> findBySomeField(String someField);

    // You can also define methods for custom update operations if required
    // @Modifying
    // @Query("UPDATE InventoryUpdateRequestsDetail i SET i.someField = ?1 WHERE i.id = ?2")
    // void updateSomeField(String someValue, Long id);
}
