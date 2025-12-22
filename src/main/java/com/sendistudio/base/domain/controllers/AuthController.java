package com.sendistudio.base.domain.controllers;

import com.sendistudio.base.domain.services.AuthService;
import com.sendistudio.base.data.requests.LoginRequest;
import com.sendistudio.base.data.responses.DataResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Panggil service
        String token = authService.login(request);

        // Bungkus response
        DataResponse<String> data = new DataResponse<>();
        data.setStatus(true);
        data.setData(token);

        // Return JSON: { "token": "eyJhGcP..." }
        return ResponseEntity.ok(data);
    }
}