package com.sendistudio.base.domain.services;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sendistudio.base.app.handlers.exceptions.ConflictException;
import com.sendistudio.base.app.handlers.exceptions.EmailServiceException;
import com.sendistudio.base.app.handlers.exceptions.InternalServerException;
import com.sendistudio.base.app.helpers.IpHelper;
import com.sendistudio.base.app.helpers.MailHelper;
import com.sendistudio.base.app.helpers.UserDetailsHelper;
import com.sendistudio.base.app.utils.AppUtil;
import com.sendistudio.base.app.utils.JwtTokenUtil;
import com.sendistudio.base.data.requests.auth.*;
import com.sendistudio.base.data.responses.LoginResponse;
import com.sendistudio.base.data.requests.MailSendMessageRequest;
import com.sendistudio.base.domain.sources.ForgotPasswordTokenSource;
import com.sendistudio.base.domain.sources.RefreshTokenSource;
import com.sendistudio.base.domain.sources.UserSource;

import jakarta.servlet.http.HttpServletRequest;

import com.sendistudio.base.data.models.UserModel;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil.AccessToken jwtToken;
    private final JwtTokenUtil.RefreshToken jwtTokenRefresh;
    private final JwtTokenUtil.ForgotPssword jwtTokenForgotPassword;
    private final UserDetailsHelper userDetailsService;
    private final RefreshTokenSource refreshTokenSource;
    private final UserSource userSource;
    private final ForgotPasswordTokenSource forgotPasswordTokenSource;
    private final MailHelper mailHelper;
    private final PasswordEncoder passwordEncoder;
    private final IpHelper ipHelper;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            // 1. Validasi oleh Spring Security
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            // Di sini kita tangkap, lalu lempar ulang dengan pesan custom
            throw new BadCredentialsException("Invalid email or password");
        } catch (Exception e) {
            throw new RuntimeException("Error during login process: " + e.getMessage());
        }

        // 2. Jika lolos (password benar), kita ambil data user-nya
        UserModel user = (UserModel) userDetailsService.loadUserByUsername(request.getEmail());

        // 3. Generate Token Baru
        // Parameter kedua 'true' artinya set expired date
        Long tokenExpirationMillis = 1000 * 60 * 10L; // 10 minutes
        String token = jwtToken.generateToken(user.getUsername(), user.getId(), user.getTenantId(), user.getGlobalRole(), tokenExpirationMillis);

        Long refreshTokenExpirationMillis = 1000 * 60 * 60 * 24 * 30L; // 30 days (1 month)
        String refreshToken = jwtTokenRefresh.generateTokenRefresh(user.getUsername(), refreshTokenExpirationMillis);

        // Simpan refresh token ke database
        CreateRefreshTokenRequest refreshTokenRequest = new CreateRefreshTokenRequest();
        refreshTokenRequest.setToken(refreshToken);
        refreshTokenRequest.setUserId(user.getId());
        refreshTokenRequest.setExpiresAt(System.currentTimeMillis() + refreshTokenExpirationMillis);
        refreshTokenRequest.setIsRevoked(false);

        refreshTokenSource.createRefreshToken(refreshTokenRequest);

        // 4. Buat objek LoginResponse dan kembalikan
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setTenantId(user.getTenantId());
        response.setEmail(user.getEmail());
        response.setGlobalRole(user.getGlobalRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setStatus(user.getStatus());
        response.setAccessToken(token);
        response.setRefreshToken(refreshToken);
        return response;
    }

    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        // 1. Validasi token lama dan ambil username-nya
        String username = jwtTokenRefresh.extractUsernameRefresh(refreshToken).orElseThrow(
                () -> new BadCredentialsException("Invalid token"));

        Boolean isValid = refreshTokenSource.isRefreshTokenValid(refreshToken);
        if (!isValid) {
            throw new BadCredentialsException("Refresh token is invalid or expired");
        }

        // 2. Load data user berdasarkan username
        UserModel user = (UserModel) userDetailsService.loadUserByUsername(username);

        // 3. Generate token baru
        Long tokenExpirationMillis = 1000 * 60 * 10L; // 10 minutes
        String newToken = jwtToken.generateToken(user.getUsername(), user.getId(), user.getTenantId(),
                user.getGlobalRole(), tokenExpirationMillis);

        Long refreshTokenExpirationMillis = 1000 * 60 * 60 * 24 * 30L; // 30 days (1 month)
        String newRefreshToken = jwtTokenRefresh.generateTokenRefresh(
                user.getUsername(),
                refreshTokenExpirationMillis);

        // Simpan refresh token ke database
        CreateRefreshTokenRequest refreshTokenRequest = new CreateRefreshTokenRequest();
        refreshTokenRequest.setToken(newRefreshToken);
        refreshTokenRequest.setUserId(user.getId());
        refreshTokenRequest.setExpiresAt(System.currentTimeMillis() + refreshTokenExpirationMillis);
        refreshTokenRequest.setIsRevoked(false);
        refreshTokenSource.createRefreshToken(refreshTokenRequest);

        // Revoke refresh token lama
        refreshTokenSource.revokeRefreshToken(refreshToken);

        // 4. Buat objek LoginResponse dan kembalikan
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(user.getId());
        loginResponse.setTenantId(user.getTenantId());
        loginResponse.setEmail(user.getEmail());
        loginResponse.setGlobalRole(user.getGlobalRole());
        loginResponse.setCreatedAt(user.getCreatedAt());
        loginResponse.setUpdatedAt(user.getUpdatedAt());
        loginResponse.setAccessToken(newToken);
        loginResponse.setRefreshToken(newRefreshToken);
        return loginResponse;
    }

    @Transactional
    public Boolean forgotPassword(HttpServletRequest servletRequest, ForgotPasswordRequest request) {
        // 1. Cari user berdasarkan email
        UserModel user = userSource.getUserByEmail(request.getEmail()).orElseThrow(
                () -> new InternalServerException("Something went wrong"));

        // 2. Cek apakah sudah ada token reset password yang aktif untuk user ini
        if (forgotPasswordTokenSource.existsNotUsedAndNotExpiredTokenByUserId(user.getId())) {
            throw new ConflictException(
                    "There is already an active forgot password token for this user. Please check your email or try again later.");
        }

        // 3. Buat Token Unik
        Long expirationMillis = 1000L * 60 * 5; // 5 minutes
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expirationMillis / 1000);

        String token = jwtTokenForgotPassword.generateTokenForgotPassword(user.getUsername());
        Boolean isUpserted = forgotPasswordTokenSource.upsertToken(user.getId(), token, expiresAt, ipHelper.getClientIp(servletRequest));

        if (!isUpserted) {
            throw new RuntimeException("Failed to request forgot password. Please try again.");
        }

        // 3. Buat URL reset password dengan menyertakan token dan redirectUrl
        String resetUrl = request.getRedirectUrl() + "?token=" + token;

        // 4. Kirim email ke user dengan template reset password
        MailSendMessageRequest mailRequest = new MailSendMessageRequest();
        mailRequest.setTo(user.getEmail());
        mailRequest.setSubject("Password Reset Request");

        String companyName = "Sendi Studio";

        try {
            Boolean mailSent = mailHelper.sendEmailWithTemplate(
                    mailRequest,
                    "forgot-password",
                    Map.of(
                            "name", user.getEmail(),
                            "company_name", companyName,
                            "reset_url", resetUrl));

            if (!mailSent) {
                throw new EmailServiceException("Failed to send forgot password email. Please try again.");
            }

            return mailSent;
        } catch (Exception e) {
            throw new EmailServiceException("Error sending forgot password email: " + e.getMessage());
        }
    }

    @Transactional
    public Boolean resetPassword(ResetPasswordRequest request) {
        // 1. Validasi token dan ambil username
        String username = jwtTokenForgotPassword.extractUsernameForgotPassword(request.getToken()).orElseThrow(
                () -> new BadCredentialsException("Invalid token"));

        // 2. Load data user berdasarkan username
        UserModel user = (UserModel) userDetailsService.loadUserByUsername(username);

        // 3. Update password user
        String passwordHash = passwordEncoder.encode(request.getNewPassword());
        userSource.updatePassword(user.getId(), passwordHash);
        // Tandai token sebagai sudah digunakan
        Boolean isMarked = forgotPasswordTokenSource.markTokenAsUsed(request.getToken());
        return isMarked;
    }

    public Boolean logout() {
        // Ambil user dari SecurityContext (sudah di-set oleh JwtAuthenticationFilter)
        UserModel currentUser = AppUtil.getCurrentUser();

        String refreshToken = refreshTokenSource.getActiveRefreshTokenByUserId(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("Active refresh token not found for user"));

        // Revoke refresh token
        Boolean isRevoked = refreshTokenSource.revokeRefreshToken(refreshToken);
        return isRevoked;
    }

    public void me() {
        // TODO
    }


}