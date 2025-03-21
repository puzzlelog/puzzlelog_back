package com.puzzlelog.api.dto.request.diary.element;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryElementsOrderUpdateRequest {
	// 조각의 순서 변경을 처리 (추가와 삭제는 API로만 처리)
    private List<String> elementIds; // 요소의 순서를 포함한 전체 요소 ID 목록
}
