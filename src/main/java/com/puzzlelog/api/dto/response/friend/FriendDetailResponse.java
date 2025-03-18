package com.puzzlelog.api.dto.response.friend;

import com.puzzlelog.api.dao.entity.Friend;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendDetailResponse {
    private String userId;
    private String friendId;
    private String nickname;  // 친구의 닉네임 (USER 엔티티 참조)
    private String updatedAt; // 상태 변경일시 (Friend 참조)

    public static FriendDetailResponse from(Friend friend) {
        return FriendDetailResponse.builder()
                .userId(friend.getUser().getUserId())
                .friendId(friend.getFriend().getUserId())
                .nickname(friend.getFriend().getNickname())
                .updatedAt(friend.getUpdatedAt().toString())
                .build();
    }
}