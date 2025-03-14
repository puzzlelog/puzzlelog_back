package com.puzzlelog.api.dto.response.friend;

import com.puzzlelog.api.dao.entity.Friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendDetailResponse {
    private int userId;
    private int friendId;
    private String nickname;  // 친구의 닉네임 (USER 엔티티 참조)
    private String updatedAt; // 상태 변경일시 (Friend 참조)

    public static FriendDetailResponse from(Friend friend) {
        return FriendDetailResponse.builder()
                .userId(friend.getUser().getId())
                .friendId(friend.getFriend().getId())
                .nickname(friend.getFriend().getNickname())
                .updatedAt(friend.getUpdatedAt().toString())
                .build();
    }
}
