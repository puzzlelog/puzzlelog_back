package com.puzzlelog.api.dto.response.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupResponse {
    private Integer id;
    private String userId;
    private String email;
    private String profileImg;
}