package com.puzzlelog.api.dto.request.album;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlbumRequest {

	private String userId;
	private String title;
	private List<String> diaryId;
	private boolean purchased;
}
