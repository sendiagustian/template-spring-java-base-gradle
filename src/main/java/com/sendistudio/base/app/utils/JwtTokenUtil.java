package com.sendistudio.base.app.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.sendistudio.base.app.properties.DatabaseProperties;

@Component
@Getter
@Setter
@AllArgsConstructor
public class JwtTokenUtil {

    @Autowired
    DatabaseProperties databaseProperties;

    public SecretKey getSigningKey() {
        String SECRET_KEY = databaseProperties.getSecret();
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Generate JWT token
    public String generateToken(String username, Boolean setExpired) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, setExpired);
    }

    // Create JWT token
    public String createToken(Map<String, Object> claims, String subject, Boolean setExpired) {
        JwtBuilder builder = Jwts.builder();

        builder.claims().empty().add(claims).and();
        builder.subject(subject);

        if (setExpired) {
            builder.issuedAt(new Date(System.currentTimeMillis()));
            builder.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
            // builder.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 1));
        } else {
            builder.expiration(null);
        }

        builder.signWith(getSigningKey());

        return builder.compact();

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
        final Claims claims = extractAllClaims(token);
        return Optional.ofNullable(claimsResolver.apply(claims));
    }

    // Retrieve all claims from JWT token, including if the token is expired
    public Claims extractAllClaims(String token) {
        try {
            JwtParser parserBuilder = Jwts.parser().verifyWith(getSigningKey()).build();
            return parserBuilder.parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            // Token expired, but we can still extract claims
            return e.getClaims();
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
        return (username != null &&
                username.equals(userDetails.getUsername()) &&
                !isTokenExpired(token));
    }
}
