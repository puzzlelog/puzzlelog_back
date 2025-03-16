package com.puzzlelog.api.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public com.cloudinary.Cloudinary cloudinary() {
        // ✅ `.env` 파일이 존재하는 경우만 로드
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))  // 현재 프로젝트 디렉토리에서 로드
                .ignoreIfMissing()
                .load();

        String cloudinaryUrl = dotenv.get("CLOUDINARY_URL", "");
        if (cloudinaryUrl.isEmpty()) {
            throw new RuntimeException("CLOUDINARY_URL 환경 변수가 설정되지 않았습니다.");
        }

        return new com.cloudinary.Cloudinary(cloudinaryUrl);
    }
}
