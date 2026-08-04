package com.mycompany.myapp.repository.partner6;

import com.mycompany.myapp.domain.SapOpor;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface SapOporRepository
    extends JpaRepository<SapOpor, Long>, JpaSpecificationExecutor<SapOpor> {
    List<SapOpor> findByOporDocEntry(String oporDocEntry);
}
