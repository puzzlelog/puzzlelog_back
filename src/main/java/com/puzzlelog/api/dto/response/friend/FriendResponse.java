package com.puzzlelog.api.dto.response.friend;

import com.puzzlelog.api.dao.entity.Friend;
import lombok.*;

/**
 * 친구 관련 응답 DTO입니다.
 * 친구 요청 또는 상태 변경 후, 사용자 간 관계 정보를 반환합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendResponse {

    /** 요청한 사용자 ID */
    private String userId;

    /** 요청 대상 사용자 ID */
    private String friendId;

    /**
     * Friend 엔티티를 FriendResponse DTO로 변환합니다.
     *
     * @param friend 친구 관계 엔티티
     * @return 변환된 FriendResponse 객체
     */
    public static FriendResponse from(Friend friend) {
        return FriendResponse.builder()
                .userId(friend.getUser().getUserId())
                .friendId(friend.getFriend().getUserId())
                .build();
    }
}
