package com.puzzlelog.api.dto.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.puzzlelog.api.dao.entity.User.Gender;
import com.puzzlelog.api.dao.entity.User.Role;
import com.puzzlelog.api.dao.entity.User.Status;

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
    private Gender gender;
    private Boolean isAlarm;
    private Status status;
    private Role role;
    
    // 날짜 형태
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