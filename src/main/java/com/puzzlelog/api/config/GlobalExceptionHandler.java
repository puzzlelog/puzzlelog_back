package com.puzzlelog.api.config;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.puzzlelog.api.dto.response.common.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("파라미터 타입 오류: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail("파라미터 '" + e.getName() + "'의 값이 잘못되었습니다."));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameter(MissingServletRequestParameterException e) {
        log.warn("필수 파라미터 누락: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail("필수 요청 파라미터('" + e.getParameterName() + "')가 없습니다."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidFormat(HttpMessageNotReadableException e) {
        log.warn("요청 본문 형식 오류: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail("요청 본문의 형식이 잘못되었습니다."));
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknownProperty(UnrecognizedPropertyException e) {
        log.warn("허용되지 않은 필드 존재: {}", e.getPropertyName());
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail("허용되지 않은 파라미터가 있습니다: " + e.getPropertyName()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFound(NoHandlerFoundException e) {
        log.warn("존재하지 않는 경로 접근: {}", e.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail("존재하지 않는 API 경로입니다."));
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomAccessDenied(CustomAccessDeniedException e) {
        log.warn("접근 거부 (비즈니스 로직): {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("서버 내부 예외 발생: {}", e.getMessage(), e);
        Map<String, Object> errorDetail = Map.of(
                "error", e.getClass().getSimpleName(),
                "message", e.getMessage(),
                "location", e.getStackTrace()[0].toString()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("서버 내부 오류가 발생했습니다: " + errorDetail));
    }
}