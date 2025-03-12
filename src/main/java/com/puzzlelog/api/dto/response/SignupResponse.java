package com.puzzlelog.api.dto.response;

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
}