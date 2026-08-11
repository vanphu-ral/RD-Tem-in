package com.mycompany.myapp.repository.partner6;

import com.mycompany.myapp.domain.SapPor1;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SapPor1Repository extends JpaRepository<SapPor1, Long> {
    @Query("SELECT s FROM SapPor1 s WHERE s.docEntry = :docEntry")
    List<SapPor1> findByDocEntry(@Param("docEntry") String docEntry);
}
