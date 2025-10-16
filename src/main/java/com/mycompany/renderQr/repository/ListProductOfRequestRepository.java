package com.mycompany.renderQr.repository;

import com.mycompany.renderQr.domain.ListProductOfRequest;
import com.mycompany.renderQr.domain.ListProductOfRequestResponse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ListProductOfRequestRepository
    extends JpaRepository<ListProductOfRequest, Long> {
    @Query(
        "SELECT p FROM ListProductOfRequest p WHERE p.requestCreateTemId = :requestId"
    )
    List<ListProductOfRequest> findByRequestCreateTemId(
        @Param("requestId") Long requestId
    );

    // Delete all products by request ID
    @Modifying
    @Transactional
    @Query(
        "DELETE FROM ListProductOfRequest p WHERE p.requestCreateTemId = :requestId"
    )
    void deleteByRequestCreateTemId(@Param("requestId") Long requestId);

    // Nếu bạn muốn lấy tất cả projection
    List<ListProductOfRequestResponse> findAllProjectedBy();
}
