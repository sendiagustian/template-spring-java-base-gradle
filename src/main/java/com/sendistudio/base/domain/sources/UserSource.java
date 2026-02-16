package com.sendistudio.base.domain.sources;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sendistudio.base.app.utils.QueryUtil;
import com.sendistudio.base.app.utils.TypeUtil;
import com.sendistudio.base.constants.enums.GlobalRoleEnum;
import com.sendistudio.base.constants.enums.UserStatusEnum;
import com.sendistudio.base.data.models.UserModel;
import com.sendistudio.base.data.requests.users.CreateUserRequest;
import com.sendistudio.base.data.schemas.UserSchema;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserSource {

    private final QueryUtil query;

    public Optional<UserModel> getUserByEmail(String email) {
        String sql = "SELECT * FROM %s WHERE %s = ?".formatted(
            UserSchema.Users.TABLE,
            UserSchema.Users.EMAIL
        );
        return query.queryForObject(sql, new UserModel.UserModelRowMapper(), email);
    }

    public String createUser(CreateUserRequest request) {
        String sql = "INSERT INTO %s (\"%s\", \"%s\", \"%s\", \"%s\") VALUES (?::uuid, ?, ?, ?::%s) RETURNING id::uuid".formatted(
            UserSchema.Users.TABLE,
            UserSchema.Users.TENANT_ID,
            UserSchema.Users.EMAIL,
            UserSchema.Users.PASSWORD_HASH,
            UserSchema.Users.GLOBAL_ROLE,
            GlobalRoleEnum.NAME
        );
        return query.queryForObject(
            sql,
            new TypeUtil.StringRowMapper("id"),
            request.getTenantId(),
            request.getEmail(),
            request.getPasswordHash(),
            request.getGlobalRole()).orElse(null);
    }

    public Boolean updatePassword(String userId, String hashedPassword) {
        String sql = "UPDATE %s SET \"%s\" = ? WHERE \"%s\" = ?::uuid".formatted(
            UserSchema.Users.TABLE,
            UserSchema.Users.PASSWORD_HASH,
            UserSchema.Users.ID
        );
        int rowsAffected = query.exec(sql, hashedPassword, userId);
        return rowsAffected > 0;
    }

    public Boolean existsByEmailAndTenantId(String email, String tenantId) {
        String sql = "SELECT COUNT(%s) FROM %s WHERE %s = ? AND %s = ?::uuid".formatted(
            UserSchema.Users.ID,
            UserSchema.Users.TABLE,
            UserSchema.Users.EMAIL,
            UserSchema.Users.TENANT_ID
        );
        Integer count = query.queryForObject(sql, new TypeUtil.IntegerRowMapper("count"), email, tenantId).orElse(0);
        return count != null && count > 0;
    }

    public Boolean updateStatus(String userId, String status) {
        String sql = "UPDATE %s SET \"%s\" = ?::%s WHERE \"%s\" = ?::uuid".formatted(
            UserSchema.Users.TABLE,
            UserSchema.Users.STATUS,
            UserStatusEnum.NAME,
            UserSchema.Users.ID
        );
        int rowsAffected = query.exec(sql, status, userId);
        return rowsAffected > 0;
    }

}
