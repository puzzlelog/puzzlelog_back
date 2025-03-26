package com.puzzlelog.api.dao.document;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "invitations")
public class Invitation {

	@Id
	private String id;
	
	private String senderId;
	private List<String> receiverIds;
	private String diaryDate;
	private String status; // PENDING, ACCEPTED, REJECTED
	private List<String> acceptedUsers;
	private Instant createdAt;
}