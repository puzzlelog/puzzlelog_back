package com.puzzlelog.api.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.puzzlelog.api.dto.response.common.ApiResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 글로벌 ObjectMapper 및 Security 관련 설정
 */
@Configuration
@Slf4j
public class SecuritySupportConfig {

    /**
     * 글로벌 ObjectMapper 설정
     * - 알 수 없는 필드 존재 시 예외 발생
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601로 직렬화
        return objectMapper;
    }

    /**
     * 커스텀 AccessDeniedHandler 설정
     */
    @Bean
    public AccessDeniedHandler customAccessDeniedHandler(ObjectMapper objectMapper) {
        return (HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) -> {
            log.warn("접근 거부: {}", ex.getMessage());
            log.error("🔥🔥🔥 CustomAccessDeniedHandler 호출됨!");

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");

            String message = ex.getMessage() != null ? ex.getMessage() : "접근이 거부되었습니다.";
            ApiResponse<Void> errorResponse = ApiResponse.fail(message);
            String json = objectMapper.writeValueAsString(errorResponse);

            response.getWriter().write(json);
        };
    }
}
