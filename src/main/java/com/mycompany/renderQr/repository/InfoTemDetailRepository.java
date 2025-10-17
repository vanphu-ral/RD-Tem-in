package com.mycompany.renderQr.repository;

import com.mycompany.renderQr.domain.InfoTemDetail;
import com.mycompany.renderQr.domain.InfoTemDetailResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InfoTemDetailRepository
    extends JpaRepository<InfoTemDetail, Long> {
    Optional<InfoTemDetail> findTopByOrderByIdDesc();

    //lấy tem detail theo request id
    List<InfoTemDetail> findByProductOfRequestId(Long productOfRequestId);

    @Modifying
    @Transactional
    @Query(
        "DELETE FROM InfoTemDetail d WHERE d.productOfRequestId = :productId"
    )
    void deleteByProductOfRequestId(@Param("productId") Long productId);

    @Query(
        "SELECT d FROM InfoTemDetail d WHERE d.productOfRequestId IN (SELECT p.id FROM ListProductOfRequest p WHERE p.requestCreateTemId = :requestId)"
    )
    List<InfoTemDetail> findByRequestCreateTemId(
        @Param("requestId") Long requestId
    );
}
