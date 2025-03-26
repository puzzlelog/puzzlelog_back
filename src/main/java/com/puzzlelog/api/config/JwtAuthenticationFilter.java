package com.puzzlelog.api.config;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT 인증 필터 클래스입니다.
 * 요청의 헤더에서 JWT 토큰을 추출하고, 유효성을 검증한 후,
 * 인증 정보를 SecurityContext에 저장하여 Spring Security가 사용할 수 있도록 합니다.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /**
     * HTTP 요청에서 JWT를 추출하고 인증을 처리하는 메서드입니다.
     *
     * @param request     HttpServletRequest 객체
     * @param response    HttpServletResponse 객체
     * @param filterChain 필터 체인 객체
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        // Authorization 헤더에서 JWT 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // "Bearer " 이후 문자열 추출

            try {
                // JWT가 유효하면 인증 정보 생성
                if (jwtProvider.validateToken(token)) {
                    String userId = jwtProvider.getUserId(token);
                    String role = jwtProvider.getRole(token);

                    // 권한 생성 (ROLE_ 접두사 추가)
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + role)
                    );

                    // SecurityContext에 인증 정보 저장
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException ex) {
                // JWT가 유효하지 않으면 오류 응답 반환
                log.warn("JWT 인증 실패: {}", ex.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid JWT token\"}");
                return; // 필터 체인 진행 중단
            }
        }

        // JWT가 없거나 인증 완료 후, 다음 필터로 이동
        filterChain.doFilter(request, response);
    }
}
