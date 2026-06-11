package com.sendistudio.base.app.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class AppUtil {

    // Hide implicit public constructor for utility class
    private AppUtil() {}

    public static Authentication getAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
