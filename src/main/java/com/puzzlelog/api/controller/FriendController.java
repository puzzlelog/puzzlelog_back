package com.puzzlelog.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.puzzlelog.api.config.ApiResponse;
import com.puzzlelog.api.dto.response.friend.FriendResponse;
import com.puzzlelog.api.dto.response.friend.PagedFriendResponse;
import com.puzzlelog.api.service.FriendService;

@RestController
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    // 친구 요청 보내기
    @PostMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<ApiResponse<FriendResponse>> sendFriendRequest(
            @PathVariable String userId,
            @PathVariable String friendId
    ) {
        FriendResponse response = friendService.sendFriendRequest(userId, friendId);
        return ResponseEntity.ok(ApiResponse.success(response, "친구 요청 완료"));
    }

    // 친구 요청 수락
    @PatchMapping("/{userId}/requests/{friendId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptFriendRequest(
            @PathVariable String userId,
            @PathVariable String friendId
    ) {
        friendService.acceptFriendRequest(userId, friendId);
        return ResponseEntity.ok(ApiResponse.successMessage("친구 요청 수락 성공"));
    }

    // 친구 목록 조회 (페이징, 상태별 조회)
    @GetMapping("/{userId}/friends")
    public ResponseEntity<ApiResponse<PagedFriendResponse>> getFriends(
            @PathVariable String userId,
            @RequestParam String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
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

    // 친구 차단
    @PatchMapping("/{userId}/friends/{friendId}/block")
    public ResponseEntity<ApiResponse<FriendResponse>> blockFriend(
            @PathVariable String userId,
            @PathVariable String friendId
    ) {
        FriendResponse response = friendService.blockFriend(userId, friendId);
        return ResponseEntity.ok(ApiResponse.success(response, "친구를 차단했습니다."));
    }

    // 친구 차단 해제
    @PatchMapping("/{userId}/friends/{friendId}/unblock")
    public ResponseEntity<ApiResponse<FriendResponse>> unblockFriend(
            @PathVariable String userId,
            @PathVariable String friendId
    ) {
        FriendResponse response = friendService.unblockFriend(userId, friendId);
        return ResponseEntity.ok(ApiResponse.success(response, "친구 차단을 해제했습니다."));
    }

    // 친구 비활성화 (삭제)
    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<ApiResponse<FriendResponse>> deactivateFriend(
            @PathVariable String userId,
            @PathVariable String friendId
    ) {
        FriendResponse response = friendService.deactivateFriend(userId, friendId);
        return ResponseEntity.ok(ApiResponse.success(response, "친구를 삭제했습니다."));
    }
}