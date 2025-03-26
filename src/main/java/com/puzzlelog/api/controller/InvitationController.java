package com.puzzlelog.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.puzzlelog.api.dto.request.invitation.InvitationRequest;
import com.puzzlelog.api.dto.response.invitation.InvitationResponse;
import com.puzzlelog.api.service.InvitationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController {

	private final InvitationService invitationService;
	
	// 초대 생성
	@PostMapping
	public ResponseEntity<InvitationResponse> createInvitation(@RequestBody InvitationRequest request) {
		InvitationResponse response = invitationService.createInvitation(request);
		return ResponseEntity.ok(response);
	}
	
	// 조회
	@GetMapping("/{invitationId}")
	public ResponseEntity<InvitationResponse> getInvitation(@PathVariable String invitationId) {
		InvitationResponse response = invitationService.getInvitation(invitationId);
		return ResponseEntity.ok(response);
	}
	
	// 초대 수락
	@PatchMapping("/{invitationId}/accept")
	public ResponseEntity<String> acceptInvitation(@PathVariable String invitationId, @RequestParam String userId) {
		invitationService.acceptInvitation(invitationId, userId);
		return ResponseEntity.ok("초대가 수락되었습니다.");
	}
	
	// 초대 거절
	@PatchMapping("/{invitationId}/reject")
	public ResponseEntity<String> rejectInvitation(@PathVariable String invitationId, @RequestParam String userId) {
		invitationService.rejectInvitation(invitationId, userId);
		return ResponseEntity.ok("초대가 거절되었습니다.");
	}
}