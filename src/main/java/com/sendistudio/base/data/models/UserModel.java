package com.sendistudio.base.data.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sendistudio.base.data.schemas.UserSchema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModel implements UserDetails {
    private String id;
    private String tenantId;
    private String email;
    @JsonIgnore
    private String passwordHash;
    private String globalRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + globalRole));
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return passwordHash;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public static class UserModelRowMapper implements RowMapper<UserModel> {
        @Override
        public UserModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserModel user = new UserModel();
            user.setId(rs.getString(UserSchema.Users.ID));
            user.setTenantId(rs.getString(UserSchema.Users.TENANT_ID));
            user.setEmail(Objects.requireNonNull(rs.getString(UserSchema.Users.EMAIL)));
            user.setPasswordHash(rs.getString(UserSchema.Users.PASSWORD_HASH));
            user.setGlobalRole(rs.getString(UserSchema.Users.GLOBAL_ROLE));
            user.setCreatedAt(rs.getTimestamp(UserSchema.Users.CREATED_AT).toLocalDateTime());
            user.setUpdatedAt(rs.getTimestamp(UserSchema.Users.UPDATED_AT).toLocalDateTime());
            user.setStatus(rs.getString(UserSchema.Users.STATUS));
            return user;
        }
    }
}