package com.puzzlelog.api.dao.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // DB: INT

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "user_pwd", length = 100)
    private String userPwd;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender", length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Builder.Default
    @Column(name = "is_alarm", nullable = false)
    private Boolean isAlarm = true;

    @Column(name = "profile_img", columnDefinition = "TEXT")
    private String profileImg;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Builder.Default
    @Column(name = "role", length = 20)
    @Enumerated(EnumType.STRING)
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