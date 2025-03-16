package com.puzzlelog.api.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        String cloudinaryUrl;

        if ("prod".equals(System.getProperty("SPRING_PROFILES_ACTIVE")) || 
            "prod".equals(System.getenv("SPRING_PROFILES_ACTIVE"))) {
            // ✅ EC2 (prod)에서는 `.env`를 사용하지 않고 환경 변수에서 직접 가져옴
            cloudinaryUrl = System.getenv("CLOUDINARY_URL");
        } else {
            // ✅ 로컬(local)에서는 `.env` 파일에서 값을 가져옴
            cloudinaryUrl = io.github.cdimascio.dotenv.Dotenv.load().get("CLOUDINARY_URL");
        }

        if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
            throw new RuntimeException("❌ CLOUDINARY_URL 환경 변수가 설정되지 않았습니다.");
        }

        return new Cloudinary(cloudinaryUrl);
    }
}