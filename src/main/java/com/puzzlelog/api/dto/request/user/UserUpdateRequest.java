package com.puzzlelog.api.dto.request.user;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequest {
    private String userPwd;    
    private String nickname;   
    private String birthDate;  
    private String gender;     
    private Boolean isAlarm;   
    private String profileImg; 
    private String status;     
    private String role;       

    @JsonIgnore
    private final Set<String> fieldsSet = new HashSet<>();

    @JsonSetter("userPwd")
    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
        fieldsSet.add("userPwd");
    }

    @JsonSetter("nickname")
    public void setNickname(String nickname) {
        this.nickname = nickname;
        fieldsSet.add("nickname");
    }

    @JsonSetter("birthDate")
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
        fieldsSet.add("birthDate");
    }

    @JsonSetter("gender")
    public void setGender(String gender) {
        this.gender = gender;
        fieldsSet.add("gender");
    }

    @JsonSetter("isAlarm")
    public void setIsAlarm(Boolean isAlarm) {
        this.isAlarm = isAlarm;
        fieldsSet.add("isAlarm");
    }

    @JsonSetter("profileImg")
    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
        fieldsSet.add("profileImg");
    }

    @JsonSetter("status")
    public void setStatus(String status) {
        this.status = status;
        fieldsSet.add("status");
    }

    @JsonSetter("role")
    public void setRole(String role) {
        this.role = role;
        fieldsSet.add("role");
    }

    public boolean hasUserPwd() { return fieldsSet.contains("userPwd"); }
    public boolean hasNickname() { return fieldsSet.contains("nickname"); }
    public boolean hasBirthDate() { return fieldsSet.contains("birthDate"); }
    public boolean hasGender() { return fieldsSet.contains("gender"); }
    public boolean hasIsAlarm() { return fieldsSet.contains("isAlarm"); }
    public boolean hasProfileImg() { return fieldsSet.contains("profileImg"); }
    public boolean hasStatus() { return fieldsSet.contains("status"); }
    public boolean hasRole() { return fieldsSet.contains("role"); }

    public boolean isEmpty() {
        return fieldsSet.isEmpty();
    }
}
