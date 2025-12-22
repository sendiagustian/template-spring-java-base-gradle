package com.sendistudio.base.app.configs;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.sendistudio.base.constants.ExcludeEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

/*
    * Security Configuration Class
*/
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private JwtAauthFilterConfig jwtAauthFilterConfig;

    @Autowired
    private ExcludeEndpoint excludeEndpoint;

    @Autowired
    private AuthenticationEntryPointConfig authenticationEntryPointConfig;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(csrf -> csrf.disable());

        // Setup Excludes (Sama seperti logika Anda)
        String[] excludesData = excludeEndpoint.getAllExcludes().toArray(new String[0]);

        http.authorizeHttpRequests(auth -> {
            // Izinkan endpoint public
            auth.requestMatchers(excludesData).permitAll();
            // Semua request lain harus diautentikasi
            auth.anyRequest().authenticated();
        });

        // Handle unauthorized access
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPointConfig));

        // Tambahkan Filter JWT SEBELUM UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAauthFilterConfig, UsernamePasswordAuthenticationFilter.class);

        http.cors(cors -> {
            cors.configurationSource(request -> {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                configuration.setAllowedHeaders(List.of("*"));
                return configuration;
            });
        });

        return http.build();
    }
}