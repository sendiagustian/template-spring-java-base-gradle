package com.sendistudio.base.app.handlers;

import com.sendistudio.base.data.responses.WebResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * Custom AuthenticationEntryPoint untuk menangani akses tidak sah (401 Unauthorized)
 * dan mengembalikan response JSON standar.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        // 1. Set Header Response agar dikenali sebagai JSON
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401

        // 2. Gunakan Class WebResponse standar
        // Status: false, Messages: pesan error custom
        WebResponse WebResponse = new WebResponse(false, "Access Denied: Invalid or missing token.");

        // 3. Tulis object Java menjadi JSON String
        String jsonResponse = objectMapper.writeValueAsString(WebResponse);

        // 4. Kirim ke client
        response.getWriter().write(jsonResponse);
    }
}