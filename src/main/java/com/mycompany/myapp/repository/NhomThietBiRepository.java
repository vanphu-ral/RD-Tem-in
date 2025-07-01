package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.NhomThietBi;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NhomThietBiRepository extends JpaRepository<NhomThietBi, Long> {
    public List<NhomThietBi> findAllByLoaiThietBi(String loaiThietBi);

    @Modifying
    @Query(value = "update nhom_thiet_bi set group_id = ?1 where id = ?2", nativeQuery = true)
    public void updateNhomThietBiNew(Long groupId, Long id);

    public List<NhomThietBi> findAllByGroupId(Long groupId);
}
