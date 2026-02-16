package com.sendistudio.base.domain.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sendistudio.base.app.annotations.ApiTokenHeader;
import com.sendistudio.base.constants.ScalarTagConst;
import com.sendistudio.base.data.requests.auth.ForgotPasswordRequest;
import com.sendistudio.base.data.requests.auth.LoginRequest;
import com.sendistudio.base.data.requests.auth.RefreshTokenRequest;
import com.sendistudio.base.data.requests.auth.ResetPasswordRequest;
import com.sendistudio.base.data.responses.LoginResponse;
import com.sendistudio.base.data.responses.global.DataResponse;
import com.sendistudio.base.data.responses.global.WebResponse;
import com.sendistudio.base.domain.services.AuthService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = ScalarTagConst.AUTHENTICATION)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<WebResponse> login(@RequestBody LoginRequest request) {
        LoginResponse result = authService.login(request);

        DataResponse<LoginResponse> response = new DataResponse<>();
        response.setStatus(true);
        response.setData(result);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<WebResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        LoginResponse result = authService.refreshToken(request.getToken());

        DataResponse<LoginResponse> response = new DataResponse<>();
        response.setStatus(true);
        response.setData(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<WebResponse> forgotPassword(HttpServletRequest servletRequest, @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(servletRequest, request);

        DataResponse<String> response = new DataResponse<>();
        response.setStatus(true);
        response.setData("Forgot password email sent");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<WebResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);

        DataResponse<String> response = new DataResponse<>();
        response.setStatus(true);
        response.setData("Password reset successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @ApiTokenHeader
    public ResponseEntity<WebResponse> me() {
        authService.me();

        DataResponse<String> response = new DataResponse<>();
        response.setStatus(true);
        response.setData("User information retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @ApiTokenHeader
    public ResponseEntity<WebResponse> logout() {
        DataResponse<String> response = new DataResponse<>();
        response.setStatus(true);
        response.setData("Logged out successfully");
        return ResponseEntity.ok(response);
    }

}