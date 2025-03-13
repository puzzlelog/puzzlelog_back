package com.puzzlelog.api.config;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(e.getMessage()));
    }

    // 파라미터 타입 오류 처리
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail("파라미터 '" + e.getName() + "'의 값이 잘못되었습니다."));
    }

    // 필수 파라미터 누락 처리
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameter(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail("필수 요청 파라미터('" + e.getParameterName() + "')가 없습니다."));
    }

    // JSON 형식 오류 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidFormat(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail("요청 본문의 형식이 잘못되었습니다."));
    }

    // 허용되지 않은 필드 오류 처리
    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknownProperty(UnrecognizedPropertyException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail("허용되지 않은 파라미터가 있습니다: " + e.getPropertyName()));
    }

    // 없는 API 경로 접근 처리
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFound(NoHandlerFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail("존재하지 않는 API 경로입니다."));
    }

    // 서버 내부 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("서버 내부 오류가 발생했습니다."));
    }
}
