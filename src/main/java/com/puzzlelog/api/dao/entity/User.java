package com.puzzlelog.api.dao.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user",
    indexes = {
        @Index(name = "gender_index", columnList = "gender"),
        @Index(name = "birth_date_index", columnList = "birth_date"),
        @Index(name = "status_index", columnList = "status"),
        @Index(name = "is_alarm_index", columnList = "is_alarm")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id"),
        @UniqueConstraint(columnNames = "email")
    }
)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 사용자 테이블의 PK (자동 증가 INT) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 사용자 고유 ID (로그인 시 사용, 중복 불가) */
    @Column(name = "user_id", nullable = false, length = 50, unique = true)
    private String userId;

    /** 사용자 비밀번호 (암호화 저장 권장, 최대 100자) */
    @Column(name = "user_pwd", length = 100)
    private String userPwd;

    /** 사용자 생년월일 (옵션) */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /** 성별 (MALE, FEMALE로 저장) */
    @Column(name = "gender", length = 10)
    private String gender;

    /** 사용자 닉네임 (옵션, 최대 50자) */
    @Column(name = "nickname", length = 50)
    private String nickname;

    /** 사용자 이메일 (중복 불가, 필수 입력) */
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    /** 알림 수신 여부 (기본값: true) */
    @Builder.Default
    @Column(name = "is_alarm", nullable = false)
    private Boolean isAlarm = true;

    /** 사용자 프로필 이미지 URL (TEXT 형식, Cloudinary URL 등 저장) */
    @Column(name = "profile_img", columnDefinition = "TEXT")
    private String profileImg;

    /** 생성일자 (레코드 생성 시 자동 입력) */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 수정일자 (레코드 업데이트 시 자동 변경) */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 사용자 상태 (ACTIVE, DELETED, BANNED, 기본값: ACTIVE) */
    @Builder.Default
    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    /** 마지막 로그인 일시 */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    /** 사용자 권한 (USER, ADMIN, 기본값: USER) */
    @Builder.Default
    @Column(name = "role", length = 20)
    private String role = "USER";

    /** 데이터 생성 전 처리 메서드 (자동으로 값 초기화) */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (isAlarm == null) isAlarm = true;
        if (status == null) status = "ACTIVE";
        if (role == null) role = "USER";
    }

    /** 데이터 수정 전 처리 메서드 (자동으로 updatedAt 업데이트) */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
