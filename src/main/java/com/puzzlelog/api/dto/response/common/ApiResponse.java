package com.puzzlelog.api.dto.response.common;

import lombok.*;

/** 공통 응답 객체 (API Response Wrapper)
 * 
 * @param success : 성공 여부 (true/false)
 * @param message : 응답 메시지
 * @param data : 실제 데이터
 * 
 * @param <T> : Response 데이터 객체
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // 성공 응답 (데이터와 메시지 포함)
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    // 성공 응답 (데이터만 포함)
    public static <T> ApiResponse<T> success(T data) {
        return success(data, null);
    }

    // 성공 응답 (메시지만 포함)
    public static <T> ApiResponse<T> successMessage(String message) {
        return success(null, message);
    }

    // 실패 응답 (메시지 포함)
    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
}