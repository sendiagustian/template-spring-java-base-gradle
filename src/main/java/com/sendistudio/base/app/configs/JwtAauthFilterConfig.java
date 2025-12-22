package com.sendistudio.base.app.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.sendistudio.base.app.utils.JwtTokenUtil;

import java.io.IOException;

/*
    * JWT Authentication Filter Configuration Class
*/
@Component
public class JwtAauthFilterConfig extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String header = request.getHeader("X-API-TOKEN");
        final String username;

        // 1. Cek Header
        if (header == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract Username dari Token (Tanpa ke DB)
        // Pastikan logic extractClaim handle error/expired token
        try {
            username = jwtUtil.extractClaim(header, claims -> claims.get("sub", String.class)).orElse(null);
        } catch (Exception e) {
            // Token invalid/expired, biarkan lanjut, nanti ditolak oleh SecurityConfig
            // karena context kosong
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Masukkan ke Security Context jika belum ada
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validasi token (bisa cek DB atau signature dll)
            if (jwtUtil.isTokenValid(header, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Memberi tahu Spring Security siapa yang login
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}