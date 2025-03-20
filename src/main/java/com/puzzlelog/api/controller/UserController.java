package com.puzzlelog.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.puzzlelog.api.config.ApiResponse;
import com.puzzlelog.api.dto.request.auth.LoginRequest;
import com.puzzlelog.api.dto.request.auth.SignupRequest;
import com.puzzlelog.api.dto.request.user.UserSearchRequest;
import com.puzzlelog.api.dto.request.user.UserUpdateRequest;
import com.puzzlelog.api.dto.response.auth.LoginResponse;
import com.puzzlelog.api.dto.response.auth.SignupResponse;
import com.puzzlelog.api.dto.response.user.PagedUserResponse;
import com.puzzlelog.api.dto.response.user.UserResponse;
import com.puzzlelog.api.dto.response.user.UserUpdateResponse;
import com.puzzlelog.api.service.AuthService;
import com.puzzlelog.api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	
	private final AuthService authService;
	private final UserService userService;

    // 회원가입
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @RequestPart("data") SignupRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {

        if (request.getUserId() == null || request.getUserId().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("아이디는 필수 입력값입니다."));
        }

        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("이메일은 필수 입력값입니다."));
        }

        if (authService.existsByUserId(request.getUserId())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("이미 존재하는 아이디입니다."));
        }

        if (authService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("이미 존재하는 이메일입니다."));
        }

        SignupResponse response = authService.registerUser(request, file);
        return ResponseEntity.ok(ApiResponse.success(response, "회원가입 성공"));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
    	LoginResponse response = authService.validateUser(loginRequest);

    	if (response != null) {
            return ResponseEntity.ok(ApiResponse.success(response, "로그인 성공"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("아이디 또는 비밀번호가 잘못되었습니다."));
        }
    }

    // 전체 및 특정 사용자 정보 조회 (페이징, 조건 검색 포함)
    @GetMapping
    public ResponseEntity<ApiResponse<PagedUserResponse>> getUsers(
            UserSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<UserResponse> users;

        if (request.hasNoCondition()) {
            users = userService.getAllUsers(page, size);
            return ResponseEntity.ok(ApiResponse.success(PagedUserResponse.from(users), "전체 사용자 조회 성공"));
        }

        users = userService.findUsers(request, page, size);

        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("조건에 맞는 사용자가 없습니다."));
        }

        return ResponseEntity.ok(ApiResponse.success(PagedUserResponse.from(users), "사용자 정보 조회 성공"));
    }

    // 특정 사용자 정보 수정 (Multipart 처리로 변경)
    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserUpdateResponse>> updateUser(
            @PathVariable String userId,
            @RequestPart(value = "data", required = false) UserUpdateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        // 둘 다 null이거나 비어있으면 예외 처리
        if ((request == null || request.isEmpty()) && (file == null || file.isEmpty())) {
            return ResponseEntity.badRequest().body(
                ApiResponse.fail("수정할 데이터 또는 파일을 하나 이상 첨부해야 합니다.")
            );
        }

        UserUpdateResponse response = userService.updateUser(userId, request, file);
        String updatedFields = String.join(", ", response.getUpdatedFields().keySet());

        return ResponseEntity.ok(
            ApiResponse.success(response, "사용자 정보가 성공적으로 수정되었습니다. (" + updatedFields + ")")
        );
    }

    // 특정 사용자 삭제 (비활성화)
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable String userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.ok(ApiResponse.successMessage("사용자 정보 삭제 성공"));
    }

    // 중복 체크 (아이디, 이메일, 닉네임)
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Void>> checkDuplicate(
            @RequestParam String type, 
            @RequestParam String value
    ) {
        boolean isDuplicate = userService.checkDuplicate(type, value);

        if (isDuplicate) {
            String message = "이미 사용 중인 " + getTypeInKorean(type) + "입니다.";
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.fail(message));
        }

        String message = "사용 가능한 " + getTypeInKorean(type) + "입니다.";
        return ResponseEntity.ok(ApiResponse.successMessage(message));
    }

    private String getTypeInKorean(String type) {
        switch (type) {
            case "userId":
                return "아이디";
            case "email":
                return "이메일";
            case "nickname":
                return "닉네임";
            default:
                return "값";
        }
    }
}