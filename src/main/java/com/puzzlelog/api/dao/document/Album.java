package com.puzzlelog.api.dao.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "album") // MongoDB 컬렉션 지정
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class Album {
	@Id
    private String id; 

    private String userId; 
    private String title; 
    private List<String> diaryId;
    private boolean purchased;
    private boolean isDeleted;
    
    @CreatedDate
    private Date createdAt;

	
}

