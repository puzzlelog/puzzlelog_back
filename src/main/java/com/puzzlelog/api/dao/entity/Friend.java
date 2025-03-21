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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY) // 참조 무한반복 방지
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", referencedColumnName = "user_id", nullable = false)
    private User friend;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";  // PENDING, ACCEPTED, DEACTIVATED, BLOCKED, REJECTED

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
