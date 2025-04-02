package com.puzzlelog.api.dto.response.invitation;

import java.time.Instant;

import com.puzzlelog.api.dao.document.Invitation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvitationSimpleResponse {
    private String invitationId;
    private String diaryDate;
    private String status;
    private String senderId;
    private int receiverCount;
    private Instant createdAt;

    public static InvitationSimpleResponse from(Invitation invitation) {
        return InvitationSimpleResponse.builder()
            .invitationId(invitation.getId())
            .diaryDate(invitation.getDiaryDate())
            .status(invitation.getStatus())
            .senderId(invitation.getSenderId())
            .receiverCount(invitation.getReceiverIds().size())
            .createdAt(invitation.getCreatedAt())
            .build();
    }
}
