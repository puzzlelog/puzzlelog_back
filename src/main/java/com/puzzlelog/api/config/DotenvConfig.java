package com.puzzlelog.api.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    static {
        // ✅ `SPRING_PROFILES_ACTIVE` 환경 변수 가져오기
        String activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
        if (activeProfile == null) {
            activeProfile = System.getProperty("SPRING_PROFILES_ACTIVE", "local"); // 기본값은 "local"
        }

        // ✅ EC2 (prod) 환경에서는 `.env`를 로드하지 않음
        if ("prod".equals(activeProfile)) {
            System.out.println("⚠️ [Dotenv] EC2(prod) 환경 - .env 로딩 생략, 환경 변수 직접 사용");
        } else {
            // ✅ 로컬(local) 환경에서는 `.env` 파일에서 값 로드
            String projectRoot = System.getProperty("user.dir"); // 현재 프로젝트 루트 디렉토리
            Dotenv dotenv = Dotenv.configure()
                    .directory(projectRoot)
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });

            System.out.println("✅ [Dotenv] Loaded .env file from: " + projectRoot);
        }

        // ✅ 환경 변수 확인 로그 (EC2 및 로컬 환경 모두 출력)
        System.out.println("✅ SPRING_PROFILES_ACTIVE = " + activeProfile);
        System.out.println("✅ SPRING_DATASOURCE_URL = " + System.getenv("SPRING_DATASOURCE_URL"));
        System.out.println("✅ SPRING_DATASOURCE_USERNAME = " + System.getenv("SPRING_DATASOURCE_USERNAME"));
    }
}
