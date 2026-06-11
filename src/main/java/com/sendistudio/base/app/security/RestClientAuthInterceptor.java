package com.sendistudio.base.app.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import com.sendistudio.base.app.utils.AppUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Interceptor that automatically injects a system M2M JWT into every
 * outbound RestClient request via the Authorization header.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestClientAuthInterceptor implements ClientHttpRequestInterceptor {

    private static final String HEADER_NAME = "Authorization";

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        String existingToken = request.getHeaders().getFirst(HEADER_NAME);
        if (existingToken != null && !existingToken.isBlank()) {
            log.debug("[RestClientAuthInterceptor] Using pre-existing token for request → {}", request.getURI());
            return execution.execute(request, body);
        }

        String token = resolveToken();
        if (token != null && !token.isBlank()) {
            request.getHeaders().set(HEADER_NAME, token);
        } else {
            log.warn("[RestClientAuthInterceptor] No token found in current request/security context for URI {}", request.getURI());
        }

        log.info("[RestClientAuthInterceptor] Injecting system token for request → {}",
                request.getURI());

        return execution.execute(request, body);
    }

    private String resolveToken() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest servletRequest = attributes.getRequest();
                String headerToken = servletRequest.getHeader(HEADER_NAME);
                if (headerToken != null && !headerToken.isBlank()) {
                    return headerToken;
                }
            }

            Authentication authentication = AppUtil.getAuthenticated();
            if (authentication != null && authentication.getCredentials() != null) {
                String credentialToken = authentication.getCredentials().toString();
                if (!credentialToken.isBlank() && !"null".equalsIgnoreCase(credentialToken)) {
                    return credentialToken;
                }
            }
        } catch (Exception ex) {
            log.warn("[RestClientAuthInterceptor] Failed to resolve token: {}", ex.getMessage());
        }

        return null;
    }
}
