package com.puzzlelog.api.controller;

import com.puzzlelog.api.dto.response.common.ApiResponse;
import com.puzzlelog.api.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;
    private LocalDateTime startTime;

    @PostConstruct
    public void init() {
        this.startTime = LocalDateTime.now();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> home(HttpServletRequest request) {
        Map<String, Object> healthData = new LinkedHashMap<>();

        String host = request.getServerName();
        String environment = host.contains("localhost") || host.startsWith("127.") ? "dev" : "prod";

        Duration uptime = Duration.between(startTime, LocalDateTime.now());
        String uptimeStr = String.format("%d hours %d minutes", uptime.toHours(), uptime.toMinutesPart());

        Map<String, Map<String, String>> status = homeService.checkAll();

        healthData.put("timestamp", LocalDateTime.now());
        healthData.put("environment", environment);
        healthData.put("uptime", uptimeStr);
        healthData.put("databaseStatus", status.get("databaseStatus"));
        healthData.put("cloudinaryStatus", status.get("cloudinaryStatus"));

        return ResponseEntity.ok(
                ApiResponse.success(healthData, "PuzzleLog API 서버가 정상적으로 작동 중입니다.")
        );
    }
}