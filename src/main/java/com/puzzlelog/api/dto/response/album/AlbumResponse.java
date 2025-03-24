package com.puzzlelog.api.dto.response.album;

import java.util.Date;
import java.util.List;

import com.puzzlelog.api.dao.document.Album;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlbumResponse {

	private String id;
	private String userId;
	private String title;
	private List<String> diaryId;
	private boolean purchased;
	private boolean deleted;
    private Date createdAt;
	
	public AlbumResponse(Album album) {
		this.id = album.getId();
		this.userId = album.getUserId();
        this.title = album.getTitle();
        this.diaryId = album.getDiaryId();
        this.purchased = album.isPurchased();
        this.deleted = album.isDeleted();
        this.createdAt = album.getCreatedAt();
	}
}
