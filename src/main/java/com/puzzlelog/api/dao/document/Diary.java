package com.puzzlelog.api.dao.document;

import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "diaries")
public class Diary {

    /** MongoDB Document의 고유 식별자 */
    @Id
    private String id;

    /** 일기 작성자의 사용자 ID */
    private String userId;

    /** 일기의 제목 */
    private String title;

    /**
     * 일기 내부 요소(텍스트, 이미지, 영상 등)의 ID 리스트
     * DiaryElement 컬렉션의 id를 참조하며, 배열 순서대로 앞에 있는 요소가 가장 아래에 위치하며,
     * 뒤에 있을수록 위에 쌓이는 형태로 렌더링됩니다.
     */
    private List<String> elementIds;

    /** 일기의 배경 이미지 콘텐츠 ID (null 가능) */
    private String backgroundContentId;

    /** 일기의 테마 색상 (Hex 코드, RGB 등) */
    private String themeColor;

    /** 감정 상태를 나타내는 콘텐츠 ID (이모티콘, 이미지 등, null 가능) */
    private String emotionContentId;

    /** 일기의 공유 상태 (기본값: false, true일 경우 커뮤니티에 공개됨) */
    @Builder.Default
    private boolean shared = false;

    /** 일기 생성 시점 (자동 저장) */
    @CreatedDate
    private Instant createdAt;

    /** 일기 최종 수정 시점 */
    private Instant updatedAt;

    /** 일기의 삭제 여부 (논리 삭제, 기본값: false) */
    @Builder.Default
    private boolean deleted = false;

    /**
     * 타임캡슐 기능을 위한 필드로, 해당 시간이 되면 사용자가 접근할 수 있음.
     * (null일 경우 타임캡슐 기능 미사용)
     */
    private Instant openAt;
    
    
    //협업일기 참여자ㅏ
    private List<String> participants;
}
