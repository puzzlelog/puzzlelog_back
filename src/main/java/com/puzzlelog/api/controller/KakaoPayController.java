package com.puzzlelog.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.puzzlelog.api.dto.request.kakaoPay.KakaoPayRequest;
import com.puzzlelog.api.dto.response.common.ApiResponse;
import com.puzzlelog.api.dto.response.kakaoPay.KakaoPayResponse;
import com.puzzlelog.api.service.KakaoPayService;

@RestController
@RequestMapping("/subscription")
public class KakaoPayController {

	@Autowired
	private KakaoPayService kakaoPayService;
	
	// 결제 준비 요청
	@PostMapping
	public ResponseEntity<ApiResponse<KakaoPayResponse>> subscribe (
			@RequestBody KakaoPayRequest requestDTO) {
		ApiResponse<KakaoPayResponse> response = kakaoPayService.initiateSubscription(requestDTO);
		return ResponseEntity.ok(response);
	}
	
	// 결제 승인 요청
	@GetMapping("/approve")
	public ResponseEntity<ApiResponse<String>> approvePayment(
	        @RequestParam("pg_token") String pgToken,
	        @RequestParam("partnerOrderId") String partnerOrderId,
	        @RequestParam("partnerUserId") String partnerUserId) {

	    // 결제 승인 로직 호출
	    ApiResponse<String> response = kakaoPayService.approvePayment(pgToken, partnerOrderId, partnerUserId);

	    // ✅ 처리 결과에 따라 프론트엔드로 리다이렉트
	    if (response.isSuccess()) {
	        // 결제 승인 성공
	        return ResponseEntity.status(302)
	                .header("Location", "http://localhost:3000/subscribe/result?status=success")
	                .body(response);
	    } else {
	        // 결제 승인 실패
	        return ResponseEntity.status(302)
	                .header("Location", "http://localhost:3000/subscribe/result?status=fail")
	                .body(response);
	    }
	}
	
	// 정기 결제 실행 요청
	@PostMapping("/execute")
	public ResponseEntity<ApiResponse<String>> executeSubscription (
			@RequestParam String billingKey,
			@RequestParam String partnerOrderId,
			@RequestParam String partnerUserId,
			@RequestParam int amount) {
		ApiResponse<String> response = kakaoPayService.executeSubscription(billingKey, partnerOrderId, partnerUserId, amount);
		return ResponseEntity.ok(response);
	}
	
	// 정기 결제 취소 요청
	@DeleteMapping("/cancel")
	public ResponseEntity<ApiResponse<String>> cancelSubscription (
			@RequestParam("tid") String tid,
			@RequestParam("amount") int amount) {
		ApiResponse<String> response = kakaoPayService.cancelSubscription(tid, amount);
		return ResponseEntity.ok(response);
	}
}
