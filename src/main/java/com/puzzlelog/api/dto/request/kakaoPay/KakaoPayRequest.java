package com.puzzlelog.api.dto.request.kakaoPay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoPayRequest {

	private String partnerOrderId;
	private String partnerUserId;
	private String itemName;
	private int totalAmount;
}
