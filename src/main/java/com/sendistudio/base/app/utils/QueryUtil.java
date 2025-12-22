package com.sendistudio.base.app.utils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class QueryUtil {
    private final JdbcTemplate jdbcTemplate;

    // Generic method to execute a query for a list of models
    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... args) {
        return jdbcTemplate.query(Objects.requireNonNull(sql), Objects.requireNonNull(rowMapper), args);
    }

    // Generic method to execute a query for a single model
    public <T> Optional<T> queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        try {
            T result = jdbcTemplate.queryForObject(Objects.requireNonNull(sql), Objects.requireNonNull(rowMapper),
                    args);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            // Return an empty Optional if no result is found
            return Optional.empty();
        }
    }

    // Generic method to execute an insert/update/delete operation
    public int exec(String sql, Object... args) {
        return jdbcTemplate.update(Objects.requireNonNull(sql), args);
    }

    // Generic method to execute batch updates
    public int[] batchExec(String sql, List<Object[]> batchArgs) {
        return jdbcTemplate.batchUpdate(Objects.requireNonNull(sql), Objects.requireNonNull(batchArgs));
    }
}
