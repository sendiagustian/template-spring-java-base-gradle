package com.sendistudio.base.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Nanti jika sudah ada DB, di sini @Autowired UserRepository

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // LOGIKA MOCK (Jembatan Sementara)
        // pura-pura punya user bernama "dev" dengan password "password"
        // Note: Password harus di-encode nanti saat compare, tapi untuk User builder
        // ini spring yang urus

        if ("dev".equals(username)) {
            // Ini membuat object UserDetails bawaan Spring
            // Password "password" dalam bentuk BCrypt (hardcoded hash untuk "password")
            String passwordHash = passwordEncoder.encode("password");

            return new User("dev", passwordHash, new ArrayList<>());
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
        // TODO:
        // --- NANTI KALAU SUDAH ADA DB, GANTI JADI INI: ---
        // User user = userRepository.findByUsername(username)
        // .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }
}
