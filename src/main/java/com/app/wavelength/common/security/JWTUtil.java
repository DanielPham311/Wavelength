package com.app.wavelength.common.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {
    private final SecretKey secretKey;
    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;

    public JWTUtil(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access-token-expiry}") long accessTokenExpiry,
        @Value("${jwt.refresh-token-expiry}") long refreshTokenExpiry
    ) 
    {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public String generateAccessToken(UUID userID, String email, String role) {
        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(userID.toString())
            .claim("email", email)
            .claim("role", role)
            .claim("type", "access")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + accessTokenExpiry))
            .signWith(secretKey)
            .compact();
    }

    public String generateRefreshToken(UUID userID) {
        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(userID.toString())
            .claim("type", "refresh")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiry))
            .signWith(secretKey)
            .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public UUID extractUserID(String token) {
        return UUID.fromString(extractClaims(token).getSubject());
    }

    public String extractEmail(String token) {
        return extractClaims(token).get("email", String.class);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public String extractJti(String token) {
        return extractClaims(token).getId();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            return "access".equals(extractClaims(token).get("type", String.class));
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            return "refresh".equals(extractClaims(token).get("type", String.class));
        } catch (JwtException e) {
            return false;
        }
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }
}
