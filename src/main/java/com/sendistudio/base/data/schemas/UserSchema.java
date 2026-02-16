package com.sendistudio.base.data.schemas;

public class UserSchema {
    public static final String NAME = "core";

    public static class Users {
        // Selalu gunakan format schema.table agar aman
        public static final String TABLE = NAME + ".users";

        public static final String ID = "id";
        public static final String TENANT_ID = "tenant_id";
        public static final String EMAIL = "email";
        public static final String PASSWORD_HASH = "password_hash";
        public static final String GLOBAL_ROLE = "global_role";
        public static final String CREATED_AT = "created_at";
        public static final String UPDATED_AT = "updated_at";
        public static final String STATUS = "status";
    }

}
