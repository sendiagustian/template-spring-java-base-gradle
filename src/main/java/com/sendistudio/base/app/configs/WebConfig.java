package com.sendistudio.base.app.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // @Autowired
    // LogInterceptorMiddleware logInterceptorMiddleware;

    // @Autowired
    // TokenInterceptorMiddleware tokenInterceptorMiddleware;

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    @Async
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Daftarkan interceptor ke semua request API
        // registry.addInterceptor(logInterceptorMiddleware).addPathPatterns("/api/**");
        // registry.addInterceptor(tokenInterceptorMiddleware).addPathPatterns("/api/**");
    }
}