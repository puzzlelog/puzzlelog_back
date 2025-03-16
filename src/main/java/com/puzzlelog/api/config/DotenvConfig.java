package com.puzzlelog.api.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    static {
        // ✅ 현재 프로젝트 루트에서 `.env` 파일 로드
        String projectRoot = System.getProperty("user.dir"); // 현재 실행 중인 프로젝트의 루트 디렉토리

        if (System.getProperty("SPRING_PROFILES_ACTIVE") == null &&
            System.getenv("SPRING_PROFILES_ACTIVE") == null) {

            Dotenv dotenv = Dotenv.configure()
                    .directory(projectRoot) // 프로젝트 루트에서 `.env` 로드
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue()); // 환경 변수 설정
            });

            System.out.println("✅ [Dotenv] Loaded .env file from: " + projectRoot);
        } else {
            System.out.println("⚠️ [Dotenv] Skipping .env loading - SPRING_PROFILES_ACTIVE already set.");
        }

        // ✅ 환경 변수 확인 로그 (MySQL 연결 정보)
        System.out.println("✅ SPRING_PROFILES_ACTIVE = " + System.getProperty("SPRING_PROFILES_ACTIVE"));
        System.out.println("✅ SPRING_DATASOURCE_URL = " + System.getProperty("SPRING_DATASOURCE_URL"));
        System.out.println("✅ SPRING_DATASOURCE_USERNAME = " + System.getProperty("SPRING_DATASOURCE_USERNAME"));
        System.out.println("✅ SPRING_DATASOURCE_PASSWORD = " + System.getProperty("SPRING_DATASOURCE_PASSWORD"));
    }
}
