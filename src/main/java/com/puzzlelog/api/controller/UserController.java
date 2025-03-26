package com.puzzlelog.api.controller;

import com.puzzlelog.api.dto.request.user.UserSearchRequest;
import com.puzzlelog.api.dto.request.user.UserUpdateRequest;
import com.puzzlelog.api.dto.response.common.ApiResponse;
import com.puzzlelog.api.dto.response.user.PagedUserResponse;
import com.puzzlelog.api.dto.response.user.UserResponse;
import com.puzzlelog.api.dto.response.user.UserUpdateResponse;
import com.puzzlelog.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 현재 로그인한 사용자의 정보 수정 API
     *
     * <p>
     * 사용자 본인의 프로필 정보를 수정합니다. 요청은 multipart/form-data 형식으로 전송되며,
     * JSON 문자열 형태의 수정 요청 데이터(`data`)와 프로필 이미지 파일(`file`)을 전달받습니다.
     * 둘 중 하나는 반드시 포함되어야 하며, 둘 다 없을 경우 400 Bad Request 응답을 반환합니다.
     * </p>
     *
     * @param request 수정할 사용자 정보(JSON 문자열로 전달됨, 선택)
     * @param file 수정할 프로필 이미지(선택)
     * @return 수정된 필드 목록과 함께 사용자 정보 응답 DTO
     */
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserUpdateResponse>> updateUser(
            @RequestPart(value = "data", required = false) UserUpdateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        // JWT에서 인증된 사용자 아이디 추출
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 요청 데이터가 모두 비어 있을 경우 예외 처리
        if ((request == null || request.isEmpty()) && (file == null || file.isEmpty())) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.fail("수정할 데이터 또는 파일을 하나 이상 첨부해야 합니다."));
        }

        // 사용자 정보 수정 서비스 호출
        UserUpdateResponse response = userService.updateUser(userId, request, file);

        // 어떤 필드가 수정되었는지 메타 메시지 구성
        String updatedFields = String.join(", ", response.getUpdatedFields().keySet());

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "사용자 정보가 성공적으로 수정되었습니다. (" + updatedFields + ")"
        ));
    }
    
    /**
     * 관리자 전용 사용자 정보 수정 API
     *
     * <p>
     * 관리자 권한이 있는 사용자가 다른 사용자의 정보를 수정할 수 있는 엔드포인트입니다.
     * 요청은 multipart/form-data 형식으로 전송되며,
     * JSON 문자열 형태의 수정 요청 데이터(`data`)와 프로필 이미지 파일(`file`)을 전달받습니다.
     * </p>
     *
     * @param userId 수정 대상 사용자 ID (PathVariable)
     * @param request 수정할 사용자 정보(JSON 문자열로 전달됨, 선택)
     * @param file 수정할 프로필 이미지(선택)
     * @return 수정된 필드 목록과 함께 사용자 정보 응답 DTO
     */
    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserUpdateResponse>> updateUserByAdmin(
            @PathVariable String userId,
            @RequestPart(value = "data", required = false) UserUpdateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        if ((request == null || request.isEmpty()) && (file == null || file.isEmpty())) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.fail("수정할 데이터 또는 파일을 하나 이상 첨부해야 합니다."));
        }

        UserUpdateResponse response = userService.updateUser(userId, request, file);
        String updatedFields = String.join(", ", response.getUpdatedFields().keySet());

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "사용자 정보가 성공적으로 수정되었습니다. (" + updatedFields + ")"
        ));
    }
    
//    /**
//     * 관리자용 사용자 목록 조회 API
//     */
//    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<PagedUserResponse>> getUsers(
//            UserSearchRequest request,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        Page<UserResponse> users = request.hasNoCondition() ?
//                userService.getAllUsers(page, size) :
//                userService.findUsers(request, page, size);
//
//        if (users.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(ApiResponse.fail("조건에 맞는 사용자가 없습니다."));
//        }
//
//        return ResponseEntity.ok(ApiResponse.success(PagedUserResponse.from(users), "사용자 정보 조회 성공"));
//    }


//
//    /**
//     * 현재 사용자 탈퇴(논리 삭제) API
//     */
//    @DeleteMapping("/me")
//    public ResponseEntity<ApiResponse<Void>> deactivateUser() {
//        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
//        userService.deactivateUser(userId);
//        return ResponseEntity.ok(ApiResponse.successMessage("사용자 정보 삭제 성공"));
//    }
//
//    /**
//     * 아이디, 이메일, 닉네임 중복 검사 API
//     */
//    @GetMapping("/check")
//    public ResponseEntity<ApiResponse<Void>> checkDuplicate(
//            @RequestParam String type,
//            @RequestParam String value) {
//
//        boolean isDuplicate = userService.checkDuplicate(type, value);
//
//        String message = isDuplicate ?
//                "이미 사용 중인 항목입니다." :
//                "사용 가능한 항목입니다.";
//
//        return isDuplicate ?
//                ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.fail(message)) :
//                ResponseEntity.ok(ApiResponse.successMessage(message));
//    }
} 
