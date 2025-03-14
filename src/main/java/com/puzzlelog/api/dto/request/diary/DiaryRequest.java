package com.puzzlelog.api.dto.request.diary;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DiaryRequest {
    private Integer userId;
    private String title;
    // 나머지 필드들은 일단 생략
}
