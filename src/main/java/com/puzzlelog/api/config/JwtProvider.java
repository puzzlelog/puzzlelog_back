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

/**
 * JWT 생성 및 검증을 담당하는 클래스
 */
@Component
public class JwtProvider {

    @Value("${jwt.secret:}")
    private String secretKey;

    @Value("${jwt.token-validity-in-seconds:1800}")
    private long tokenValidTime;

    private Key signingKey;

    private static final Set<String> VALID_ROLES = new HashSet<>(Set.of("USER", "ADMIN"));

    /**
     * 빈 생성 후 JWT 서명 키를 초기화합니다.
     * secretKey가 없을 경우 무작위 키를 생성합니다.
     */
    @PostConstruct
    public void init() {
        tokenValidTime = tokenValidTime * 1000L;

        if (secretKey == null || secretKey.trim().isEmpty()) {
            SecureRandom secureRandom = new SecureRandom();
            byte[] keyBytes = new byte[32];
            secureRandom.nextBytes(keyBytes);
            secretKey = Base64.getEncoder().encodeToString(keyBytes);
            // 실제 운영환경에서는 랜덤 키 사용을 피하고 명시적 키를 설정해야 합니다.
            System.out.println("⚠️ WARNING: JWT secret 미설정. 임의의 secret 키를 생성했습니다.");
        }

        signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT 토큰을 생성합니다.
     *
     * @param id 사용자 고유 번호 (DB의 PK)
     * @param userId 사용자 로그인 ID
     * @param role 사용자 권한 (USER, ADMIN 중 하나)
     * @return 생성된 JWT 문자열
     */
    public String createToken(Integer id, String userId, String role) {
        if (!VALID_ROLES.contains(role)) {
            throw new IllegalArgumentException("잘못된 역할(role)입니다: " + role + ". 가능한 역할: " + VALID_ROLES);
        }

        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId) // getName()으로 들어가는 값
                .claim("id", id)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(signingKey)
                .compact();
    }

    /**
     * JWT 토큰이 유효한지 검증합니다.
     *
     * @param token JWT 문자열
     * @return 검증 결과 (true: 유효, false: 유효하지 않음)
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            System.out.println("❌ JWT 검증 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * JWT 토큰에서 사용자 로그인 ID(userId)를 추출합니다.
     *
     * @param token JWT 문자열
     * @return 사용자 로그인 ID
     */
    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * JWT 토큰에서 사용자 고유 번호(id)를 추출합니다.
     *
     * @param token JWT 문자열
     * @return 사용자 고유 번호 (Integer)
     */
    public Integer getId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("id", Integer.class);
    }

    /**
     * JWT 토큰에서 사용자 권한(role)을 추출합니다.
     *
     * @param token JWT 문자열
     * @return 사용자 권한 (String)
     */
    public String getRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
}
