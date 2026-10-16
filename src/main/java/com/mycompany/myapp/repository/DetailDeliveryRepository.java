package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.DetailDelivery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DetailDelivery entity.
 */
@Repository
public interface DetailDeliveryRepository
    extends JpaRepository<DetailDelivery, UUID> {
    default Optional<DetailDelivery> findOneWithEagerRelationships(UUID id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<DetailDelivery> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<DetailDelivery> findAllWithEagerRelationships(
        Pageable pageable
    ) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select detailDelivery from DetailDelivery detailDelivery left join fetch detailDelivery.delivery",
        countQuery = "select count(detailDelivery) from DetailDelivery detailDelivery"
    )
    Page<DetailDelivery> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select detailDelivery from DetailDelivery detailDelivery left join fetch detailDelivery.delivery"
    )
    List<DetailDelivery> findAllWithToOneRelationships();

    @Query(
        "select detailDelivery from DetailDelivery detailDelivery left join fetch detailDelivery.delivery where detailDelivery.lineId =:id"
    )
    Optional<DetailDelivery> findOneWithToOneRelationships(
        @Param("id") UUID id
    );

    @Query(
        "select detailDelivery from DetailDelivery detailDelivery where detailDelivery.delivery.deliveryId =:deliveryId"
    )
    List<DetailDelivery> findByDelivery_DeliveryId(
        @Param("deliveryId") UUID deliveryId
    );
}
