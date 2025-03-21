package com.puzzlelog.api.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;

@Component
public class JwtProvider {

    @Value("${jwt.secret:}")
    private String secretKey;

    @Value("${jwt.token-validity-in-seconds:1800}")
    private long tokenValidTime;

    private Key signingKey;

    private static final Set<String> VALID_ROLES = new HashSet<>(Set.of("USER", "ADMIN"));

    @PostConstruct
    public void init() {
        tokenValidTime = tokenValidTime * 1000L;
        System.out.println("Loaded JWT secret: " + secretKey);
        if (secretKey == null || secretKey.trim().isEmpty()) {
            SecureRandom secureRandom = new SecureRandom();
            byte[] keyBytes = new byte[32];
            secureRandom.nextBytes(keyBytes);
            secretKey = Base64.getEncoder().encodeToString(keyBytes);
            System.out.println("WARNING: No JWT secret provided. Generated random key: " + secretKey);
        }
        signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Integer id, String userId, String role) {
        if (!VALID_ROLES.contains(role)) {
            throw new IllegalArgumentException("Invalid role: " + role + ". Must be one of: " + VALID_ROLES);
        }

        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId)
                .claim("id", id)
                .claim("role", "ROLE_" + role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            System.out.println("JWT 검증 실패: " + e.getMessage());
            return false;
        }
    }

    public String getUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token)
                .getBody().getSubject();
    }

    public Integer getId(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token)
                .getBody().get("id", Integer.class);
    }

    public String getRole(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token)
                .getBody().get("role", String.class);
    }
}