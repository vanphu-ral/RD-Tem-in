package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.scanProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface scanProductRepository extends JpaRepository<scanProduct, Long> {
    @Query(value = "select * from scan_products order by product_status ;", nativeQuery = true)
    public List<scanProduct> listProduct();

    @Query(
        value = "select * from Scan_products " +
        "where product_code like ?1 " +
        "and product_name like ?2 \n" +
        "and created_at between ?3 " +
        "and ?4 \n" +
        "and username like ?5 \n" +
        "ORDER BY  product_id desc \n" +
        "OFFSET ?6 ROWS FETCH NEXT ?7 ROWS ONLY ;",
        nativeQuery = true
    )
    public List<scanProduct> getListProduct(
        String productCode,
        String productName,
        String createdAt1,
        String createdAt2,
        String username,
        Integer pageNumber,
        Integer itemPerPage
    );

    @Query(
        value = "select COUNT(product_id) from Scan_products " +
        "        where product_code like ?1  " +
        "        and product_name like ?2  " +
        "        and created_at between ?3  " +
        "        and ?4 " +
        "        and username like ?5 ;",
        nativeQuery = true
    )
    public Integer getTotalItem(String productCode, String productName, String createdAt1, String createdAt2, String username);
}
