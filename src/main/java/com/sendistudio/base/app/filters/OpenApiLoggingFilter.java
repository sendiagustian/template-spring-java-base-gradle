package com.sendistudio.base.app.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class OpenApiLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(OpenApiLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path != null && (path.startsWith("/v3/api-docs") || path.startsWith("/scalar") || path.startsWith("/api-docs"))) {
            log.info("OpenApiLoggingFilter - incoming request: {} {}", request.getMethod(), path);
            filterChain.doFilter(request, response);
            log.info("OpenApiLoggingFilter - response status: {} for {} {}", response.getStatus(), request.getMethod(), path);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
