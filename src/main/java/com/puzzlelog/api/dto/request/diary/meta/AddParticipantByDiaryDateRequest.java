package com.puzzlelog.api.dto.request.diary.meta;

import lombok.Data;

@Data
public class AddParticipantByDiaryDateRequest {
    private String diaryDate;
    private String senderId;
    private String userId;
}


/* 
1. @PatchMapping("/participants/add-by-date") 컨트롤러 메서드 ❌ 삭제
2. 서비스 메서드 addParticipantByDiaryDate(...) ❌ 삭제
3. DTO 클래스 AddParticipantByDiaryDateRequest.java ❌ 삭제
*/