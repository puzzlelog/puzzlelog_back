package com.puzzlelog.api.dto.response.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Integer id; 
    private String userId;
    private String role;
    private String token;
}