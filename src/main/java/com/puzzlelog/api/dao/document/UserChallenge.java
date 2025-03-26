package com.puzzlelog.api.dao.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

/**
 * 사용자의 챌린지 진행 현황을 저장하는 MongoDB Document입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user_challenges")
public class UserChallenge {

    /** MongoDB의 고유 식별자 */
    @Id
    private String id;

    /** 사용자 ID (MySQL의 users 테이블의 id 참조) */
    private Integer userId;

    /** 챌린지 ID (MySQL의 challenges 테이블의 id 참조) */
    private Integer challengeId;

    /** 챌린지 진행률 (예: 7일 목표 중 5일 출석한 경우 5로 기록) */
    @Builder.Default
    private Integer progress = 0;

    /** 챌린지 완료 여부 */
    @Builder.Default
    private Boolean isCompleted = false;

    /** 챌린지 완료 시간 (미완료 시 null) */
    private Instant completedAt;

    /** 마지막으로 챌린지 진행 상태가 업데이트된 시간 */
    private Instant lastUpdated;
}
