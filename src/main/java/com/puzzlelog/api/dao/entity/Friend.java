package com.puzzlelog.api.dao.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "friend",
       indexes = {
           @Index(name = "user_id_index", columnList = "user_id"),
           @Index(name = "friend_id_index", columnList = "friend_id"),
           @Index(name = "status_index", columnList = "status")
       })
public class Friend {

    /** 친구 관계 엔티티의 기본 키(PK, 자동 증가) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 친구 요청을 보낸 사용자 (소유자) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    /** 친구 요청을 받은 대상 사용자 (상대방) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", referencedColumnName = "user_id", nullable = false)
    private User friend;

    /**
     * 친구 관계 상태 (기본값: PENDING)
     *
     * 가능한 값:
     * - PENDING: 요청 대기중
     * - ACCEPTED: 수락된 친구 관계
     * - DEACTIVATED: 삭제된 친구 관계
     * - BLOCKED: 차단된 관계
     * - REJECTED: 거절된 친구 관계
     */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    /** 친구 요청 생성일자 (처음 요청한 시점) */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 친구 관계 최종 변경 일자 (상태 변경 등) */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 엔티티가 처음 저장될 때 생성시간, 업데이트 시간 자동 초기화 */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }

    /** 엔티티가 수정될 때 업데이트 시간 자동 갱신 */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
