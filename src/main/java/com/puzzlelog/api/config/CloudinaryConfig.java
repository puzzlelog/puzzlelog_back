package com.puzzlelog.api.config;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        // ✅ `.env`에서 환경 변수를 먼저 가져오기
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        String cloudinaryUrl = dotenv.get("CLOUDINARY_URL", System.getenv("CLOUDINARY_URL"));

        if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
            throw new RuntimeException("❌ CLOUDINARY_URL 환경 변수가 설정되지 않았습니다.");
        }

        return new Cloudinary(cloudinaryUrl);
    }
}