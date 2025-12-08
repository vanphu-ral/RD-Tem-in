package com.mycompany.wh.repository;

import com.mycompany.wh.domain.ItemData;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ItemData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ItemDataRepository extends JpaRepository<ItemData, Long> {
    Optional<ItemData> findByItemCode(String itemCode);
}
