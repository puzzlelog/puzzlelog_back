package com.puzzlelog.api.controller;

import com.puzzlelog.api.config.ApiResponse;
import com.puzzlelog.api.dao.document.Sticker;
import com.puzzlelog.api.service.AuthService;
import com.puzzlelog.api.service.StickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stickers")
public class StickerController {
    @Autowired
    private StickerService stickerService;

    @Autowired
    private AuthService authService;

    // ✅ 스티커 추가 (관리자만 가능)
    @PostMapping
    public ResponseEntity<ApiResponse<Sticker>> addSticker(
            @RequestHeader("userId") String userId,
            @RequestBody Sticker sticker) {

        if (!authService.isAdmin(userId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.fail("관리자만 스티커를 추가할 수 있습니다."));
        }

        try {
            Sticker savedSticker = stickerService.addSticker(sticker);
            return ResponseEntity.ok(ApiResponse.success(savedSticker, "스티커가 추가되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    // ✅ 삭제되지 않은 모든 스티커 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<Sticker>>> getAllStickers() {
        List<Sticker> stickers = stickerService.getAllStickers();
        return ResponseEntity.ok(ApiResponse.success(stickers, "스티커 목록 조회 성공"));
    }

    // ✅ 특정 타입의 삭제되지 않은 스티커 조회
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<Sticker>>> getStickersByType(@PathVariable String type) {
        List<Sticker> stickers = stickerService.getStickersByType(type);
        return ResponseEntity.ok(ApiResponse.success(stickers, "특정 타입 스티커 조회 성공"));
    }

    // ✅ 스티커 논리적 삭제 (관리자만 가능)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSticker(
            @RequestHeader("userId") String userId,
            @PathVariable String id) {

        if (!authService.isAdmin(userId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.fail("관리자만 삭제할 수 있습니다."));
        }

        boolean deleted = stickerService.deleteSticker(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.successMessage("스티커가 삭제되었습니다."));
        } else {
            return ResponseEntity.status(404)
                    .body(ApiResponse.fail("스티커를 찾을 수 없습니다."));
        }
    }
}
