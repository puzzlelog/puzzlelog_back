package com.puzzlelog.puzzlelog.document;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "diaries")
public class DiaryDocument {

	@Id
	private String id; // MongoDB의 ObjectId
	@Field("user_id")
	private String userId; // MySQL users 테이블의 id 참조
	private String title;
	@Field("piece_ids")
	private List<String> pieceIds; // 연결된 조각 ID 목록
	@Field("theme_color")
	private String themeColor; // 감정 기반 색상
	@Field("emotion")
	private String emotion; // 사용자가 선택한 감정
	@Field("is_shared")
	private boolean isShared; // 공개 여부
	@Field("created_at")
	private Instant createdAt;
	@Field("encryption_key_id")
	private String encryptionKeyId; // 암호화 키 ID
	@Field("is_deleted")
	private boolean isDeleted = false; // 기본값 false

	public DiaryDocument() {
	}

	public DiaryDocument(String userId, String title, List<String> pieceIds, String themeColor, boolean isShared,
			Instant createdAt, String encryptionKeyId, boolean isDeleted, String emotion) {
		this.userId = userId;
		this.title = title;
		this.pieceIds = pieceIds;
		this.themeColor = themeColor;
		this.isShared = isShared;
		this.createdAt = createdAt;
		this.encryptionKeyId = encryptionKeyId;
		this.isDeleted = isDeleted;
		this.emotion = emotion;
	}

	// Getter & Setter
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getPieceIds() {
		return pieceIds;
	}

	public void setPieceIds(List<String> pieceIds) {
		this.pieceIds = pieceIds;
	}

	public String getThemeColor() {
		return themeColor;
	}

	public void setThemeColor(String themeColor) {
		this.themeColor = themeColor;
	}

	public void setShared(boolean shared) {
		isShared = shared;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public String getEncryptionKeyId() {
		return encryptionKeyId;
	}

	public void setEncryptionKeyId(String encryptionKeyId) {
		this.encryptionKeyId = encryptionKeyId;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public boolean isShared() {
		return isShared;
	}

	public String getEmotion() {
		return emotion;
	}

	public void setEmotion(String emotion) {
		this.emotion = emotion;
	}
	
}
