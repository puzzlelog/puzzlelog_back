package com.puzzlelog.api.config;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security 설정 클래스입니다.
 * JWT 인증 필터, 비밀번호 인코딩, CORS 및 접근 권한 등을 설정합니다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * 비밀번호 인코더 (BCrypt 사용)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 정적 리소스에 대해서는 보안 필터를 적용하지 않도록 제외 처리
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                "/resources/**",
                "/static/**",
                "/css/**",
                "/js/**",
                "/images/**",
                "/**/*.ico",
                "/**/*.png"
        );
    }

    /**
     * JWT 인증, 접근 제한 및 HTTP 보안 관련 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS 구성 적용
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // REST API에서는 CSRF 보호 기능을 비활성화
            .csrf(csrf -> csrf.disable())
            
            // JWT 사용을 위해 세션을 STATELESS로 설정
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // URL에 따른 접근 권한 설정
            .authorizeHttpRequests(authz -> authz
            	    .antMatchers(HttpMethod.GET, "/").permitAll()
            	    .antMatchers("/auth/**").permitAll() // auth는 모두 허용
            	    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            	    .anyRequest().authenticated()
            	)
            
            // 예외 에러 설정
            .exceptionHandling(exception ->
		            exception.accessDeniedHandler(customAccessDeniedHandler)
		    )

            // X-Frame-Options 헤더를 SAMEORIGIN으로 설정 (iframe 내에서 접근 가능)
            .headers(headers -> headers.frameOptions().sameOrigin())

            // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS (Cross-Origin Resource Sharing) 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 출처(도메인) 설정
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://puzzlelog.me",
            "https://puzzlelog.me",
            "null"
        ));

        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"
        ));

        // 모든 헤더 허용 (필요 시 특정 헤더만 설정 가능)
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 쿠키 및 인증 정보 전송 허용
        configuration.setAllowCredentials(true);

        // Preflight 요청의 캐시 시간 (1시간)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
