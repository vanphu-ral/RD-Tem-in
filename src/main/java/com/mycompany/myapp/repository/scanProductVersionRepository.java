package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ProfileCheckResponse;
import com.mycompany.myapp.domain.scanProductVersions;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface scanProductVersionRepository extends JpaRepository<scanProductVersions, Long> {
    @Query(value = "select * from scan_product_versions where product_id = ?1", nativeQuery = true)
    public List<scanProductVersions> getAllByProductId(Long productId);

    @Query(value = "select * from scan_product_versions where version_id = ?1 ;", nativeQuery = true)
    public scanProductVersions getAllByVersionId(Long versionId);
}
