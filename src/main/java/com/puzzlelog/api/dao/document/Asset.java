package com.puzzlelog.api.dao.document;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Document(collection = "assets")
public class Asset {

    /** MongoDB Document 고유 식별자 */
    @Id
    private String id;

    /** 에셋의 이름 (예: "스마일 스티커", "바다 배경" 등) */
    private String name;

    /**
     * 에셋의 유형
     * 가능한 값:
     * - STICKER: 스티커 이미지
     * - BACKGROUND: 배경 이미지
     * - EMOTION: 감정 표현 이미지
     */
    private String type;

    /** Cloudinary에서 제공한 미디어 URL (접근 가능한 공개 URL) */
    private String mediaId;

    /** Cloudinary의 publicId (미디어 자산 관리에 사용, 옵션) */
    private String publicId;

    /** 에셋에 연관된 태그 목록 (옵션, 검색과 분류 목적으로 활용) */
    private List<String> tags;

    /** 에셋 삭제 여부 (논리 삭제, 기본값: false) */
    @Builder.Default
    private boolean deleted = false;
}
