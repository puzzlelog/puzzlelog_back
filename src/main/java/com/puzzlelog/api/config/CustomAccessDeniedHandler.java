package com.puzzlelog.api.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puzzlelog.api.dto.response.common.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("접근 거부: {}", accessDeniedException.getMessage());
        
        log.error("🔥🔥🔥 CustomAccessDeniedHandler 호출됨!");

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        // 컨트롤러에서 던진 메시지를 반영
        String message = accessDeniedException.getMessage() != null 
                ? accessDeniedException.getMessage() 
                : "접근이 거부되었습니다.";
        ApiResponse<Void> errorResponse = ApiResponse.fail(message);
        String json = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(json);
    }
}