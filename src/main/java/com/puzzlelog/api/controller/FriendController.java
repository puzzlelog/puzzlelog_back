package com.puzzlelog.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.puzzlelog.api.dto.response.common.ApiResponse;
import com.puzzlelog.api.dto.response.friend.FriendResponse;
import com.puzzlelog.api.dto.response.friend.PagedFriendResponse;
import com.puzzlelog.api.service.FriendService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    /**
     * 친구 요청을 보냅니다.
     * JWT 인증을 통해 본인만 요청할 수 있습니다.
     *
     * @param friendId 친구 요청을 보낼 대상의 사용자 ID
     * @return 친구 요청 결과 응답
     */
    @PostMapping("/{friendId}")
    public ResponseEntity<ApiResponse<FriendResponse>> sendFriendRequest(
            @PathVariable String friendId
    ) {
        // 인증된 사용자 ID 추출
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        FriendResponse response = friendService.sendFriendRequest(userId, friendId);
        return ResponseEntity.ok(ApiResponse.success(response, "친구 요청 완료"));
    }

    /**
     * 친구 요청을 수락합니다.
     * 본인에게 들어온 친구 요청만 수락할 수 있습니다.
     *
     * @param friendId 친구 요청을 보낸 사용자 ID
     * @return 성공 메시지 응답
     */
    @PatchMapping("/{friendId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptFriendRequest(
            @PathVariable String friendId
    ) {
        // 본인 ID는 JWT 인증에서 추출
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        friendService.acceptFriendRequest(userId, friendId);
        return ResponseEntity.ok(ApiResponse.successMessage("친구 요청을 수락했습니다."));
    }
    
    /**
     * 친구 요청을 거절합니다.
     * 본인에게 들어온 친구 요청만 거절할 수 있습니다.
     *
     * @param friendId 친구 요청을 보낸 사용자 ID
     * @return 성공 메시지 응답
     */
    @PatchMapping("/{friendId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectFriendRequest(
            @PathVariable String friendId
    ) {
        // 인증된 사용자 ID 추출 (JWT 기반)
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        friendService.rejectFriendRequest(userId, friendId);
        return ResponseEntity.ok(ApiResponse.successMessage("친구 요청을 거절했습니다."));
    }

    /**
     * 친구 목록을 조회합니다.
     * 조회 유형(type)에 따라 받은 요청, 보낸 요청, 친구 목록, 차단 목록으로 구분됩니다.
     * 
     * @param type 조회 유형 (your_request, my_request, friends, blocked)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 당 항목 수
     * @return 조회된 친구 목록 응답
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedFriendResponse>> getFriends(
            @RequestParam String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // 인증된 사용자 ID 추출
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        PagedFriendResponse response = friendService.getFriendsByType(userId, type, page, size);

        String message = switch (type.toLowerCase()) {
            case "your_request" -> "받은 친구 요청 목록 조회 성공";
            case "my_request" -> "보낸 친구 요청 목록 조회 성공";
            case "friends" -> "친구 목록 조회 성공";
            case "blocked" -> "차단된 친구 목록 조회 성공";
            default -> "친구 목록 조회 성공";
        };

        return ResponseEntity.ok(ApiResponse.success(response, message));
    }

    /**
     * 친구를 차단합니다.
     * 차단된 친구는 이후 친구 요청이나 상호작용이 불가능합니다.
     *
     * @param friendId 차단할 대상 사용자 ID
     * @return 차단된 친구 정보
     */
    @PatchMapping("/{friendId}/block")
    public ResponseEntity<ApiResponse<FriendResponse>> blockFriend(
            @PathVariable String friendId
    ) {
        // 본인 ID는 JWT로부터 추출
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        FriendResponse response = friendService.blockFriend(userId, friendId);
        return ResponseEntity.ok(ApiResponse.success(response, "친구를 차단했습니다."));
    }

    /**
     * 친구 차단을 해제합니다.
     * 차단 상태였던 친구 관계를 다시 ACCEPTED 상태로 되돌립니다.
     *
     * @param friendId 차단을 해제할 대상 사용자 ID
     * @return 갱신된 친구 관계 정보
     */
    @PatchMapping("/{friendId}/unblock")
    public ResponseEntity<ApiResponse<FriendResponse>> unblockFriend(
            @PathVariable String friendId
    ) {
        // 본인 ID는 JWT 인증에서 추출
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        FriendResponse response = friendService.unblockFriend(userId, friendId);
        return ResponseEntity.ok(ApiResponse.success(response, "친구 차단을 해제했습니다."));
    }

    /**
     * 친구를 삭제(비활성화)합니다.
     * 친구 상태가 ACCEPTED인 경우만 삭제할 수 있으며,
     * 양방향 친구 관계를 모두 DEACTIVATED 상태로 변경합니다.
     *
     * @param friendId 삭제할 친구의 사용자 ID
     * @return 삭제된 친구 정보
     */
    @DeleteMapping("/{friendId}")
    public ResponseEntity<ApiResponse<FriendResponse>> deactivateFriend(
            @PathVariable String friendId
    ) {
        // JWT에서 본인 ID 추출
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        FriendResponse response = friendService.deactivateFriend(userId, friendId);
        return ResponseEntity.ok(ApiResponse.success(response, "친구를 삭제했습니다."));
    }
}