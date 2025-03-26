package com.puzzlelog.api.service;

import com.cloudinary.Cloudinary;
import com.mongodb.MongoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * 실제 서비스/DB 연결 상태를 점검하는 헬스체크 서비스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    private final DataSource dataSource;
    private final MongoTemplate mongoTemplate;
    private final Cloudinary cloudinary;

    /**
     * MySQL, MongoDB, Cloudinary 상태를 점검하여 결과 반환
     *
     * @return 상태별 Map 결과 (OK 또는 FAIL)
     */
    public Map<String, Map<String, String>> checkAll() {
        Map<String, Map<String, String>> result = new HashMap<>();

        result.put("databaseStatus", Map.of(
            "MySQL", checkMySQL() ? "OK" : "FAIL",
            "MongoDB", checkMongoDB() ? "OK" : "FAIL"
        ));

        result.put("cloudinaryStatus", Map.of(
            "Cloudinary", checkCloudinary() ? "OK" : "FAIL"
        ));

        return result;
    }

    private boolean checkMySQL() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(1);
        } catch (Exception e) {
            log.warn("❌ MySQL 연결 실패", e);
            return false;
        }
    }

    private boolean checkMongoDB() {
        try {
            mongoTemplate.executeCommand("{ ping: 1 }");
            return true;
        } catch (MongoException e) {
            log.warn("❌ MongoDB 연결 실패", e);
            return false;
        }
    }

    private boolean checkCloudinary() {
        try {
            cloudinary.api().ping(Map.of());
            return true;
        } catch (Exception e) {
            log.warn("❌ Cloudinary 연결 실패", e);
            return false;
        }
    }
}
