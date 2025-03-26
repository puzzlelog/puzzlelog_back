package com.puzzlelog.api.dto.response.invitation;

import java.util.List;

import com.puzzlelog.api.dao.document.Invitation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvitationResponse {

	private String invitationId;
	private String senderId;
	private List<String> receiverIds;
	private String diaryDate;
	private String status;
	private List<String> acceptedUsers;
	
	public static InvitationResponse from(Invitation invitation) {
		return InvitationResponse.builder()
				.invitationId(invitation.getId())
				.senderId(invitation.getSenderId())
				.receiverIds(invitation.getReceiverIds())
				.diaryDate(invitation.getDiaryDate())
				.status(invitation.getStatus())
				.acceptedUsers(invitation.getAcceptedUsers())
				.build();
	}
}