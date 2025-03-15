package com.puzzlelog.api.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {
    
    @GetMapping
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "PuzzleLog API 서버가 정상적으로 작동 중입니다.");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}
