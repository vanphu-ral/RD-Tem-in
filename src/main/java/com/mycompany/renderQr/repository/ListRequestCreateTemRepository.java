package com.mycompany.renderQr.repository;

import com.mycompany.renderQr.domain.*;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ListRequestCreateTemRepository
    extends JpaRepository<ListRequestCreateTem, Long> {
    List<ListRequestCreateTemResponse> findAllProjectedBy();

    List<ListRequestCreateTem> findByStatus(String status);

    @Query(
        "select r from ListRequestCreateTem r " +
        "where (:search is null or :search = '' " +
        "or lower(coalesce(r.vendor, '')) like :searchLike " +
        "or lower(coalesce(r.vendorName, '')) like :searchLike " +
        "or lower(coalesce(r.userData5, '')) like :searchLike " +
        "or lower(coalesce(r.createdBy, '')) like :searchLike " +
        "or lower(coalesce(r.status, '')) like :searchLike) " +
        "and (:status is null or :status = '' or r.status = :status) " +
        "and (:vendor is null or :vendor = '' " +
        "or lower(coalesce(r.vendor, '')) like :vendorLike) " +
        "and (:vendorName is null or :vendorName = '' " +
        "or lower(coalesce(r.vendorName, '')) like :vendorNameLike) " +
        "and (:userData5 is null or :userData5 = '' " +
        "or lower(coalesce(r.userData5, '')) like :userData5Like) " +
        "and (:createdBy is null or :createdBy = '' " +
        "or lower(coalesce(r.createdBy, '')) like :createdByLike) " +
        "and (:createdDateStart is null or r.createdDate >= :createdDateStart) " +
        "and (:createdDateEnd is null or r.createdDate < :createdDateEnd)"
    )
    Page<ListRequestCreateTem> search(
        @Param("search") String search,
        @Param("searchLike") String searchLike,
        @Param("status") String status,
        @Param("vendor") String vendor,
        @Param("vendorLike") String vendorLike,
        @Param("vendorName") String vendorName,
        @Param("vendorNameLike") String vendorNameLike,
        @Param("userData5") String userData5,
        @Param("userData5Like") String userData5Like,
        @Param("createdBy") String createdBy,
        @Param("createdByLike") String createdByLike,
        @Param("createdDateStart") LocalDateTime createdDateStart,
        @Param("createdDateEnd") LocalDateTime createdDateEnd,
        Pageable pageable
    );
}
