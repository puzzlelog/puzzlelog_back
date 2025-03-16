package com.puzzlelog.api.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    static {
        // ✅ `.env`에서 `SPRING_PROFILES_ACTIVE` 값을 먼저 가져오기
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        String activeProfile = dotenv.get("SPRING_PROFILES_ACTIVE", System.getenv("SPRING_PROFILES_ACTIVE"));

        if (activeProfile == null || activeProfile.isEmpty()) {
            activeProfile = System.getProperty("SPRING_PROFILES_ACTIVE", "local"); // 기본값: local
        }

        // ✅ 환경 변수 설정 (Docker 및 로컬 환경 공통)
        System.setProperty("SPRING_PROFILES_ACTIVE", activeProfile);
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        // ✅ 환경 변수 출력 (디버깅용)
        System.out.println("✅ [DotenvConfig] SPRING_PROFILES_ACTIVE = " + activeProfile);
        System.out.println("✅ [DotenvConfig] SPRING_DATASOURCE_URL = " + System.getProperty("SPRING_DATASOURCE_URL"));
        System.out.println("✅ [DotenvConfig] SPRING_DATASOURCE_USERNAME = " + System.getProperty("SPRING_DATASOURCE_USERNAME"));
    }
}
