package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.PalletBoxMapping;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PalletBoxMapping entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PalletBoxMappingRepository
    extends JpaRepository<PalletBoxMapping, Long> {
    List<PalletBoxMapping> findByReelIdBoxIn(Collection<String> reelIds);

    Optional<PalletBoxMapping> findByReelIdBox(String reelIdBox);

    List<PalletBoxMapping> findBySerialPalletOrderByReelIdBoxAsc(
        String serialPallet
    );

    @Query(
        "select palletBoxMapping from PalletBoxMapping palletBoxMapping" +
        " left join fetch palletBoxMapping.vendorLabelInfo" +
        " where palletBoxMapping.serialPallet = :serialPallet" +
        " order by palletBoxMapping.reelIdBox asc"
    )
    List<PalletBoxMapping> findBySerialPalletWithVendorLabelInfo(
        @Param("serialPallet") String serialPallet
    );
}
