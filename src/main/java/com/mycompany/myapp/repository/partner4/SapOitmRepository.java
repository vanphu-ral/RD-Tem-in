package com.mycompany.myapp.repository.partner4;

import com.mycompany.myapp.domain.partner4.SapOitm;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SapOitm entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SapOitmRepository extends JpaRepository<SapOitm, Long> {
    List<SapOitm> findByItemCode(String itemCode);
}
