package com.mycompany.myapp.repository.partner4;

import com.mycompany.myapp.domain.partner4.ItemData;
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
