package com.puzzlelog.api.dto.response.kakaoPay;

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
public class KakaoPayResponse {

	private String nextRedirectUrl;
	private String tid;
	private String billingKey;
}
