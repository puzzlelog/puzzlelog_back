package com.puzzlelog.api.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.puzzlelog.api.dao.document.Invitation;
import com.puzzlelog.api.dto.request.invitation.InvitationRequest;
import com.puzzlelog.api.dto.response.invitation.InvitationResponse;
import com.puzzlelog.api.repository.mongo.InvitationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvitationService {

	private final InvitationRepository invitationRepository;
	private final DiaryService diaryService;
	
	// 초대 생성
	@Transactional
	public InvitationResponse createInvitation(InvitationRequest request) {
		Invitation invitation = Invitation.builder()
			.senderId(request.getSenderId())
			.receiverIds(request.getReceiverIds())
			.diaryDate(request.getDiaryDate())
			.status("PENDING")
			.acceptedUsers(new ArrayList<>())
			.createdAt(Instant.now())
			.build();
		
		invitation = invitationRepository.save(invitation);
		return InvitationResponse.from(invitation);
	}
	
	// 조회
	@Transactional(readOnly = true)
	public InvitationResponse getInvitation(String invitationId) {
		Invitation invitation = invitationRepository.findById(invitationId)
				.orElseThrow(() -> new NoSuchElementException("조회 실패"));
		return InvitationResponse.from(invitation);
	}
	
	// 초대 수락
	@Transactional
	public void acceptInvitation(String invitationId, String userId) {
		Invitation invitation = invitationRepository.findById(invitationId)
				.orElseThrow(() -> new NoSuchElementException("조회 실패"));
		
		if (!invitation.getReceiverIds().contains(userId)) {
			throw new IllegalArgumentException("이 초대는 해당 사용자에게 보내진 것이 아닙니다.");
		}
		
		// 이미 수락한 사용자라면 예외 처리
		if (invitation.getAcceptedUsers().contains(userId)) {
			throw new IllegalArgumentException("이미 수락한 사용자입니다.");
		}
		
		invitation.getAcceptedUsers().add(userId);
		
		// 수신자가 수락하면 상태를 "ACCEPTED"로 변경
		if (invitation.getAcceptedUsers().containsAll(invitation.getReceiverIds())) {
			invitation.setStatus("ACCEPTED");
		}
		
		invitationRepository.save(invitation);
		
		// DiaryService 호출하여 협업 일기에 추가
		diaryService.addParticipantByDiaryDate(invitation.getDiaryDate(), invitation.getSenderId(), userId);
	}
	
	// 초대 거절
	@Transactional
	public void rejectInvitation(String invitationId, String userId) {
		Invitation invitation = invitationRepository.findById(invitationId)
				.orElseThrow(() -> new NoSuchElementException("조회 실패"));
		
		if (!invitation.getReceiverIds().contains(userId)) {
			throw new IllegalArgumentException("이 초대는 해당 사용자에게 보내진 것이 아닙니다.");
		}
		
		// 초대 상태를 "REJECTED"로 변경
		invitation.setStatus("REJECTED");
		invitationRepository.save(invitation);
	}
}