package com.sendistudio.base.app.utils;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

@Component
public class TypeUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String genereateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String generateOTP(Integer digit) {
        String otp = "";
        for (int i = 0; i < digit; i++) {
            otp += (int) (ThreadLocalRandom.current().nextDouble() * 10);
        }
        return otp;
    }

    public BigInteger generateExpiredAtByDay(Integer dayPlus) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(dayPlus);
        Timestamp timestamp = Timestamp.valueOf(tomorrow);
        return BigInteger.valueOf(timestamp.getTime());
    }

    public BigInteger generateExpiredAtByHour(Integer hourPlus) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusHours(hourPlus);
        Timestamp timestamp = Timestamp.valueOf(tomorrow);
        return BigInteger.valueOf(timestamp.getTime());
    }

    public BigInteger generateExpiredAtByMinute(Integer minutePlus) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusMinutes(minutePlus);
        Timestamp timestamp = Timestamp.valueOf(tomorrow);
        return BigInteger.valueOf(timestamp.getTime());
    }

    public Integer getPagginationOffset(Integer page, Integer size) {
        Integer offset = 0;

        if (page == 1) {
            offset = 0;
        } else {
            Integer pageFisrt = 1 * size;
            Integer pageRequest = (page * size);
            offset = pageRequest - pageFisrt;
        }

        if ((page - 1) > offset) {
            throw new IllegalArgumentException("Page not found");
        }
        return offset;
    }

    public Boolean isTokenExpired(BigInteger expiredAt) {
        BigInteger now = BigInteger.valueOf(System.currentTimeMillis());
        return now.compareTo(expiredAt) > 0;
    }

    public <T> T mapToModel(Map<String, Object> map, Class<T> clazz) {
        try {
            return objectMapper.convertValue(map, clazz);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error mapping map to model: " + clazz.getSimpleName(), e);
        }
    }

    public <T> T mapToModel(Map<String, Object> map, ParameterizedTypeReference<T> typeReference) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructType(typeReference.getType());
            return objectMapper.convertValue(map, javaType);
        } catch (IllegalArgumentException e) {
            System.out.println(e);
            throw new RuntimeException("Error mapping map ParameterizedTypeReference to model", e);
        }
    }

    /**
     * Convert object to JSON string
     */
    public String convertToJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Error converting object to JSON string", e);
        }
    }

    /**
     * Convert JSON string to object
     */
    public <T> T convertFromJsonString(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON string to object: " + clazz.getSimpleName(), e);
        }
    }

    
    /**
     * Generic helper method to merge field: use newValue if not null, otherwise use existing value
     * 
     * @param <T> Type of the field value
     * @param <E> Type of the existing object
     * @param newValue New value from request
     * @param existing Existing object (can be any type)
     * @param getter Function to extract field from existing object
     * @return Merged value
     */
    public <T, E> T mergeField(T newValue, E existing, java.util.function.Function<E, T> getter) {
        if (newValue != null) {
            return newValue;
        }
        return existing != null ? getter.apply(existing) : null;
    }


    @AllArgsConstructor
    public static class StringRowMapper implements RowMapper<String> {

        private String columnName;

        @Override
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(columnName);
        }
    }

    @AllArgsConstructor
    public static class IntegerRowMapper implements RowMapper<Integer> {

        private String columnName;

        @Override
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(columnName);
        }
    }

}
