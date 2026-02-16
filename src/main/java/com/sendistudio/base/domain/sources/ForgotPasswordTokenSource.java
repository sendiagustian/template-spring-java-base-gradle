package com.sendistudio.base.domain.sources;

import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import com.sendistudio.base.app.utils.QueryUtil;
import com.sendistudio.base.app.utils.TypeUtil;
import com.sendistudio.base.data.schemas.ForgotPasswordTokenSchema;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ForgotPasswordTokenSource {
    private final QueryUtil query;

    public Boolean upsertToken(String userId, String token, LocalDateTime expiresAt, String ip_address) {
        String sql = """
                INSERT INTO %s (%s, %s, %s, %s)
                VALUES (?::uuid, ?, ?, ?)
                ON CONFLICT (%s)  -- Target Konflik: token
                DO UPDATE SET
                    token = EXCLUDED.token,
                    expires_at = EXCLUDED.expires_at,
                    ip_address = EXCLUDED.ip_address,
                    created_at = now()
                """.formatted(
                ForgotPasswordTokenSchema.ForgotPasswordTokens.TABLE, // 1. Table Name
                ForgotPasswordTokenSchema.ForgotPasswordTokens.USER_ID, // 2. Col User
                ForgotPasswordTokenSchema.ForgotPasswordTokens.TOKEN, // 3. Col Token
                ForgotPasswordTokenSchema.ForgotPasswordTokens.EXPIRES_AT, // 4. Col Expires
                ForgotPasswordTokenSchema.ForgotPasswordTokens.IP_ADDRESS, // 5. Col IP
                ForgotPasswordTokenSchema.ForgotPasswordTokens.TOKEN // 6. Target Conflict
        );

        int rowsAffected = query.exec(sql, userId, token, expiresAt, ip_address);
        return rowsAffected > 0;
    }

    public Boolean isTokenValid(String token) {
        String sql = "SELECT COUNT(%s) FROM %s WHERE %s = ? AND %s > now() AND %s = false".formatted(
                ForgotPasswordTokenSchema.ForgotPasswordTokens.ID,
                ForgotPasswordTokenSchema.ForgotPasswordTokens.TABLE,
                ForgotPasswordTokenSchema.ForgotPasswordTokens.TOKEN,
                ForgotPasswordTokenSchema.ForgotPasswordTokens.EXPIRES_AT,
                ForgotPasswordTokenSchema.ForgotPasswordTokens.IS_USED);

        Integer count = query.queryForObject(sql, new TypeUtil.IntegerRowMapper("count"), token).orElse(0);
        return count > 0;
    }

    public Boolean existsNotUsedAndNotExpiredTokenByUserId(String userId) {
        String sql = "SELECT COUNT(%s) FROM %s WHERE %s = ?::uuid AND %s > now() AND %s = false".formatted(
                ForgotPasswordTokenSchema.ForgotPasswordTokens.ID,
                ForgotPasswordTokenSchema.ForgotPasswordTokens.TABLE,
                ForgotPasswordTokenSchema.ForgotPasswordTokens.USER_ID,
                ForgotPasswordTokenSchema.ForgotPasswordTokens.EXPIRES_AT,
                ForgotPasswordTokenSchema.ForgotPasswordTokens.IS_USED);

        Integer count = query.queryForObject(sql, new TypeUtil.IntegerRowMapper("count"), userId).orElse(0);
        return count > 0;
    }

    public Boolean markTokenAsUsed(String token) {
        String sql = "UPDATE %s SET %s = true WHERE %s = ?".formatted(
                ForgotPasswordTokenSchema.ForgotPasswordTokens.TABLE,
                ForgotPasswordTokenSchema.ForgotPasswordTokens.IS_USED,
                ForgotPasswordTokenSchema.ForgotPasswordTokens.TOKEN);

        int rowsAffected = query.exec(sql, token);
        return rowsAffected > 0;
    }
}
