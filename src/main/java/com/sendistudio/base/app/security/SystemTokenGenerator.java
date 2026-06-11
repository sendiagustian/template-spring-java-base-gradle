package com.sendistudio.base.app.security;

import org.springframework.stereotype.Component;

import com.sendistudio.base.app.utils.JwtTokenUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Generates a short-lived System (M2M) JWT token for outbound service-to-service calls.
 * Reuses the existing AccessToken infrastructure — signs with secretToken key (HS256).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SystemTokenGenerator {

    private static final long  EXPIRATION_MS = 1000L * 60 * 60; // 1 hour

    // M2M identity claims — fixed system identity for Scheduler service
    private static final String SYSTEM_USERNAME   = "service-account@sendistudio.id";
    private static final String SYSTEM_USER_ID   = "4e0ddeb8-50fd-436b-b8fe-141dfcadd5b4"; //scheduler-service user ID in Auth service
    private static final String SYSTEM_ROLE      = "Super Admin";

    private final JwtTokenUtil.AccessToken accessToken;

    /**
     * Generates a signed JWT for use in the Authorization header.
     * Token is valid for 1 hour from the time of generation.
     */
    public String generateToken() {
        String token = accessToken.generateToken(
                SYSTEM_USERNAME,
                SYSTEM_USER_ID,
                SYSTEM_ROLE,
                EXPIRATION_MS
        );

        log.debug("[SystemTokenGenerator] M2M system token generated successfully.");
        return token;
    }
}
