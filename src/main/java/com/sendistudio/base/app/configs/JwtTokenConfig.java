package com.sendistudio.base.app.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sendistudio.base.app.properties.DatabaseProperties;
import com.sendistudio.base.app.utils.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JwtTokenConfig {
    private final JwtTokenUtil jwtTokenUtil;
    private final DatabaseProperties databaseProperties;

    @Bean
    public JwtTokenUtil.AccessToken accessToken() {
        return new JwtTokenUtil.AccessToken(jwtTokenUtil, databaseProperties);
    }

    @Bean
    public JwtTokenUtil.RefreshToken refreshToken() {
        return new JwtTokenUtil.RefreshToken(jwtTokenUtil, databaseProperties);
    }

    @Bean
    public JwtTokenUtil.ForgotPssword forgotPassword() {
        return new JwtTokenUtil.ForgotPssword(jwtTokenUtil, databaseProperties);
    }
}
