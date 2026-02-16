package com.sendistudio.base.app.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sendistudio.base.data.models.UserModel;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppUtil {

    public static UserModel getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel currentUser = (UserModel) authentication.getPrincipal();
        return currentUser;
    }
}
