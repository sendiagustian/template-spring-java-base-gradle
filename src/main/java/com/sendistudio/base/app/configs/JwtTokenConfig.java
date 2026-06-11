package com.sendistudio.base.app.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sendistudio.base.app.properties.AppProperties;
import com.sendistudio.base.app.utils.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JwtTokenConfig {
    private final JwtTokenUtil jwtTokenUtil;
    private final AppProperties appProperties;

    @Bean
    public JwtTokenUtil.AccessToken accessToken() {
        return new JwtTokenUtil.AccessToken(jwtTokenUtil, appProperties);
    }

    @Bean
    public JwtTokenUtil.RefreshToken refreshToken() {
        return new JwtTokenUtil.RefreshToken(jwtTokenUtil, appProperties);
    }

    @Bean
    public JwtTokenUtil.ForgotPssword forgotPassword() {
        return new JwtTokenUtil.ForgotPssword(jwtTokenUtil, appProperties);
    }
}
