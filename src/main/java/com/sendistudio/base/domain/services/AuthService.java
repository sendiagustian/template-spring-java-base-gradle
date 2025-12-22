package com.sendistudio.base.domain.services;

import com.sendistudio.base.app.utils.JwtTokenUtil;
import com.sendistudio.base.data.requests.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;

    public String login(LoginRequest request) {
        // 1. Validasi Username & Password secara otomatis oleh Spring Security
        // Jika password salah, baris ini akan melempar Exception (BadCredentials)
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        System.out.println("Authenticated: " + authenticate.isAuthenticated());
        System.out.println("Username: " + authenticate.getName());
        System.out.println("Details: " + authenticate.getDetails());
        System.out.println("Authorities: " + authenticate.getAuthorities());
        System.out.println("Credentials: " + authenticate.getCredentials());

        // 2. Jika lolos (password benar), kita ambil data user-nya
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // 3. Generate Token Baru
        // Parameter kedua 'true' artinya set expired date
        return jwtTokenUtil.generateToken(userDetails.getUsername(), true);
    }
}