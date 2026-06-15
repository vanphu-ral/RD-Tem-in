package com.mycompany.myapp.repository;

import com.mycompany.myapp.service.dto.ProductInPoStatusDTO;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class ProductInPoStatusRepository {

    private static final String SELECT_COLUMNS =
        "SELECT [id], [sap_code], [product_name], [WhsCode], [UserData5], [created_at], [create_by], [quantity_by_po], [vendor], [vendor_name], [UOMCode]";

    private static final String FROM_TABLE = "FROM [product_in_po_status]";

    private final JdbcTemplate jdbcTemplate;

    public ProductInPoStatusRepository(
        @Qualifier("jdbcTemplate") JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Page<ProductInPoStatusDTO> findAll(Pageable pageable) {
        return findPage(null, List.of(), pageable);
    }

    public Page<ProductInPoStatusDTO> findBySapCode(
        String sapCode,
        Pageable pageable
    ) {
        return findPage("WHERE [sap_code] = ?", List.of(sapCode), pageable);
    }

    public Page<ProductInPoStatusDTO> findByWhsCode(
        String whsCode,
        Pageable pageable
    ) {
        return findPage("WHERE [WhsCode] = ?", List.of(whsCode), pageable);
    }

    public Page<ProductInPoStatusDTO> findByUserData5(
        String userData5,
        Pageable pageable
    ) {
        return findPage("WHERE [UserData5] = ?", List.of(userData5), pageable);
    }

    public List<ProductInPoStatusDTO> findByUserData5List(String userData5) {
        String sql =
            SELECT_COLUMNS +
            " " +
            FROM_TABLE +
            " WHERE [UserData5] = ? ORDER BY [id]";
        return jdbcTemplate.query(sql, this::mapRow, userData5);
    }

    public Page<ProductInPoStatusDTO> search(
        String keyword,
        Pageable pageable
    ) {
        String value = "%" + keyword + "%";
        return findPage(
            "WHERE ([sap_code] LIKE ? OR [product_name] LIKE ? OR [WhsCode] LIKE ? OR [UserData5] LIKE ?)",
            List.of(value, value, value, value),
            pageable
        );
    }

    public Optional<ProductInPoStatusDTO> findById(Long id) {
        String sql = SELECT_COLUMNS + " " + FROM_TABLE + " WHERE [id] = ?";
        List<ProductInPoStatusDTO> rows = jdbcTemplate.query(
            sql,
            this::mapRow,
            id
        );
        return rows.stream().findFirst();
    }

    public ProductInPoStatusDTO insert(ProductInPoStatusDTO dto) {
        String sql =
            "INSERT INTO [product_in_po_status] ([sap_code], [product_name], [WhsCode], [UserData5], [create_by], [quantity_by_po], [vendor], [vendor_name], [UOMCode]) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
            connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, dto.getSapCode());
                ps.setString(2, dto.getProductName());
                ps.setString(3, dto.getWhsCode());
                ps.setString(4, dto.getUserData5());
                ps.setString(5, dto.getCreateBy());
                ps.setInt(
                    6,
                    dto.getQuantityByPo() != null ? dto.getQuantityByPo() : 0
                );
                ps.setString(7, dto.getVendor());
                ps.setString(8, dto.getVendorName());
                ps.setString(9, dto.getUOMCode());
                return ps;
            },
            keyHolder
        );

        dto.setId(keyHolder.getKey().longValue());
        return dto;
    }

    public Optional<ProductInPoStatusDTO> update(ProductInPoStatusDTO dto) {
        String sql =
            "UPDATE [product_in_po_status] SET [sap_code] = ?, [product_name] = ?, [WhsCode] = ?, [UserData5] = ?, [create_by] = ?, [quantity_by_po] = ?, [vendor] = ?, [vendor_name] = ?, [UOMCode] = ? WHERE [id] = ?";
        int updated = jdbcTemplate.update(
            sql,
            dto.getSapCode(),
            dto.getProductName(),
            dto.getWhsCode(),
            dto.getUserData5(),
            dto.getCreateBy(),
            dto.getQuantityByPo() != null ? dto.getQuantityByPo() : 0,
            dto.getVendor(),
            dto.getVendorName(),
            dto.getUOMCode(),
            dto.getId()
        );

        if (updated == 0) {
            return Optional.empty();
        }

        return findById(dto.getId());
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM [product_in_po_status] WHERE [id] = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    private Page<ProductInPoStatusDTO> findPage(
        String whereClause,
        List<Object> params,
        Pageable pageable
    ) {
        String countSql = "SELECT COUNT(*) FROM [product_in_po_status]";
        if (StringUtils.hasText(whereClause)) {
            countSql += " " + whereClause;
        }

        long total = Optional.ofNullable(
            jdbcTemplate.queryForObject(countSql, Long.class, params.toArray())
        ).orElse(0L);

        String sql = SELECT_COLUMNS + " " + FROM_TABLE;
        List<Object> pageParams = new ArrayList<>(params);
        if (StringUtils.hasText(whereClause)) {
            sql += " " + whereClause;
        }
        sql += " ORDER BY [id] OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        pageParams.add(pageable.getOffset());
        pageParams.add((long) pageable.getPageSize());

        List<ProductInPoStatusDTO> rows = jdbcTemplate.query(
            sql,
            this::mapRow,
            pageParams.toArray()
        );
        return new PageImpl<>(rows, pageable, total);
    }

    private ProductInPoStatusDTO mapRow(java.sql.ResultSet rs, int rowNum)
        throws java.sql.SQLException {
        ProductInPoStatusDTO dto = new ProductInPoStatusDTO();
        dto.setId(rs.getLong("id"));
        dto.setSapCode(rs.getString("sap_code"));
        dto.setProductName(rs.getString("product_name"));
        dto.setWhsCode(rs.getString("WhsCode"));
        dto.setUserData5(rs.getString("UserData5"));
        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        dto.setCreatedAt(
            createdAt == null ? null : createdAt.toLocalDateTime()
        );
        dto.setCreateBy(rs.getString("create_by"));
        dto.setQuantityByPo(
            rs.getObject("quantity_by_po") != null
                ? rs.getInt("quantity_by_po")
                : null
        );
        dto.setVendor(rs.getString("vendor"));
        dto.setVendorName(rs.getString("vendor_name"));
        dto.setUOMCode(rs.getString("UOMCode"));
        return dto;
    }
}
