package com.mycompany.renderQr.repository;

import com.mycompany.renderQr.domain.InfoTemDetail;
import com.mycompany.renderQr.domain.InfoTemDetailResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoTemDetailRepository
    extends JpaRepository<InfoTemDetail, Long> {
    Optional<InfoTemDetail> findTopByOrderByIdDesc();

    //lấy tem detail theo request id
    List<InfoTemDetail> findByProductOfRequestId(Long productOfRequestId);
}
