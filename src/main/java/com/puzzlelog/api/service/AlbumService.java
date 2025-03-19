package com.puzzlelog.api.service;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.puzzlelog.api.dao.document.Album;
import com.puzzlelog.api.repository.mongo.AlbumRepository;

@Service
public class AlbumService {

	@Autowired
	private AlbumRepository albumRepository;

	// 사용자별 앨범 조회
	public List<Album> getAlbumsByUser(String userId) {

		return albumRepository.findByUserIdAndIsDeletedFalse(userId);
	}

	// 특정 앨범 조회
    public Album getAlbumById(ObjectId albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다."));
    }

	// 앨범 저장 (insert)
	public Album addAlbum(Album album) {
		album.setCreatedAt(new Date());
		return albumRepository.save(album);
	}

	public void deleteAlbum(ObjectId albumId) {
	    Album album = albumRepository.findById(albumId)
	            .orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다."));
	    album.setDeleted(true); // isDeleted를 true로 설정
	    albumRepository.save(album); // 변경된 앨범 저장
	}
}
