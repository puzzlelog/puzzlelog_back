package com.puzzlelog.api.dto.request.user;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequest {
    private String email;
    private String userId;
    private String nickname;
    private String gender;  // "MALE", "FEMALE"
    private Boolean isAlarm;
    private String status;  // "ACTIVE", "DELETED", "BANNED"
    private String role;    // "USER", "ADMIN"
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAtFrom;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAtTo;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDateFrom;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDateTo;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate lastLoginFrom;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate lastLoginTo;
    
    public boolean hasNoCondition() {
        return email == null &&
               userId == null &&
               nickname == null &&
               createdAtFrom == null &&
               createdAtTo == null &&
               birthDateFrom == null &&
               birthDateTo == null &&
               gender == null &&
               isAlarm == null &&
               status == null &&
               role == null &&
               lastLoginFrom == null &&
               lastLoginTo == null;
    }
}
