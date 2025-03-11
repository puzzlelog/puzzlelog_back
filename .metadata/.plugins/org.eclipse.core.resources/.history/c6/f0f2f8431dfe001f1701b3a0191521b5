package com.puzzlelog.puzzlelog.document;

import java.util.Map;

import lombok.Data;

@Data
public class EmotionStatsDocument {

	private String userId;
	private String startDate;
	private String endDate;
	private Map<String, Integer> emotionCounts;
	
	public EmotionStatsDocument () {}
	
	public EmotionStatsDocument (String userId, String startDate, String endDate, Map<String, Integer> emotionCounts) {
		this.userId = userId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.emotionCounts = emotionCounts;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Map<String, Integer> getEmotionCounts() {
		return emotionCounts;
	}

	public void setEmotionCounts(Map<String, Integer> emotionCounts) {
		this.emotionCounts = emotionCounts;
	}
}
