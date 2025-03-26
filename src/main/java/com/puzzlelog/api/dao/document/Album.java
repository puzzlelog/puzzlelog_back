package com.puzzlelog.api.dao.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자가 작성한 여러 일기를 하나의 그룹으로 묶어 저장하고 관리하는 앨범(Album) Document.
 * 여러 개의 Diary(일기)를 포함할 수 있으며, 앨범 단위로 공유 및 구매가 가능합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "albums")
public class Album {

    /** MongoDB Document의 고유 식별자 */
    @Id
    private String id;

    /** 앨범을 만든 사용자의 ID */
    private String userId;

    /** 앨범의 제목 */
    private String title;

    /** 앨범에 포함된 일기(Diary)들의 ID 목록 */
    private List<String> diaryId;

    /** 앨범의 구매 여부 (기본값: false) */
    @Builder.Default
    private boolean purchased = false;

    /** 앨범의 삭제 여부 (논리 삭제, 기본값: false) */
    @Builder.Default
    private boolean deleted = false;

    /** 앨범 생성 시각 (자동 저장) */
    @CreatedDate
    private Date createdAt;
}
