package com.sendistudio.base.data.schemas;

public class ForgotPasswordTokenSchema {
    private static final String NAME = "core";

    public static class ForgotPasswordTokens {
        public static final String TABLE = NAME + ".forgot_password_tokens";

        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String TOKEN = "token";
        public static final String EXPIRES_AT = "expires_at";
        public static final String IS_USED = "is_used";
        public static final String CREATED_AT = "created_at";
        public static final String IP_ADDRESS = "ip_address";
    }
}
