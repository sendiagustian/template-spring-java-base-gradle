package com.sendistudio.base.app.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sendistudio.base.data.models.UserModel;

public class AppUtil {
    private AppUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static UserModel getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserModel) authentication.getPrincipal();
    }
}
