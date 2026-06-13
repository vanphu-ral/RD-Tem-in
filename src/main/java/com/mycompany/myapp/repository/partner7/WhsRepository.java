package com.mycompany.myapp.repository.partner7;

import com.mycompany.myapp.service.dto.WhsDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WhsRepository {

    private final JdbcTemplate jdbcTemplate;

    public WhsRepository(
        @Qualifier("partner7JdbcTemplate") JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<WhsDTO> findAll() {
        String sql =
            "SELECT [WhsCode], [WhsName], [createDate], [updateDate], [Address2], [U_Whskeeper] " +
            "FROM [OWHS] ORDER BY [WhsCode]";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            WhsDTO dto = new WhsDTO();
            dto.setWhsCode(rs.getString("WhsCode"));
            dto.setWhsName(rs.getString("WhsName"));
            java.sql.Timestamp createDate = rs.getTimestamp("createDate");
            java.sql.Timestamp updateDate = rs.getTimestamp("updateDate");

            dto.setCreateDate(
                createDate == null ? null : createDate.toLocalDateTime()
            );
            dto.setUpdateDate(
                updateDate == null ? null : updateDate.toLocalDateTime()
            );
            dto.setAddress2(rs.getString("Address2"));
            dto.setUWhskeeper(rs.getString("U_Whskeeper"));
            return dto;
        });
    }
}
