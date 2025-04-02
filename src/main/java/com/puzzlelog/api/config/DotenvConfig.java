 package com.puzzlelog.api.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    static {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        System.setProperty("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL"));
        System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
        System.setProperty("SPRING_DATA_MONGODB_URI", dotenv.get("SPRING_DATA_MONGODB_URI"));
        System.setProperty("CLOUDINARY_URL", dotenv.get("CLOUDINARY_URL"));
        System.setProperty("SERVER_PORT", dotenv.get("SERVER_PORT", "8080"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));

        // 디버깅용 출력
        System.out.println("✅ [DotenvConfig] 환경 변수 로드 완료");
    }
}
