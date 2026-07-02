package com.sendistudio.base.app.utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sendistudio.base.data.models.PagingModel;
import com.sendistudio.base.data.models.SortingModel;
import com.sendistudio.base.data.responses.DataPaginationResponse;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PgsqlQueryUtil {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    // ========================================================================
    // SECTION 1: SIMPLE QUERY (Positional Parameters: ?)
    // Cocok untuk query sederhana: "SELECT * FROM users WHERE id = ?"
    // ========================================================================

    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... args) {
        return jdbcTemplate.query(Objects.requireNonNull(sql), Objects.requireNonNull(rowMapper), args);
    }

    public <T> Optional<T> queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        try {
            T result = jdbcTemplate.queryForObject(Objects.requireNonNull(sql), Objects.requireNonNull(rowMapper),
                    args);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int exec(String sql, Object... args) {
        return jdbcTemplate.update(Objects.requireNonNull(sql), args);
    }

    public int[] batchExec(String sql, List<Object[]> batchArgs) {
        return jdbcTemplate.batchUpdate(Objects.requireNonNull(sql), Objects.requireNonNull(batchArgs));
    }

    // ========================================================================
    // SECTION 1.5: SIMPLE QUERY WITH SORTING
    // Query dengan positional parameters dan sorting support
    // ========================================================================

    /**
     * Query for list with sorting support
     * @param sql Base SQL query (without ORDER BY)
     * @param rowMapper Row mapper for results
     * @param sorting Sorting configuration (can be null)
     * @param columnMapping Map of field names to database column names (e.g., "createdAt" -> "created_at")
     * @param args Query parameters
     */
    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, SortingModel sorting, 
            Map<String, String> columnMapping, Object... args) {
        String finalSql = buildSqlWithSorting(sql, sorting, columnMapping);
        return jdbcTemplate.query(Objects.requireNonNull(finalSql), Objects.requireNonNull(rowMapper), args);
    }

    // ========================================================================
    // SECTION 2: COMPLEX QUERY (Named Parameters: :paramName)
    // Cocok untuk query kompleks/join: "SELECT * FROM users WHERE email = :email"
    // ========================================================================

    // 1. Named Query for List
    public <T> List<T> queryForList(String sql, Map<String, Object> params, RowMapper<T> rowMapper) {
        return namedJdbcTemplate.query(Objects.requireNonNull(sql), params, Objects.requireNonNull(rowMapper));
    }

    // 2. Named Query for Object (Single Result)
    public <T> Optional<T> queryForObject(String sql, Map<String, Object> params, RowMapper<T> rowMapper) {
        try {
            T result = namedJdbcTemplate.queryForObject(Objects.requireNonNull(sql), params,
                    Objects.requireNonNull(rowMapper));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    // 3. Named Exec (Insert/Update/Delete)
    public int exec(String sql, Map<String, Object> params) {
        return namedJdbcTemplate.update(Objects.requireNonNull(sql), params);
    }

    // ========================================================================
    // SECTION 3: SORTING UTILITIES
    // Helper methods untuk build ORDER BY clause
    // ========================================================================

    /**
     * Build SQL with ORDER BY clause from SortingModel
     * @param baseSql Base SQL query
     * @param sorting Sorting configuration
     * @param columnMapping Map of API field names to DB column names (null if no mapping needed)
     * @return SQL with ORDER BY appended if sorting is active
     */
    private String buildSqlWithSorting(String baseSql, SortingModel sorting, Map<String, String> columnMapping) {
        if (sorting == null || !sorting.isActive()) {
            return baseSql;
        }

        String sortColumn = sorting.getSortBy();
        
        // Apply column mapping if provided
        if (columnMapping != null && columnMapping.containsKey(sortColumn)) {
            sortColumn = columnMapping.get(sortColumn);
        }

        String direction = sorting.getValidatedDirection();

        // Always append a stable tiebreaker to guarantee consistent ordering across pages.
        // Without this, rows with the same sort value can appear in different positions
        // between pages (duplicates / missing rows).
        if (sortColumn.equalsIgnoreCase("id")) {
            return baseSql + " ORDER BY " + sortColumn + " " + direction;
        }
        return baseSql + " ORDER BY " + sortColumn + " " + direction + ", id ASC";
    }

    // ========================================================================
    // SECTION 4: PAGINATION UTILITIES
    // ========================================================================

    /**
     * Generic Pagination Helper
     */
    public <T> DataPaginationResponse<List<T>> queryForPage(
            String baseSql, // Query SELECT data (tanpa LIMIT/OFFSET)
            String countSql, // Query SELECT COUNT(*)
            Object[] params, // Parameter untuk WHERE clause (tanda tanya ?)
            RowMapper<T> rowMapper,
            int page, // Halaman saat ini (1, 2, 3...)
            int size // Jumlah data per halaman
    ) {
        return queryForPage(baseSql, countSql, params, rowMapper, page, size, null, null);
    }

    /**
     * Generic Pagination Helper with Sorting Support
     */
    public <T> DataPaginationResponse<List<T>> queryForPage(
            String baseSql, // Query SELECT data (tanpa LIMIT/OFFSET dan ORDER BY)
            String countSql, // Query SELECT COUNT(*)
            Object[] params, // Parameter untuk WHERE clause (tanda tanya ?)
            RowMapper<T> rowMapper,
            int page, // Halaman saat ini (1, 2, 3...)
            int size, // Jumlah data per halaman
            SortingModel sorting, // Sorting configuration (can be null)
            Map<String, String> columnMapping // Field to column mapping (can be null)
    ) {
        // 1. Hitung Total Data Dulu
        Integer totalItems = jdbcTemplate.queryForObject(countSql, Integer.class, params);
        if (totalItems == null) totalItems = 0;

        // OPTIMASI: Kalau data kosong, langsung return list kosong.
        if (totalItems == 0) {
            PagingModel paging = new PagingModel(size, 0, 1, 1);
            return new DataPaginationResponse<>(true, List.of(), paging);
        }

        // 2. Hitung Total Halaman
        int totalPages = (int) Math.ceil((double) totalItems / size);
        if (totalPages < 1) totalPages = 1; // Minimal 1 halaman walau data 0

        // 3. LOGIC SMART RESET:
        // Jika user minta page 10, padahal cuma ada 2 page, paksa set ke page terakhir.
        if (page > totalPages) {
            page = totalPages;
        }
        // Jika user minta page 0 atau minus
        if (page < 1) {
            page = 1;
        }

        // 4. Baru Hitung Offset setelah page dipastikan aman
        int offset = (page - 1) * size;

        // 5. Build SQL with Sorting (jika ada) kemudian tambahkan LIMIT OFFSET
        String sqlWithSorting = buildSqlWithSorting(baseSql, sorting, columnMapping);
        String finalSql = sqlWithSorting + " LIMIT ? OFFSET ?";

        // 6. Gabungkan parameter asli dengan parameter limit & offset
        Object[] newParams = new Object[params.length + 2];
        if (params.length > 0) {
            System.arraycopy(params, 0, newParams, 0, params.length);
        }
        newParams[params.length] = size; // Parameter LIMIT
        newParams[params.length + 1] = offset; // Parameter OFFSET

        // 7. Eksekusi Query Data
        List<T> content = jdbcTemplate.query(finalSql, rowMapper, newParams);

        // 8. Buat Object PagingModel
        PagingModel paging = new PagingModel(size, totalItems, page, totalPages);

        // 9. Return Response Wrapper Status true, Data List, dan Paging Info
        return new DataPaginationResponse<>(true, content, paging);
    }
}