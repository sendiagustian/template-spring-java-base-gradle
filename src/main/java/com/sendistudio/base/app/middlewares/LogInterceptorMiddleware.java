package com.sendistudio.base.app.middlewares;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerInterceptor;

import com.sendistudio.base.app.utils.JwtTokenUtil;
import com.sendistudio.base.constants.ExcludeEndpoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

// import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
// import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
// import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Slf4j
@Component
// @SecurityScheme(name = "LOG-SERVICE-TRX", type = SecuritySchemeType.APIKEY,
// in = SecuritySchemeIn.HEADER)
public class LogInterceptorMiddleware implements HandlerInterceptor {

    @Autowired
    ExcludeEndpoint excludeEndpoint;

    @Autowired
    JwtTokenUtil jwt;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        String logServiceTrx = request.getHeader("LOG-SERVICE-TRX");

        List<String> logExcludes = excludeEndpoint.getAllExcludes();

        if (!logExcludes.stream().anyMatch(requestURI::startsWith)) {
            if (logServiceTrx == null || logServiceTrx.isEmpty()) {
                throw new MissingServletRequestParameterException("LOG-SERVICE-TRX", "Header");
            }
        }

        log.info("Receiver Service Log Transaction: {}", logServiceTrx);

        return true;
    }
}
