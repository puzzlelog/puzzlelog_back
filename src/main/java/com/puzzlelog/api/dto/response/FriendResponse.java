package com.puzzlelog.api.dto.response;

import com.puzzlelog.api.dao.entity.Friend;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendResponse {
    private int userId;
    private int friendId;

    public static FriendResponse from(Friend friend) {
        return FriendResponse.builder()
                .userId(friend.getUser().getId())
                .friendId(friend.getFriend().getId())
                .build();
    }
}
