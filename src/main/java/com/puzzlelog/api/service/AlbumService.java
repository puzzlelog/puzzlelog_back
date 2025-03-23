package com.puzzlelog.api.service;

import com.puzzlelog.api.dao.document.Album;
import com.puzzlelog.api.dto.request.album.AlbumRequest;
import com.puzzlelog.api.repository.mongo.AlbumRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    // 사용자별 앨범 조회
    public List<Album> getAlbumsByUser(String userId) {
        return albumRepository.findByUserIdAndDeletedFalse(userId); 
    }

    // 특정 앨범 조회
    public Album getAlbumById(ObjectId albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다."));
    }

    // 앨범 저장 (insert)
    public Album addAlbum(AlbumRequest albumRequest) {
        Album album = new Album();
        album.setUserId(albumRequest.getUserId());
        album.setTitle(albumRequest.getTitle());
        album.setDiaryId(albumRequest.getDiaryId());
        album.setPurchased(albumRequest.isPurchased());
        album.setCreatedAt(new Date());
        album.setDeleted(false); // 새 앨범은 삭제되지 않은 상태로 저장
        return albumRepository.save(album);
    }

    // 앨범 삭제 처리
    public void deleteAlbum(ObjectId albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다."));
        album.setDeleted(true); // isDeleted를 true로 설정
        albumRepository.save(album); // 변경된 앨범 저장
    }
}
