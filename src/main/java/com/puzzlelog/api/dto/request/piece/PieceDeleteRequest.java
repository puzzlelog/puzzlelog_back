package com.puzzlelog.api.dto.request.piece;

import lombok.*;
import javax.validation.constraints.NotBlank;

/**
 * 조각 삭제 요청 DTO입니다.
 * 관리자에 의해 조각이 삭제될 경우, 삭제 사유(reason)를 명시적으로 입력받기 위해 사용됩니다.
 * 일반 사용자는 이 DTO 없이 삭제 요청을 보냅니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceDeleteRequest {

    /**
     * 관리자 삭제 시 필수 입력되는 삭제 사유입니다.
     * 일반 사용자는 이 필드를 보내지 않으며, 서버에서 "본인 삭제"로 자동 기록됩니다.
     */
    @NotBlank(message = "삭제 사유는 필수입니다.")
    private String reason;
}
