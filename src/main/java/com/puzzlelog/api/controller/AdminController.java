package com.puzzlelog.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.puzzlelog.api.service.AuthService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    // 관리자 페이지 접근 제한
    @GetMapping("/dashboard")
    public ResponseEntity<String> adminDashboard(@RequestHeader("userId") String userId) {
        if (!authService.isAdmin(userId)) {
            return ResponseEntity.status(403).body("접근 권한이 없습니다.");
        }
        return ResponseEntity.ok("관리자 대시보드 접근 성공");
    }
}