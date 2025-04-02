package com.puzzlelog.api.dao.document;

import java.time.Instant;
import java.util.ArrayList;
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

    @Builder.Default
	private List<String> receiverIds = new ArrayList<>(); // 무조건 빈 리스트로 초기화

	private String diaryId;
	private String diaryDate;
	private String status; // PENDING, ACCEPTED, REJECTED

    @Builder.Default
	private List<String> acceptedUsers = new ArrayList<>();

    @Builder.Default
	private List<String> rejectedUsers = new ArrayList<>();

	private Instant createdAt;
}
