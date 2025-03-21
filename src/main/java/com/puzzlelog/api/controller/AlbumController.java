package com.puzzlelog.api.controller;

import com.puzzlelog.api.dto.request.album.AlbumRequest;
import com.puzzlelog.api.dto.response.album.AlbumResponse;
import com.puzzlelog.api.dto.response.common.ApiResponse;
import com.puzzlelog.api.service.AlbumService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    // 사용자별 앨범 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<AlbumResponse>>> getUserAlbums(@RequestParam("userId") String userId) {
        List<AlbumResponse> albumResponses = albumService.getAlbumsByUser(userId)
                .stream()
                .map(AlbumResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(albumResponses, "앨범 목록 조회 성공"));
    }

    // 특정 앨범 조회
    @GetMapping("/{albumId}")
    public ResponseEntity<ApiResponse<AlbumResponse>> getAlbumById(@PathVariable("albumId") String albumId) {
        try {
            ObjectId objectId = new ObjectId(albumId);
            AlbumResponse albumResponse = new AlbumResponse(albumService.getAlbumById(objectId));
            return ResponseEntity.ok(ApiResponse.success(albumResponse, "앨범 조회 성공"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("유효하지 않은 albumId 형식입니다."));
        }
    }

    // 앨범 저장 (insert)
    @PostMapping
    public ResponseEntity<ApiResponse<AlbumResponse>> addAlbum(@RequestBody AlbumRequest albumRequest) {
        AlbumResponse albumResponse = new AlbumResponse(albumService.addAlbum(albumRequest));
        return ResponseEntity.status(201).body(ApiResponse.success(albumResponse, "앨범 저장 성공"));
    }

    // 앨범 삭제 처리
    @DeleteMapping("/{albumId}")
    public ResponseEntity<ApiResponse<String>> deleteAlbum(@PathVariable("albumId") String albumId) {
        try {
            ObjectId objectId = new ObjectId(albumId);
            albumService.deleteAlbum(objectId);
            return ResponseEntity.ok(ApiResponse.successMessage("앨범이 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("유효하지 않은 albumId 형식입니다."));
        }
    }
}
