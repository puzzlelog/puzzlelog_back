package com.puzzlelog.api.dto.response.user;

import java.util.Map;

import lombok.*;

/**
 * 사용자 정보 수정 결과를 클라이언트에 전달하는 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateResponse {

    /** 수정 대상 사용자 ID */
    private String userId;

    /**
     * 실제 수정된 필드 목록과 각 필드의 이전/변경 값 정보
     * - key: 필드 이름
     * - value: UpdateField 객체 (before/after 값)
     */
    private Map<String, UpdateField> updatedFields;

    /** 프로필 이미지 변경 시 mediaId (Cloudinary ID 등) */
    private String mediaId;
    
    /** 수정 사유 (관리자 및 사용자) */
    private String reason;

    /**
     * 수정된 각 필드의 이전 값과 변경된 값을 담는 내부 클래스
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateField {
        private Object before;
        private Object after;
    }
}