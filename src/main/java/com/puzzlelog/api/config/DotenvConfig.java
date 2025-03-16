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
            activeProfile = System.getProperty("SPRING_PROFILES_ACTIVE", "prod"); // 기본값을 "prod"로 설정
        }

        // ✅ 환경 변수를 명확하게 설정
        System.setProperty("SPRING_PROFILES_ACTIVE", activeProfile);
        System.setProperty("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL", System.getenv("SPRING_DATASOURCE_URL")));
        System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME", System.getenv("SPRING_DATASOURCE_USERNAME")));
        System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD", System.getenv("SPRING_DATASOURCE_PASSWORD")));

        // ✅ 환경 변수 출력 (디버깅용)
        System.out.println("✅ [DotenvConfig] SPRING_PROFILES_ACTIVE = " + System.getProperty("SPRING_PROFILES_ACTIVE"));
        System.out.println("✅ [DotenvConfig] SPRING_DATASOURCE_URL = " + System.getProperty("SPRING_DATASOURCE_URL"));
        System.out.println("✅ [DotenvConfig] SPRING_DATASOURCE_USERNAME = " + System.getProperty("SPRING_DATASOURCE_USERNAME"));
    }
}
