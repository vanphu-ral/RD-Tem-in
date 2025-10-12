package com.mycompany.renderQr.repository;

import com.mycompany.renderQr.domain.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ListRequestCreateTemRepository
    extends JpaRepository<ListRequestCreateTem, Long> {
    List<ListRequestCreateTemResponse> findAllProjectedBy();

    List<ListRequestCreateTem> findByStatus(String status);
}
