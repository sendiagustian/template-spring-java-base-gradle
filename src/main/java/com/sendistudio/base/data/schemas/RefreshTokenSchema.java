package com.sendistudio.base.data.schemas;

public class RefreshTokenSchema {
    public static final String NAME = "core";

    public static class RefreshTokens {
        // Selalu gunakan format schema.table agar aman
        public static final String TABLE = NAME + ".refresh_tokens";

        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String TOKEN = "token";
        public static final String EXPIRES_AT = "expires_at";
        public static final String IS_REVOKED = "is_revoked";
        public static final String CREATED_AT = "created_at";
    }
}
