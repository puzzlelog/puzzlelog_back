package com.puzzlelog.puzzlelog.service;


import java.util.List;

import javax.transaction.Transactional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.puzzlelog.puzzlelog.document.AlbumDocument;
import com.puzzlelog.puzzlelog.repository.AlbumRepository;

@Service
public class AlbumService {
	
	@Autowired
    private AlbumRepository albumRepository; 

    // 사용자별 앨범 조회
    public List<AlbumDocument> getAlbumsByUser(String userId) {
    	
    	List<AlbumDocument> lists = albumRepository.findByUserId(userId);
    	System.out.println(lists.size());
    	
        return lists;
    }

    
    
    // 특정 앨범 조회
    public AlbumDocument getAlbumById(String albumId, String userId) {
    	
    	// ✅ String을 ObjectId로 변환하여 조회
        ObjectId objectId = new ObjectId(albumId);
        
        try {
            objectId = new ObjectId(albumId); // ✅ String을 ObjectId로 변환
            System.out.println("check");
            return albumRepository.findById(objectId)
                    .orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다."));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 albumId 형식입니다: " + albumId);
        }
        
    }

    // ✅ 앨범 결제 처리
    @Transactional
    public void purchaseAlbum(String albumId, String userId) {
        try {
            // ✅ albumId를 ObjectId로 변환
            ObjectId objectId = new ObjectId(albumId);
            
            // ✅ 앨범 조회
            AlbumDocument album = albumRepository.findById(objectId)
                    .orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다."));

            // ✅ 사용자가 소유한 앨범인지 검증
            if (!album.getUserId().equals(userId)) {
                throw new RuntimeException("사용자가 소유한 앨범이 아닙니다.");
            }

            // ✅ 결제 처리 (purchased 필드 true 변경)
            album.setPurchased(true);
            albumRepository.save(album); // ✅ MongoDB 업데이트

            System.out.println("결제 완료: " + album.getTitle());

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 albumId 형식입니다: " + albumId);
        }
    }
    
    
}

