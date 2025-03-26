package com.puzzlelog.api.dto.request.invitation;

import java.util.List;

import lombok.Data;

@Data
public class InvitationRequest {

	private String senderId;
	private List<String> receiverIds;
	private String diaryId;
	private String diaryDate;
}