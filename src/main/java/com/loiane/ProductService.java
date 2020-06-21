package com.loiane;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Types;
import java.util.List;

@Service
public class ProductService {

    private final JdbcTemplate template;

    private final String findByIdSql = """
            SELECT * FROM Product
            WHERE id = ?
            """;

    private final String insertSql = """
            INSERT INTO Product (name, status) \
            VALUES (?, ?) 
            """;

    private final RowMapper<ProductRecord> productRowMapper = (rs, rowNum) -> new ProductRecord(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("status")
    );

    public ProductService(JdbcTemplate template) {
        this.template = template;
    }

    public ProductRecord findById(Integer id) {
        return template.queryForObject(findByIdSql, new Object[]{id}, productRowMapper);
    }

    public ProductRecord create(String name, ProductStatus status) {
        var statusCode = switch (status) {
            case ACTIVE -> 1;
            case INACTIVE -> 0;
            //default -> 0;
        };

        var result = 0;
        switch (status) {
            case ACTIVE: result = 1; break;
            case INACTIVE: result = 0; break;
        }

        var pscf = new PreparedStatementCreatorFactory(insertSql, List.of(
                new SqlParameter(Types.VARCHAR, "name"),
                new SqlParameter(Types.INTEGER, "status")
        )){{
            setReturnGeneratedKeys(true);
            setGeneratedKeysColumnNames("id");
        }};
        var psc = pscf.newPreparedStatementCreator(List.of(name, statusCode));
        var generatedKey = new GeneratedKeyHolder();
        template.update(psc, generatedKey);
        if (generatedKey.getKey() instanceof BigInteger id) {
            // BigInteger id = (BigInteger) generatedKey.getKey();
            return new ProductRecord(id.intValue(), name, statusCode);
        }
        throw new IllegalArgumentException("Could not create record.");
    }
}
