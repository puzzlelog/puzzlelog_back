package com.puzzlelog.api.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        String cloudinaryUrl = System.getenv("CLOUDINARY_URL");  // ✅ 환경 변수를 직접 가져오기

        if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
            throw new RuntimeException("❌ CLOUDINARY_URL 환경 변수가 설정되지 않았습니다.");
        }

        return new Cloudinary(cloudinaryUrl);
    }
}
