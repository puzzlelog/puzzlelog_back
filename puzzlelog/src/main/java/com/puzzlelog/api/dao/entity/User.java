package com.puzzlelog.api.dao.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "user_pwd", length = 100)
    private String userPwd;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "is_alarm", nullable = false)
    private Boolean isAlarm = true;

    @Column(name = "profile_img", columnDefinition = "TEXT")
    private String profileImg;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVE;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.USER;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        if (isAlarm == null) isAlarm = true;
        if (status == null) status = Status.ACTIVE;
        if (role == null) role = Role.USER;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Gender {
        MALE, FEMALE
    }

    public enum Status {
        ACTIVE, DELETED, BANNED
    }

    public enum Role {
        USER, ADMIN
    }
}