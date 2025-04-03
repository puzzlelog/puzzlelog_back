package com.puzzlelog.api.dao.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoPaySubscription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer Id;
	
	@Column(nullable = false)
	private String partnerOrderId;
	
	@Column(nullable = false)
	private String partnerUserId;
	
	@Column(nullable = false)
	private String itemName;
	
	@Column(nullable = false)
	private int totalAmount;
	
	@Column(nullable = true)
	private String billingKey;
	
	@Column(nullable = false)
	private String tid;
	
	@Column(nullable = true)
	private String nextRedirectUrl;
	
	@CreationTimestamp
	@Column(nullable = true)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = true)
	private LocalDateTime updatedAt;
}
