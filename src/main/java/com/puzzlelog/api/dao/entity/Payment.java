package com.puzzlelog.api.dao.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 사용자 결제 내역을 관리하는 엔티티입니다.
 * 결제 수단, 금액, 상태 및 연관된 구독 정보를 저장합니다.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_payment_method", columnList = "payment_method"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_subscription_id", columnList = "subscription_id"),
    @Index(name = "idx_transaction_id", columnList = "transaction_id")
})
public class Payment {

    /** 결제의 고유 ID (자동 생성) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 사용자 ID (user 테이블의 id를 참조) */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** 결제 금액 */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /** 통화 코드 (예: KRW, USD, JPY 등) */
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    /**
     * 결제 수단
     * 가능한 값: "card"(카드), "paypal"(페이팔), "bank_transfer"(계좌이체)
     */
    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;

    /**
     * 결제 상태
     * 가능한 값: "pending"(대기), "completed"(완료), "failed"(실패)
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /** 관련 구독 ID (subscriptions 컬렉션의 ID 참조, Optional) */
    @Column(name = "subscription_id", length = 50)
    private String subscriptionId;

    /** 결제 트랜잭션 ID (결제 대행사에서 발급된 ID) */
    @Column(name = "transaction_id", nullable = false, length = 50, unique = true)
    private String transactionId;

    /** 결제 생성 일시 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 결제 생성 시점 자동 설정 */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
