package com.puzzlelog.api.dto.request.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    private String userId;
    private String userPwd;
}