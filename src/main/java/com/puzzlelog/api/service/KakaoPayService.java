package com.puzzlelog.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.puzzlelog.api.dao.entity.KakaoPaySubscription;
import com.puzzlelog.api.dto.request.kakaoPay.KakaoPayRequest;
import com.puzzlelog.api.dto.response.common.ApiResponse;
import com.puzzlelog.api.dto.response.kakaoPay.KakaoPayResponse;
import com.puzzlelog.api.repository.mysql.KakaoPaySubscriptionRepository;

@Service
public class KakaoPayService {

	@Autowired
	private KakaoPaySubscriptionRepository subscriptionRepository;
	
	@Autowired
	private AssetService assetService;
	
	@Autowired
	private UserService userService;
	
	private static final String ADMIN_KEY = "e9083cd8540bee4e366e0040e478ee46";
	private static final String CID = "TCSUBSCRIP";
	private static final String READY_URL = "https://kapi.kakao.com/v1/payment/ready";
	private static final String APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";
	private static final String SUBSCRIPTION_URL = "https://kapi.kakao.com/v1/payment/subscription";
	private static final String CANCEL_URL = "https://kapi.kakao.com/v1/payment/cancel";
	
	// 결제 준비 (Ready)
	public ApiResponse<KakaoPayResponse> initiateSubscription(KakaoPayRequest requestDTO) {
	    try {
	        RestTemplate restTemplate = new RestTemplate();
	        HttpHeaders headers = createHeaders();
	        MultiValueMap<String, String> params = createSubscriptionParams(requestDTO);

	        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
	        ResponseEntity<Map> response = restTemplate.postForEntity(READY_URL, request, Map.class);

	        Map<String, String> responseBody = response.getBody();          
	        String tid = responseBody.get("tid");
	        String nextRedirectUrl = responseBody.get("next_redirect_pc_url");

	        KakaoPaySubscription subscription = KakaoPaySubscription.builder()
	                .partnerOrderId(requestDTO.getPartnerOrderId())
	                .partnerUserId(requestDTO.getPartnerUserId())
	                .itemName(requestDTO.getItemName())
	                .totalAmount(requestDTO.getTotalAmount())
	                .tid(tid)  // TID 저장
	                .nextRedirectUrl(nextRedirectUrl)
	                .build();

	        subscriptionRepository.save(subscription);
	        
	        KakaoPayResponse responseDTO = KakaoPayResponse.builder()
	                .nextRedirectUrl(nextRedirectUrl)
	                .tid(tid)
	                .build();
	        
	        return ApiResponse.success(responseDTO, "결제 준비 성공");

	    } catch (Exception e) {
	        return ApiResponse.fail("카카오페이 결제 준비 실패: " + e.getMessage());
	    }
	}

	// 결제 승인 (Approve)
	public ApiResponse<String> approvePayment(String pgToken, String partnerOrderId, String partnerUserId) {
	    try {
	        RestTemplate restTemplate = new RestTemplate();
	        HttpHeaders headers = createHeaders();

	        // partnerOrderId를 이용하여 DB에서 TID 조회 (중복 허용)
	        List<KakaoPaySubscription> subscriptions = subscriptionRepository.findAllByPartnerOrderId(partnerOrderId);
	        if (subscriptions == null || subscriptions.isEmpty()) {
	            return ApiResponse.fail("해당 주문 ID로 조회된 TID가 없습니다.");
	        }

	        // 중복이 있을 경우 가장 최근 데이터 사용
	        KakaoPaySubscription subscription = subscriptions.get(subscriptions.size() - 1);
	        String tid = subscription.getTid();

	        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	        params.add("cid", CID);
	        params.add("tid", tid);
	        params.add("partner_order_id", partnerOrderId);
	        params.add("partner_user_id", partnerUserId);
	        params.add("pg_token", pgToken);

	        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
	        ResponseEntity<Map> response = restTemplate.postForEntity(APPROVE_URL, request, Map.class);

	        // 결제 승인 응답 확인
	        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
	        	Map<String, Object> responseBody = response.getBody();
	        	
	        	// 정기 결제 키(SID) 확인
	        	String sid = (String) responseBody.get("sid");
	        	if (sid == null) {
	        		return ApiResponse.fail("정기 결제 키(SID)가 응답에 없습니다.");
	        	}
	        	
	        	// 정기 결제 키를 DB에 저장
	        	subscription.setBillingKey(sid);
	        	subscription.setUpdatedAt(LocalDateTime.now());
	        	subscriptionRepository.save(subscription);
	        	
	        	// 구독 상태를 'ACTIVE'로 업데이트
	        	userService.updateSubscriptionStatus(partnerUserId, "ACTIVE");
	        	
	        	// 유료 스티커 잠금 해제
	        	assetService.unlockAllPaidStickers();
	        	
	        	return ApiResponse.successMessage("결제 승인 및 구독 활성화 성공");
	        } else {
	        	// 승인 실패 시 로그 및 상태 처리
	        	String errorMessage = "카카오페이 승인 실패 : " + response.getBody();
	        	return ApiResponse.fail(errorMessage);
	        }

	    } catch (Exception e) {
	        return ApiResponse.fail("카카오페이 결제 승인 중 오류 : " + e.getMessage());
	    }
	}

	public ApiResponse<String> executeSubscription(String billingKey, String partnerOrderId, String partnerUserId, int amount) {
	    try {
	        RestTemplate restTemplate = new RestTemplate();
	        HttpHeaders headers = createHeaders();

	        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	        params.add("cid", CID);
	        params.add("sid", billingKey);
	        params.add("partner_order_id", partnerOrderId);
	        params.add("partner_user_id", partnerUserId);
	        params.add("item_name", "Monthly Subscription");
	        params.add("quantity", "1");
	        params.add("total_amount", String.valueOf(amount));
	        params.add("tax_free_amount", "0");

	        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
	        restTemplate.postForEntity(SUBSCRIPTION_URL, request, Map.class);
	        
	        // 결제 성공 시 updatedAt 갱신
	        KakaoPaySubscription subscription = subscriptionRepository.findByPartnerOrderId(partnerOrderId);
	        if (subscription != null) {
	        	subscription.setUpdatedAt(LocalDateTime.now());
	        	subscriptionRepository.save(subscription);
	        }

	        return ApiResponse.successMessage("정기 결제 실행 성공");
	    } catch (Exception e) {
	        return ApiResponse.fail("카카오페이 정기 결제 실행 실패: " + e.getMessage());
	    }
	}

    // 정기 결제 취소 (Cancel)
    public ApiResponse<String> cancelSubscription(String tid, int amount) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeaders();

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("cid", CID);
            params.add("tid", tid);
            params.add("cancel_amount", String.valueOf(amount));
            params.add("cancel_tax_free_amount", "0");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            restTemplate.postForEntity(CANCEL_URL, request, Map.class);

            return ApiResponse.success("정기 결제 취소 성공");
        } catch (Exception e) {
            return ApiResponse.fail("카카오페이 결제 취소 실패: " + e.getMessage());
        }
    }

    // 공통 헤더
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + ADMIN_KEY);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        return headers;
    }

    // 결제 준비 파라미터 생성
    private MultiValueMap<String, String> createSubscriptionParams(KakaoPayRequest requestDTO) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", CID);
        params.add("partner_order_id", requestDTO.getPartnerOrderId());
        params.add("partner_user_id", requestDTO.getPartnerUserId());
        params.add("item_name", requestDTO.getItemName());
        params.add("quantity", "1");
        params.add("total_amount", String.valueOf(requestDTO.getTotalAmount()));
        params.add("tax_free_amount", "0");
        
        // 승인 URL에 파트너 정보 포함하여 전달
        String backendUrl = "http://localhost:8080/subscription/approve";
        params.add("approval_url", backendUrl + "?partnerOrderId=" + requestDTO.getPartnerOrderId()
                                      + "&partnerUserId=" + requestDTO.getPartnerUserId());
        params.add("cancel_url", "http://localhost:3000/payment/result?status=cancel");
        params.add("fail_url", "http://localhost:3000/payment/result?status=fail");

        return params;
    }

    public List<KakaoPaySubscription> getAllSubscriptions() {
    	return subscriptionRepository.findAll();
    }
    
    public void saveSubscription(KakaoPaySubscription subscription) {
    	subscriptionRepository.save(subscription);
    }
}
