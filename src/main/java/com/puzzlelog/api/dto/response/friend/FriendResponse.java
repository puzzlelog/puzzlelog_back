package com.puzzlelog.api.dto.response.friend;

import com.puzzlelog.api.dao.entity.Friend;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendResponse {
    private String userId;
    private String friendId;

    public static FriendResponse from(Friend friend) {
        return FriendResponse.builder()
                .userId(friend.getUser().getUserId())
                .friendId(friend.getFriend().getUserId())
                .build();
    }
}