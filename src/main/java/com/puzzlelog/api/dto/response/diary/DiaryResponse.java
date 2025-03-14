package com.puzzlelog.api.dto.response.diary;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DiaryResponse {
    private String id;
    private Integer userId;
    private String title;
    // 나머지 필드들은 일단 생략
}
