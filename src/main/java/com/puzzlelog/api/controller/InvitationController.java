package com.puzzlelog.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.puzzlelog.api.dto.request.invitation.InvitationRequest;
import com.puzzlelog.api.dto.response.common.ApiResponse;
import com.puzzlelog.api.dto.response.invitation.InvitationResponse;
import com.puzzlelog.api.dto.response.invitation.InvitationSimpleResponse;
import com.puzzlelog.api.service.InvitationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    // ✅ 초대 생성
    @PostMapping
    public ResponseEntity<ApiResponse<InvitationSimpleResponse>> createInvitation(@RequestBody InvitationRequest request) {
        String senderIdFromJwt = getAuthenticatedUserId(); // JWT 사용자 ID 사용
        InvitationSimpleResponse response = invitationService.createInvitation(senderIdFromJwt, request);
        return ResponseEntity.ok(ApiResponse.success(response, "초대 생성 성공"));
    }

    /**
     * 초대 목록 조회 (받은 초대 또는 보낸 초대)
     *
     * @param type 초대 목록의 타입 (기본값: my_request)
     *             - my_request: 내가 받은 초대 목록 조회
     *             - your_request: 내가 보낸 초대 목록 조회
     * @return 초대 목록 조회 결과
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvitationSimpleResponse>>> getInvitations(
            @RequestParam(defaultValue = "my_request", required = false) String type) {

        String currentUserId = getAuthenticatedUserId();

        if (!type.equals("my_request") && !type.equals("your_request")) {
            throw new IllegalArgumentException("지원하지 않는 조회 타입입니다: " + type);
        }

        List<InvitationSimpleResponse> invitations = invitationService.getInvitationsByType(currentUserId, type);

        String message = type.equals("your_request")
            ? "내가 보낸 초대 목록 조회 성공"
            : "내가 받은 초대 목록 조회 성공";

        return ResponseEntity.ok(ApiResponse.success(invitations, message));
    }

    // ✅ 초대 상세 조회
    @GetMapping("/{invitationId}")
    public ResponseEntity<ApiResponse<InvitationResponse>> getInvitation(@PathVariable("invitationId") String invitationId) {
        String currentUserId = getAuthenticatedUserId();
        String currentUserRole = getAuthenticatedUserRole();

        InvitationResponse response = invitationService.getInvitation(invitationId, currentUserId, currentUserRole);
        return ResponseEntity.ok(ApiResponse.success(response, "초대 상세 조회 성공"));
    }
    

	 // 초대 수락 및 거절은 반드시 "초대장 고유 ID (invitationId)"를 사용해야 합니다.
	 // 이유:
	 //   1. UserId(사용자 아이디)만으로는 특정 초대장을 명확히 구분할 수 없습니다.
	 //       - 동일한 사용자가 동일한 대상자를 여러 개의 다른 일기에 초대할 수 있기 때문입니다.
	 //   2. "초대장 고유 ID (MongoDB의 ObjectId)"를 사용하면 특정 초대장(Invitation)을 명확히 식별하고 처리할 수 있습니다.
	 // 따라서, 수락(accept)과 거절(reject) API에서는 UserId가 아니라 "invitationId"를 사용하여 정확한 초대를 특정해야 합니다.

    // ✅ 초대 수락
    @PatchMapping("/{invitationId}/accept")
    public ResponseEntity<ApiResponse<String>> acceptInvitation(@PathVariable String invitationId) {
        String currentUserId = getAuthenticatedUserId();
        invitationService.acceptInvitation(invitationId, currentUserId);

        /*
         * ⚠️ 클라이언트 참고 ⚠️
         * 현재 초대 수락 요청 후 응답 메시지가 아래와 같이 올 수 있습니다:
         *
         * {
         *     "success": false,
         *     "message": "Cannot invoke \"java.util.Collection.toArray()\" because \"c\" is null",
         *     "data": null
         * }
         *
         * 이 메시지가 오더라도 실제로는 초대가 정상적으로 수락된 상태입니다.
         * 이는 서버에서 기존 초대 데이터의 일부 배열(receiverIds, acceptedUsers, rejectedUsers)이 null 상태로 저장되어 있기 때문이며,
         * 서버 팀에서 현재 문제 해결을 위한 작업을 진행 중입니다.
         *
         * 이 응답이 오더라도 클라이언트 측에서는 초대 수락이 성공한 것으로 간주하고 UI를 진행시켜주세요.
         * 
         * 빠른 시일 내에 서버에서 정상 응답을 하도록 수정 예정입니다.
         */

        return ResponseEntity.ok(ApiResponse.success(null, "초대가 수락되었습니다."));
    }


    // ✅ 초대 거절
    @PatchMapping("/{invitationId}/reject")
    public ResponseEntity<ApiResponse<String>> rejectInvitation(@PathVariable String invitationId) {
        String currentUserId = getAuthenticatedUserId();
        invitationService.rejectInvitation(invitationId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(null, "초대가 거절되었습니다."));
    }

    // JWT 인증 사용자 ID 가져오기
    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    
    // JWT 인증 사용자 Role 가져오기
    private String getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(role -> role.startsWith("ROLE_"))
            .findFirst()
            .map(role -> role.replace("ROLE_", ""))
            .orElse("USER");
    }
}
