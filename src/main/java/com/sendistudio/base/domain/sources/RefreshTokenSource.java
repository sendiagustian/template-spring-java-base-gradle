package com.sendistudio.base.domain.sources;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sendistudio.base.app.utils.QueryUtil;
import com.sendistudio.base.app.utils.TypeUtil;
import com.sendistudio.base.data.requests.auth.CreateRefreshTokenRequest;
import com.sendistudio.base.data.schemas.RefreshTokenSchema;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RefreshTokenSource {
    private final QueryUtil query;

    public Optional<String> getActiveRefreshTokenByUserId(String userId) {
        String sql = "SELECT token FROM %s WHERE %s = ?::uuid AND %s = false AND %s > NOW() ORDER BY %s DESC LIMIT 1".formatted(
            RefreshTokenSchema.RefreshTokens.TABLE,
            RefreshTokenSchema.RefreshTokens.USER_ID,
            RefreshTokenSchema.RefreshTokens.IS_REVOKED,
            RefreshTokenSchema.RefreshTokens.EXPIRES_AT,
            RefreshTokenSchema.RefreshTokens.EXPIRES_AT
        );

        return query.queryForObject(sql, new TypeUtil.StringRowMapper("token"), userId);
    }

    public Boolean isRefreshTokenValid(String token) {
        String sql = "SELECT COUNT(%s) FROM %s WHERE token = ? AND %s = false AND %s > NOW()".formatted(
            RefreshTokenSchema.RefreshTokens.ID,
            RefreshTokenSchema.RefreshTokens.TABLE,
            RefreshTokenSchema.RefreshTokens.IS_REVOKED,
            RefreshTokenSchema.RefreshTokens.EXPIRES_AT
        );

        Integer count = query.queryForObject(sql, new TypeUtil.IntegerRowMapper("count"), token).orElse(0);

        return count != null && count > 0;
    }

    public Boolean createRefreshToken(CreateRefreshTokenRequest request) {
        String sql = "INSERT INTO %s (%s, %s, %s, %s) VALUES (?::uuid, ?, to_timestamp(? / 1000.0), ?)".formatted(
            RefreshTokenSchema.RefreshTokens.TABLE,
            RefreshTokenSchema.RefreshTokens.USER_ID,
            RefreshTokenSchema.RefreshTokens.TOKEN,
            RefreshTokenSchema.RefreshTokens.EXPIRES_AT,
            RefreshTokenSchema.RefreshTokens.IS_REVOKED
        );

        int rowsAffected = query.exec(
            sql,
            request.getUserId(),
            request.getToken(),
            request.getExpiresAt(),
            request.getIsRevoked());

        return rowsAffected > 0;
    }

    public Boolean revokeRefreshToken(String token) {
        String sql = "UPDATE %s SET is_revoked = true WHERE %s = ?".formatted(
            RefreshTokenSchema.RefreshTokens.TABLE,
            RefreshTokenSchema.RefreshTokens.TOKEN
        );

        int rowsAffected = query.exec(sql, token);

        return rowsAffected > 0;
    }
}
