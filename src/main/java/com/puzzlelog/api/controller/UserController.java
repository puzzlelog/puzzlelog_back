package com.puzzlelog.api.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.puzzlelog.api.dto.request.user.UserSearchRequest;
import com.puzzlelog.api.dto.request.user.UserUpdateRequest;
import com.puzzlelog.api.dto.response.common.ApiResponse;
import com.puzzlelog.api.dto.response.user.PagedUserResponse;
import com.puzzlelog.api.dto.response.user.UserResponse;
import com.puzzlelog.api.dto.response.user.UserUpdateResponse;
import com.puzzlelog.api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 현재 로그인한 사용자 본인 정보 수정 API
     * 
     * 사용자 본인의 프로필 정보를 수정합니다.
     * 요청은 multipart/form-data 형식이며, JSON 문자열 형태의 수정 요청 데이터(`data`)와
     * 프로필 이미지 파일(`file`) 중 적어도 하나는 포함되어야 합니다.
     *
     * @param request 수정할 사용자 정보(JSON, 선택)
     * @param file 수정할 프로필 이미지(선택)
     * @return 수정된 필드 목록을 포함한 사용자 정보 응답 DTO
     */
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserUpdateResponse>> updateUser(
            @Valid @RequestPart(value = "data", required = false) UserUpdateRequest request,
            BindingResult bindingResult,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 💥 유효성 검증 에러 응답
        if (request != null && bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(ApiResponse.fail("유효성 검사 실패: " + errorMessage));
        }

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


    /**
     * 관리자 전용 사용자 정보 수정 API
     * 
     * 관리자 권한이 있는 사용자가 다른 사용자의 정보를 수정할 수 있습니다.
     * 요청은 multipart/form-data 형식이며, JSON 형태의 수정 요청 데이터(`data`)와
     * 프로필 이미지 파일(`file`)을 전달할 수 있습니다.
     *
     * @param userId 수정 대상 사용자 ID (PathVariable)
     * @param request 수정할 사용자 정보(JSON, 선택)
     * @param file 수정할 프로필 이미지(선택)
     * @return 수정된 필드 목록을 포함한 사용자 정보 응답 DTO
     */
    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserUpdateResponse>> updateUserByAdmin(
            @PathVariable String userId,
            @Valid @RequestPart(value = "data", required = false) UserUpdateRequest request,
            BindingResult bindingResult,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        // 💥 유효성 검사 실패 처리
        if (request != null && bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(ApiResponse.fail("유효성 검사 실패: " + errorMessage));
        }

        // 데이터 유무 확인
        if ((request == null || request.isEmpty()) && (file == null || file.isEmpty())) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.fail("수정할 데이터 또는 파일을 하나 이상 첨부해야 합니다."));
        }

        // 서비스 호출
        UserUpdateResponse response = userService.updateUser(userId, request, file);
        String updatedFields = String.join(", ", response.getUpdatedFields().keySet());

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "사용자 정보가 성공적으로 수정되었습니다. (" + updatedFields + ")"
        ));
    }

    
    /**
     * 로그인한 사용자 본인 정보 조회 API
     * JWT 인증 정보를 기반으로 현재 로그인한 사용자의 정보를 조회합니다.
     *
     * @return 사용자 정보 응답 DTO
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponse user = userService.getMyInfo(userId);
        return ResponseEntity.ok(ApiResponse.success(user, "내 정보 조회 성공"));
    }

    /**
     * 관리자용 사용자 목록 조회 API
     * 검색 조건이 없으면 전체 목록을, 조건이 있으면 필터링된 사용자 목록을 반환합니다.
     * 페이징이 적용되며, 관리자 권한이 필요합니다.
     *
     * @param request 검색 조건 DTO
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 사용자 목록 및 페이지 정보
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedUserResponse>> getUsers(
            @Valid @ModelAttribute UserSearchRequest request,
            BindingResult bindingResult,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest servletRequest) {
    	
    	// ✅ 필드 외 파라미터 거르기
        Set<String> allowedParams = Set.of(
            "email", "userId", "nickname", "gender", "isAlarm", "status", "role",
            "createdAtFrom", "createdAtTo",
            "birthDateFrom", "birthDateTo",
            "lastLoginFrom", "lastLoginTo",
            "page", "size"
        );

        for (String paramName : servletRequest.getParameterMap().keySet()) {
            if (!allowedParams.contains(paramName)) {
                return ResponseEntity.badRequest().body(ApiResponse.fail("허용되지 않은 검색 조건: " + paramName));
            }
        }

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(ApiResponse.fail("검색 조건 유효성 검사 실패: " + errorMessage));
        }

        Page<UserResponse> users = userService.searchUsers(request, page, size);

        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("조건에 맞는 사용자가 없습니다."));
        }

        return ResponseEntity.ok(ApiResponse.success(
                PagedUserResponse.from(users), "사용자 정보 조회 성공"));
    }

    /**
     * 아이디, 이메일, 닉네임 중복 검사 API
     *
     * 주어진 type(userId, email, nickname)과 value 값에 대해
     * 중복 여부를 확인하고 메시지를 반환합니다.
     *
     * @param type 검사할 필드 종류 (userId, email, nickname)
     * @param value 검사할 값
     * @return 중복 여부에 따른 메시지 응답
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Void>> checkDuplicate(
            @RequestParam
            @Pattern(regexp = "userId|email|nickname", message = "검사할 항목은 userId, email, nickname 중 하나여야 합니다.")
            String type,

            @RequestParam
            @NotBlank(message = "값(value)은 공백일 수 없습니다.")
            String value) {

        boolean isDuplicate = userService.checkDuplicate(type, value);

        String label;
        switch (type) {
            case "userId": label = "아이디"; break;
            case "email": label = "이메일"; break;
            case "nickname": label = "닉네임"; break;
            default: label = "항목"; // fallback
        }

        String message = isDuplicate
                ? String.format("이미 사용 중인 %s입니다.", label)
                : String.format("사용 가능한 %s입니다.", label);

        return isDuplicate
                ? ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.fail(message))
                : ResponseEntity.ok(ApiResponse.successMessage(message));
    }
 
    /**
     * 현재 로그인한 사용자 탈퇴 (논리 삭제) API
     *
     * 사용자가 직접 자신의 계정을 비활성화할 수 있습니다.
     * 상태(status)를 DELETED로 변경하며, 물리적인 삭제는 하지 않습니다.
     *
     * @return 탈퇴 성공 메시지 응답
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deactivateUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.deactivateUser(userId);
        return ResponseEntity.ok(ApiResponse.successMessage("사용자 탈퇴 처리 완료"));
    }
} 
