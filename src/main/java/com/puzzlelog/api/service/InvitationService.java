package com.puzzlelog.api.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.puzzlelog.api.dao.document.Diary;
import com.puzzlelog.api.dao.document.Invitation;
import com.puzzlelog.api.dao.entity.User;
import com.puzzlelog.api.dto.request.invitation.InvitationRequest;
import com.puzzlelog.api.dto.response.invitation.InvitationResponse;
import com.puzzlelog.api.dto.response.invitation.InvitationSimpleResponse;
import com.puzzlelog.api.repository.mongo.DiaryRepository;
import com.puzzlelog.api.repository.mongo.InvitationRepository;
import com.puzzlelog.api.repository.mysql.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvitationService {

	private final InvitationRepository invitationRepository;
	private final UserRepository userRepository;
	private final DiaryRepository diaryRepository;
	private final DiaryService diaryService;
	
	// 초대 생성 (처음 생성 및 추가 초대 가능)
	@Transactional
    public InvitationSimpleResponse createInvitation(String senderIdFromJwt, InvitationRequest request) {

        // 초대 생성자는 JWT의 사용자 ID로 설정 (클라이언트 입력 X)
        String senderId = senderIdFromJwt;
        
        // 초대 대상(receiverIds)에 본인이 포함되었는지 확인
        if (request.getReceiverIds().contains(senderId)) {
            throw new IllegalArgumentException("본인에게는 초대장을 보낼 수 없습니다.");
        }

        // 초대 대상(receiverIds) 실제 존재하는 사용자 검증
        List<String> receiverIds = request.getReceiverIds();
        List<String> validReceiverIds = userRepository.findByUserIdIn(receiverIds)
            .stream()
            .map(User::getUserId)
            .collect(Collectors.toList());

        if (validReceiverIds.size() != receiverIds.size()) {
            throw new IllegalArgumentException("초대 대상 중 존재하지 않는 사용자가 포함되어 있습니다.");
        }

        // diaryId가 실제로 존재하는 Diary인지도 추가 검증
         Diary diary = diaryRepository.findById(request.getDiaryId())
             .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Diary입니다."));

        Invitation invitation = Invitation.builder()
            .senderId(senderId)
            .receiverIds(validReceiverIds) // 검증된 사용자 목록 사용
            .diaryId(diary.getId()) // 검증된 일기 Id 사용
            .diaryDate(request.getDiaryDate())
            .status("PENDING")
            .acceptedUsers(new ArrayList<>()) // null이 아닌 빈 리스트로 초기화
            .rejectedUsers(new ArrayList<>()) // null이 아닌 빈 리스트로 초기화
            .createdAt(Instant.now())
            .build();

        invitation = invitationRepository.save(invitation);
        return InvitationSimpleResponse .from(invitation);
    }
	
	// 초대 목록 조회 (받은 초대, 보낸 초대)
	@Transactional(readOnly = true)
	public List<InvitationSimpleResponse> getInvitationsByType(String userId, String type) {
	    List<Invitation> invitations;

	    switch (type.toLowerCase()) {
	        case "your_request":  // 내가 보낸 초대
	            invitations = invitationRepository.findAllBySenderId(userId);
	            break;

	        case "my_request":  // 내가 받은 초대
	            invitations = invitationRepository.findAllByReceiverIdsContaining(userId);
	            break;

	        default:
	            throw new IllegalArgumentException("지원하지 않는 조회 타입입니다: " + type);
	    }

	    return invitations.stream()
	        .map(InvitationSimpleResponse::from)
	        .collect(Collectors.toList());
	}

	
	// 초대 상세 조회
	@Transactional(readOnly = true)
	public InvitationResponse getInvitation(String invitationId, String currentUserId, String currentUserRole) {
	    Invitation invitation = invitationRepository.findById(invitationId)
	        .orElseThrow(() -> new NoSuchElementException("조회 실패"));

	    // 보안 체크: 초대자(senderId), 초대받은 사용자(receiverIds), 관리자만 접근 허용
	    boolean isSender = invitation.getSenderId().equals(currentUserId);
	    boolean isReceiver = invitation.getReceiverIds().contains(currentUserId);
	    boolean isAdmin = currentUserRole.equals("ADMIN");

	    if (!isSender && !isReceiver && !isAdmin) {
	        throw new AccessDeniedException("초대 상세 내용을 조회할 권한이 없습니다.");
	    }

	    return InvitationResponse.from(invitation);
	}
	
	// 초대 수락
	@Transactional
	public void acceptInvitation(String invitationId, String userId) {
	    Invitation invitation = validateReceiverAndGetInvitation(invitationId, userId);

	    // 반드시 먼저 체크하고 초기화 수행
	    if (invitation.getRejectedUsers() == null) {
	        invitation.setRejectedUsers(new ArrayList<>());
	    }
	    if (invitation.getAcceptedUsers() == null) {
	        invitation.setAcceptedUsers(new ArrayList<>());
	    }
	    

	    if (invitation.getAcceptedUsers().contains(userId)) {
	        throw new IllegalArgumentException("이미 수락한 사용자입니다.");
	    }

	    // 이전에 거절한 사용자라면 거절 목록에서 제거 (이전 null 체크 했으니 안전)
	    invitation.getRejectedUsers().remove(userId);

	    // 수락 목록에 추가
	    invitation.getAcceptedUsers().add(userId);

	    // 상태 결정 로직 (안전한 크기 체크)
	    int totalReceivers = (invitation.getReceiverIds() != null) ? invitation.getReceiverIds().size() : 0;
	    int acceptedCount = invitation.getAcceptedUsers().size();
	    int rejectedCount = invitation.getRejectedUsers().size();

	    if (acceptedCount == totalReceivers) {
	        invitation.setStatus("ACCEPTED");
	    } else if (acceptedCount + rejectedCount == totalReceivers) {
	        invitation.setStatus("PARTIAL");
	    } else {
	        invitation.setStatus("PENDING");
	    }

	    invitationRepository.save(invitation);

	    diaryService.addParticipant(invitation.getDiaryId(), userId);
	}
	
	@Transactional
	public void rejectInvitation(String invitationId, String userId) {
	    Invitation invitation = validateReceiverAndGetInvitation(invitationId, userId);

	    // 필수 null 체크 & 초기화
	    if (invitation.getRejectedUsers() == null) {
	        invitation.setRejectedUsers(new ArrayList<>());
	    }
	    if (invitation.getAcceptedUsers() == null) {
	        invitation.setAcceptedUsers(new ArrayList<>());
	    }

	    // ✅ 이미 수락한 사용자는 거절 불가능
	    if (invitation.getAcceptedUsers().contains(userId)) {
	        throw new IllegalArgumentException("이미 초대를 수락했습니다.");
	    }

	    // 거절 목록에 사용자 추가
	    if (!invitation.getRejectedUsers().contains(userId)) {
	        invitation.getRejectedUsers().add(userId);
	    }

	    // 명확한 상태 결정 로직 (null-safe)
	    int totalReceivers = (invitation.getReceiverIds() != null) ? invitation.getReceiverIds().size() : 0;
	    int acceptedCount = invitation.getAcceptedUsers().size();
	    int rejectedCount = invitation.getRejectedUsers().size();

	    if (rejectedCount == totalReceivers && totalReceivers > 0) {
	        invitation.setStatus("REJECTED");
	    } else if (acceptedCount + rejectedCount == totalReceivers && totalReceivers > 0) {
	        invitation.setStatus("PARTIAL");
	    } else {
	        invitation.setStatus("PENDING");
	    }

	    invitationRepository.save(invitation);
	}
	
	// 공통 메서드
	private Invitation validateReceiverAndGetInvitation(String invitationId, String userId) {
	    Invitation invitation = invitationRepository.findById(invitationId)
	        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 초대입니다."));

	    if (!invitation.getReceiverIds().contains(userId)) {
	        throw new IllegalArgumentException("이 초대는 해당 사용자에게 보내진 것이 아닙니다.");
	    }

	    return invitation;
	}

}