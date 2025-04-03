package com.puzzlelog.api.repository.mysql;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.puzzlelog.api.dao.entity.KakaoPaySubscription;

public interface KakaoPaySubscriptionRepository extends JpaRepository<KakaoPaySubscription, Long> {
	KakaoPaySubscription findByPartnerOrderId (String partnerOrderId);
	// TID로 구독 정보 조회
	KakaoPaySubscription findByTid(String tid);
	List<KakaoPaySubscription> findAllByPartnerOrderId(String partnerOrderId);
}
