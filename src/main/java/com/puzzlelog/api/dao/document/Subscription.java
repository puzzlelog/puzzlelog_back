package com.puzzlelog.api.dao.document;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

/**
 * 사용자의 구독(결제) 상태를 관리하는 MongoDB Document입니다.
 * 구독의 상태, 기간, 자동 갱신 여부 및 결제 정보를 포함합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "subscriptions")
public class Subscription {

    /** MongoDB Document 고유 식별자 */
    @Id
    private String id;

    /** 사용자 ID (MySQL users 테이블의 id 참조) */
    private Integer userId;

    /**
     * 구독 상태
     * 가능한 값: "active"(활성), "canceled"(취소됨), "expired"(만료됨)
     */
    private String status;

    /** 구독 시작 일자 (UTC 기준) */
    private Instant startDate;

    /** 구독 만료 일자 (UTC 기준) */
    private Instant endDate;

    /** 자동 갱신 여부 */
    @Builder.Default
    private Boolean autoRenew = true;

    /** 결제 정보 참조 (MongoDB payments 컬렉션의 Document 참조) */
    private String paymentId;

    /** 다음 결제 예정일 (UTC 기준, 자동 갱신 시 사용) */
    private Instant nextPaymentDate;

    /** 구독 정보 최초 생성 일자 (UTC 기준, 자동 저장) */
    @CreatedDate
    private Instant createdAt;
}
