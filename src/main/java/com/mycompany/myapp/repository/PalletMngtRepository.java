package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.PalletMngt;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PalletMngt entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PalletMngtRepository extends JpaRepository<PalletMngt, Long> {
    Optional<PalletMngt> findBySerialPallet(String serialPallet);

    List<PalletMngt> findBySerialPalletIn(Collection<String> serialPallets);
}
