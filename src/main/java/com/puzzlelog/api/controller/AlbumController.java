package com.puzzlelog.api.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.puzzlelog.api.dao.document.Album;
import com.puzzlelog.api.service.AlbumService;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {
	
	@Autowired
    private AlbumService albumService;
	
	// 사용자별 앨범 목록 조회
    @GetMapping
    public ResponseEntity<List<Album>> getUserAlbums(@RequestParam("userId") String userId) {
        return ResponseEntity.ok(albumService.getAlbumsByUser(userId));
    }

    // 특정 앨범 조회
    @GetMapping("/{albumId}")
    public ResponseEntity<Album> getAlbumById(@PathVariable("albumId") String albumId) {
        try {
            // String을 ObjectId로 변환
            ObjectId objectId = new ObjectId(albumId);
            return ResponseEntity.ok(albumService.getAlbumById(objectId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // 잘못된 ObjectId 형식일 경우 처리
        }
    }
    
    // 앨범 저장 (insert)
    @PostMapping
    public ResponseEntity<Album> addAlbum(@RequestBody Album album) {
        Album savedAlbum = albumService.addAlbum(album);
        return ResponseEntity.status(201).body(savedAlbum); // 201 Created 상태 코드 반환
    }
	
	// 앨범 삭제 처리 (DELETE 메서드로 수정)
	@DeleteMapping("/{albumId}")
	public ResponseEntity<String> deleteAlbum(@PathVariable("albumId") String albumId) {
	    try {
	        // String을 ObjectId로 변환
	        ObjectId objectId = new ObjectId(albumId);
	        albumService.deleteAlbum(objectId);  // 서비스 메서드 호출하여 앨범 삭제 처리
	        return ResponseEntity.ok("앨범이 삭제되었습니다.");
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body("유효하지 않은 albumId 형식입니다.");
	    }
	}

}

