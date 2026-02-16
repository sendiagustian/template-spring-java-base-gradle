package com.sendistudio.base.app.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.sendistudio.base.app.properties.DatabaseProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@RequiredArgsConstructor
public class JwtTokenUtil {
    private final DatabaseProperties databaseProperties;

    // Retrieve all claims from JWT token, including if the token is expired
    public Claims extractAllClaims(String token, SecretKey signingKey) {
        try {
            JwtParser parserBuilder = Jwts.parser().verifyWith(signingKey).build();
            return parserBuilder.parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            // Token expired, but we can still extract claims
            return e.getClaims();
        }
    }

    @RequiredArgsConstructor
    public static class AccessToken {
        private final JwtTokenUtil jwtTokenUtil;
        private final DatabaseProperties databaseProperties;

        public SecretKey getSigningKey() {
            String SECRET_KEY = databaseProperties.getSecretToken();
            return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        }

        // Create JWT token
        public String createToken(Map<String, Object> claims, String subject, Long expirationMillis) {
            JwtBuilder builder = Jwts.builder();

            builder.claims().empty().add(claims).and();
            builder.subject(subject);

            if (expirationMillis != null) {
                builder.issuedAt(new Date(System.currentTimeMillis()));
                builder.expiration(new Date(System.currentTimeMillis() + expirationMillis));
            } else {
                builder.expiration(null);
            }

            builder.signWith(getSigningKey());

            return builder.compact();

        }

        // Generate JWT token
        public String generateToken(String username, String userId, String tenantId, String role, Long expirationMillis) {
            Map<String, Object> claims = new HashMap<>();

            // Tambahkan klaim khusus sesuai kebutuhan, misalnya userId, tenantId, role, dll.
            claims.put("userId", userId);
            claims.put("tenantId", tenantId);
            claims.put("role", role);
            return createToken(claims, username, expirationMillis);
        }

        /**
         * Extract Tenant ID dari Token
         */
        public Optional<String> extractTenantId(String token) {
            try {
                return extractClaim(token, claims -> claims.get("tenantId", String.class));
            } catch (Exception e) {
                return Optional.empty();
            }
        }
        
        /**
         * Extract User ID dari Token
         */
        public Optional<String> extractUserId(String token) {
            try {
                return extractClaim(token, claims -> claims.get("userId", String.class));
            } catch (Exception e) {
                return Optional.empty();
            }
        }

        // Retrieve expiration date from JWT token
        public Optional<Date> extractExpiration(String token) {
            try {
                return extractClaim(token, Claims::getExpiration);
            } catch (ExpiredJwtException e) {
                // Jika token sudah expired, ambil klaim dari token yang sudah expired
                return Optional.ofNullable(e.getClaims().getExpiration());
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }

        public <T> Optional<T> extractClaim(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = jwtTokenUtil.extractAllClaims(token, getSigningKey());
            return Optional.ofNullable(claimsResolver.apply(claims));
        }

        // Retrieve username from JWT token
        public Optional<String> extractUsername(String token) {
            try {
                return extractClaim(token, Claims::getSubject);
            } catch (ExpiredJwtException e) {
                // Jika token sudah expired, ambil klaim dari token yang sudah expired
                return Optional.ofNullable(e.getClaims().getSubject());
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }

        // Check if the token has expired
        public Boolean isTokenExpired(String token) {
            Date date = extractExpiration(token).orElse(null);
            if (date != null) {
                return date.before(new Date());
            }
            return false;
        }

        // Validate token
        public Boolean validateToken(String token, String username) {
            final Optional<String> extractedUsername = extractUsername(token);
            if (extractedUsername.orElse(null).equals(username)) {
                return true;
            } else {
                return false;
            }
        }

        // Validasi token lengkap untuk Spring Security Filter
        public boolean isTokenValid(String token, UserDetails userDetails) {
            // 1. Ambil username dari token
            final String username = extractUsername(token).orElse(null);

            // 2. Cek apakah username valid dan token TIDAK expired
            return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        }
    }

    @RequiredArgsConstructor
    public static class RefreshToken {
        private final JwtTokenUtil jwtTokenUtil;
        private final DatabaseProperties databaseProperties;

        public SecretKey getSigningRefreshKey() {
            String SECRET_KEY = databaseProperties.getSecretRefresh();
            return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        }

        public String generateTokenRefresh(String username, Long expirationMillis) {
            Map<String, Object> claims = new HashMap<>();
            return createTokenRefresh(claims, username, expirationMillis);
        }

        public String createTokenRefresh(Map<String, Object> claims, String subject, Long expirationMillis) {
            JwtBuilder builder = Jwts.builder();

            builder.claims().empty().add(claims).and();
            builder.subject(subject);

            if (expirationMillis != null) {
                builder.issuedAt(new Date(System.currentTimeMillis()));
                builder.expiration(new Date(System.currentTimeMillis() + expirationMillis));
                // builder.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 1));
            } else {
                builder.expiration(null);
            }

            builder.signWith(getSigningRefreshKey());

            return builder.compact();

        }

        // Retrieve username from JWT token
        public Optional<String> extractUsernameRefresh(String token) {
            try {
                return extractClaimRefresh(token, Claims::getSubject);
            } catch (ExpiredJwtException e) {
                // Jika token sudah expired, ambil klaim dari token yang sudah expired
                return Optional.ofNullable(e.getClaims().getSubject());
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }

        public <T> Optional<T> extractClaimRefresh(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = jwtTokenUtil.extractAllClaims(token, getSigningRefreshKey());
            return Optional.ofNullable(claimsResolver.apply(claims));
        }

    }

    @RequiredArgsConstructor
    public static class ForgotPssword {
        private final JwtTokenUtil jwtTokenUtil;
        private final DatabaseProperties databaseProperties;

        public SecretKey getSigningForgotPasswordKey() {
            String SECRET_KEY = databaseProperties.getSecretForgotPassword();
            return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        }

        public String generateTokenForgotPassword(String username) {
            Map<String, Object> claims = new HashMap<>();
            Long expirationMillis = 1000L * 60 * 5; // 5 minutes
            return createTokenForgotPassword(claims, username, expirationMillis);
        }

        public String createTokenForgotPassword(Map<String, Object> claims, String subject, Long expirationMillis) {
            JwtBuilder builder = Jwts.builder();

            builder.claims().empty().add(claims).and();
            builder.subject(subject);

            if (expirationMillis != null) {
                builder.issuedAt(new Date(System.currentTimeMillis()));
                builder.expiration(new Date(System.currentTimeMillis() + expirationMillis));
                // builder.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 1));
            } else {
                builder.expiration(null);
            }

            builder.signWith(getSigningForgotPasswordKey());

            return builder.compact();

        }

        // Retrieve specific claim from JWT token
        public <T> Optional<T> extractClaimForgotPassword(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = jwtTokenUtil.extractAllClaims(token, getSigningForgotPasswordKey());
            return Optional.ofNullable(claimsResolver.apply(claims));
        }

        // Retrieve username from JWT token
        public Optional<String> extractUsernameForgotPassword(String token) {
            try {
                return extractClaimForgotPassword(token, Claims::getSubject);
            } catch (ExpiredJwtException e) {
                // Jika token sudah expired, ambil klaim dari token yang sudah expired
                return Optional.ofNullable(e.getClaims().getSubject());
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }

        public Boolean isValidTokenForgotPassword(String token, String username) {
            final Optional<String> extractedUsername = extractUsernameForgotPassword(token);
            if (extractedUsername.orElse(null).equals(username)) {
                return true;
            } else {
                return false;
            }
        }
    }
}
