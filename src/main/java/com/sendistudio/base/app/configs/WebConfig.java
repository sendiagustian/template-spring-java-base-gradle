package com.sendistudio.base.app.configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sendistudio.base.app.resolvers.PageParamsResolver;
import com.sendistudio.base.app.resolvers.SortParamsResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final PageParamsResolver pageParamsResolver;
    private final SortParamsResolver sortParamsResolver;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Async
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // Daftarkan resolver kita ke Spring
        resolvers.add(pageParamsResolver);
        resolvers.add(sortParamsResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Daftarkan interceptor ke semua request API
        // registry.addInterceptor(logInterceptorMiddleware).addPathPatterns("/api/**");
    }
}